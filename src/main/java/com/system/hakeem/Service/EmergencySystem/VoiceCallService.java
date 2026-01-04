package com.system.hakeem.Service.EmergencySystem;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service for initiating voice calls to paramedics when an emergency request is
 * created.
 * Uses Twilio API to make automated voice calls that notify paramedics about
 * new emergencies.
 */
@Service
public class VoiceCallService {

    private static final Logger logger = LoggerFactory.getLogger(VoiceCallService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Value("${twilio.voice.url}")
    private String voiceUrl;

    @Value("${twilio.enabled:true}")
    private boolean twilioEnabled;

    private boolean initialized = false;

    /**
     * Initializes Twilio client when the application starts
     */
    @PostConstruct
    public void initializeTwilio() {
        if (!twilioEnabled) {
            logger.info("Twilio is disabled. Voice call notifications will not be sent.");
            return;
        }

        try {
            Twilio.init(accountSid, authToken);
            initialized = true;
            logger.info("Twilio initialized successfully with Account SID: {}...",
                    accountSid.substring(0, Math.min(10, accountSid.length())));
        } catch (Exception e) {
            logger.error("Failed to initialize Twilio: {}", e.getMessage(), e);
            initialized = false;
        }
    }

    /**
     * Initiates a voice call to notify paramedic about an emergency request.
     * This is called automatically when an emergency request is created and
     * ambulance is assigned.
     *
     * @param paramedicPhoneNumber Phone number of the paramedic (e.g.,
     *                             +970591234567 or 0591234567)
     * @param patientName          Name of the patient requesting emergency
     * @param requestId            Emergency request ID
     * @return Call SID if successful, null otherwise
     */
    public String initiateEmergencyCall(String paramedicPhoneNumber, String patientName, Integer requestId) {
        if (!twilioEnabled) {
            logger.info("Twilio is disabled. Skipping voice call for requestId={}", requestId);
            return null;
        }

        if (!initialized) {
            logger.warn("Twilio not initialized. Attempting to initialize...");
            initializeTwilio();
            if (!initialized) {
                logger.error("Failed to initialize Twilio. Cannot make voice call.");
                return null;
            }
        }

        try {
            // Format phone number (ensure it starts with +)
            String formattedPhone = formatPhoneNumber(paramedicPhoneNumber);

            // Encode parameters for URL
            String encodedPatientName = URLEncoder.encode(
                    patientName != null ? patientName : "Unknown",
                    StandardCharsets.UTF_8);

            // Build the TwiML URL with parameters
            String twimlUrl = String.format("%s?patientName=%s&requestId=%d",
                    voiceUrl, encodedPatientName, requestId);

            logger.info("Initiating emergency call to paramedic: to={}, from={}, requestId={}",
                    formattedPhone, twilioPhoneNumber, requestId);

            // Create call with TwiML URL that will play the message
            Call call = Call.creator(
                    new PhoneNumber(formattedPhone), // To: Paramedic's phone
                    new PhoneNumber(twilioPhoneNumber), // From: Your Twilio number
                    new URI(twimlUrl) // TwiML URL with parameters
            ).create();

            logger.info("Voice call initiated successfully: callSid={}, to={}, requestId={}",
                    call.getSid(), formattedPhone, requestId);

            return call.getSid();

        } catch (Exception e) {
            logger.error("Failed to initiate voice call to paramedic: phone={}, requestId={}, error={}",
                    paramedicPhoneNumber, requestId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Formats phone number to include country code if missing.
     * Detects US numbers (starting with 1, 11 digits) and formats as +1XXXXXXXXXX.
     * Assumes Palestinian numbers (+970) for other numbers if no country code provided.
     *
     * @param phoneNumber The phone number to format
     * @return Formatted phone number with country code
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        // Remove any spaces, dashes, or parentheses
        String cleaned = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");

        // If already starts with +, return as is
        if (cleaned.startsWith("+")) {
            return cleaned;
        }

        // Check if it's a US number (starts with 1 and has 11 digits: 1XXXXXXXXXX)
        if (cleaned.startsWith("1") && cleaned.length() == 11) {
            return "+" + cleaned; // US number: +1XXXXXXXXXX
        }

        // Check if it already starts with country code (970 for Palestine)
        if (cleaned.startsWith("970") && cleaned.length() >= 12) {
            // Already has Palestinian country code, just add +
            return "+" + cleaned;
        }

        // Check if it starts with 0 (local format like 0591234567)
        if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1); // Remove leading 0
        }

        // Check if it starts with 97 (but not 970) - might be incorrectly formatted
        if (cleaned.startsWith("97") && !cleaned.startsWith("970")) {
            // Remove the incorrect 97 prefix and add correct 970
            cleaned = cleaned.substring(2);
        }

        // Default: Assume Palestinian number and add country code
        cleaned = "+970" + cleaned;

        return cleaned;
    }

    /**
     * Checks if Twilio service is properly initialized and ready
     *
     * @return true if Twilio is initialized and enabled
     */
    public boolean isReady() {
        return twilioEnabled && initialized;
    }
}
