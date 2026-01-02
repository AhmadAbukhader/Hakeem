package com.system.hakeem.Controller.EmergencySystem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling Twilio voice call webhooks.
 * This controller provides endpoints that Twilio calls to get TwiML
 * instructions
 * for playing voice messages to paramedics during emergency notifications.
 */
@RestController
@RequestMapping("/voice")
@Tag(name = "Voice Call Webhooks", description = "Twilio webhook endpoints for emergency voice call notifications. These endpoints are called by Twilio, not by users directly.")
public class VoiceController {

    private static final Logger logger = LoggerFactory.getLogger(VoiceController.class);

    /**
     * TwiML endpoint that Twilio calls to get the voice message to play.
     * 
     * Flow:
     * 1. Patient creates emergency request (POST /emergency/request)
     * 2. System assigns closest ambulance
     * 3. VoiceCallService initiates call to paramedic via Twilio API
     * 4. Twilio calls paramedic's phone
     * 5. When paramedic answers, Twilio calls THIS endpoint to get the message
     * 6. This endpoint returns TwiML XML with the voice message
     * 7. Twilio plays the message to the paramedic
     *
     * @param patientName Name of the patient (passed from VoiceCallService)
     * @param requestId   Emergency request ID (passed from VoiceCallService)
     * @return TwiML XML response that Twilio will execute
     */
    @GetMapping(value = "/emergency-notification", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Twilio TwiML webhook for emergency notifications", description = "This endpoint is called by Twilio when a paramedic answers an emergency notification call. "
            +
            "Returns TwiML XML that instructs Twilio to play an emergency alert message. " +
            "This endpoint should NOT be called directly by users - it's for Twilio webhooks only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TwiML response returned successfully")
    })
    public ResponseEntity<String> emergencyNotification(
            @Parameter(description = "Patient name for the emergency") @RequestParam(required = false, defaultValue = "Unknown") String patientName,
            @Parameter(description = "Emergency request ID") @RequestParam Integer requestId) {

        logger.info("TwiML requested for emergency notification: patientName={}, requestId={}",
                patientName, requestId);

        // TwiML XML that Twilio will execute
        // This plays a voice message to the paramedic when they answer the call
        String twiml = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<Response>" +
                        "<Say voice=\"alice\" language=\"en-US\">" +
                        "Emergency alert. You have been assigned a new emergency request. " +
                        "Patient name: %s. Request ID: %d. " +
                        "Please check your application immediately for patient location and details. " +
                        "This call will now end." +
                        "</Say>" +
                        "</Response>",
                escapeXml(patientName != null ? patientName : "Unknown"),
                requestId);

        logger.debug("Returning TwiML response for requestId={}", requestId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(twiml);
    }

    /**
     * Webhook endpoint for call status updates from Twilio.
     * Twilio can be configured to send status updates (initiated, ringing,
     * answered, completed, etc.)
     *
     * @param CallSid    Unique identifier for the call
     * @param CallStatus Current status of the call
     * @param To         Phone number that was called
     * @param From       Phone number that made the call (Twilio number)
     * @return Empty OK response
     */
    @PostMapping("/call-status")
    @Operation(summary = "Twilio call status webhook", description = "Receives call status updates from Twilio. Used for logging and monitoring call delivery.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status update received")
    })
    public ResponseEntity<Void> callStatusWebhook(
            @RequestParam(required = false) String CallSid,
            @RequestParam(required = false) String CallStatus,
            @RequestParam(required = false) String To,
            @RequestParam(required = false) String From) {

        logger.info("Call status update: CallSid={}, Status={}, To={}, From={}",
                CallSid, CallStatus, To, From);

        // You can extend this to save call status to database if needed
        // For now, we just log it

        return ResponseEntity.ok().build();
    }

    /**
     * Escapes special XML characters to prevent XML injection
     *
     * @param input The string to escape
     * @return Escaped string safe for XML
     */
    private String escapeXml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
