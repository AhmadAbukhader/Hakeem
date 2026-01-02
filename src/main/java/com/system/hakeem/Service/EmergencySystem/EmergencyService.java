package com.system.hakeem.Service.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.EmergencyRequestDto;
import com.system.hakeem.Dto.EmergencySystem.EmergencyRequestResponseDto;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.UserLocationDto;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Model.EmergencySystem.AmbulanceStatus;
import com.system.hakeem.Model.EmergencySystem.EmergencyRequest;
import com.system.hakeem.Model.EmergencySystem.EmergencyRequestStatus;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.EmergencySystem.AmbulanceRepository;
import com.system.hakeem.Repository.EmergencySystem.EmergencyRequestRepository;
import com.system.hakeem.Exceptions.BadRequestException;
import com.system.hakeem.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EmergencyService {

        private static final Logger logger = LoggerFactory.getLogger(EmergencyService.class);

        private final EmergencyRequestRepository emergencyRequestRepository;
        private final AmbulanceRepository ambulanceRepository;
        private final AmbulanceService ambulanceService;
        private final GeometryFactory geometryFactory;
        private final SimpMessagingTemplate messagingTemplate;
        private final VoiceCallService voiceCallService;

        /**
         * Creates a new emergency request, finds closest available ambulance,
         * assigns it, and sends patient location to paramedic via WebSocket
         */
        @Transactional
        public EmergencyRequestResponseDto createEmergencyRequest(EmergencyRequestDto requestDto) {
                // Get authenticated patient
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User patient = (User) auth.getPrincipal();

                // Check if patient has an active emergency request
                List<EmergencyRequestStatus> activeStatuses = Arrays.asList(
                                EmergencyRequestStatus.PENDING,
                                EmergencyRequestStatus.ASSIGNED,
                                EmergencyRequestStatus.IN_PROGRESS);
                EmergencyRequest existingRequest = emergencyRequestRepository
                                .findByPatientAndStatusIn(patient, activeStatuses)
                                .orElse(null);

                if (existingRequest != null) {
                        throw new BadRequestException(
                                        "You already have an active emergency request. Request ID: "
                                                        + existingRequest.getRequestId());
                }

                // Validate coordinates
                validateCoordinates(requestDto.getLatitude(), requestDto.getLongitude());

                // Create patient location point
                Point patientLocationPoint = geometryFactory
                                .createPoint(new Coordinate(requestDto.getLongitude(), requestDto.getLatitude()));
                patientLocationPoint.setSRID(4326);

                // Find closest available ambulance
                Ambulance closestAmbulance = ambulanceService.findClosestAvailableAmbulance(
                                requestDto.getLatitude(), requestDto.getLongitude());

                if (closestAmbulance == null) {
                        throw new NotFoundException("No available ambulances found. Please try again later.");
                }

                // Create emergency request
                EmergencyRequest emergencyRequest = EmergencyRequest.builder()
                                .patient(patient)
                                .ambulance(closestAmbulance)
                                .patientLocation(patientLocationPoint)
                                .status(EmergencyRequestStatus.ASSIGNED)
                                .createdAt(LocalDateTime.now())
                                .assignedAt(LocalDateTime.now())
                                .notes(requestDto.getNotes())
                                .build();

                emergencyRequest = emergencyRequestRepository.save(emergencyRequest);

                // Update ambulance status to BUSY
                ambulanceService.updateAmbulanceStatus(closestAmbulance.getAmbulanceId(), AmbulanceStatus.BUSY);

                // Send patient location to paramedic via WebSocket
                sendPatientLocationToParamedic(closestAmbulance, patient, requestDto.getLatitude(),
                                requestDto.getLongitude());

                // Initiate voice call to notify paramedic about the emergency request
                initiateParamedicVoiceCall(closestAmbulance, patient, emergencyRequest.getRequestId());

                logger.info("Emergency request created: requestId={}, patientId={}, ambulanceId={}",
                                emergencyRequest.getRequestId(), patient.getId(), closestAmbulance.getAmbulanceId());

                // Build and return response
                return buildEmergencyRequestResponse(emergencyRequest);
        }

        /**
         * Gets the current emergency request for the authenticated patient
         */
        public EmergencyRequestResponseDto getMyEmergencyRequest() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User patient = (User) auth.getPrincipal();

                EmergencyRequest request = emergencyRequestRepository
                                .findByPatientAndStatusIn(patient,
                                                Arrays.asList(EmergencyRequestStatus.PENDING,
                                                                EmergencyRequestStatus.ASSIGNED,
                                                                EmergencyRequestStatus.IN_PROGRESS))
                                .orElseThrow(() -> new NotFoundException("No active emergency request found"));

                return buildEmergencyRequestResponse(request);
        }

        /**
         * Gets emergency requests for the authenticated paramedic
         */
        public List<EmergencyRequestResponseDto> getMyParamedicEmergencyRequests() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User paramedic = (User) auth.getPrincipal();

                Ambulance ambulance = ambulanceRepository.findByParamedic(paramedic)
                                .orElseThrow(() -> new NotFoundException("No ambulance assigned to paramedic"));

                List<EmergencyRequest> requests = emergencyRequestRepository.findByAmbulance(ambulance);

                return requests.stream()
                                .map(this::buildEmergencyRequestResponse)
                                .toList();
        }

        /**
         * Updates emergency request status
         */
        @Transactional
        public EmergencyRequestResponseDto updateEmergencyRequestStatus(Integer requestId,
                        EmergencyRequestStatus newStatus) {
                EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Emergency request not found with id: " + requestId));

                EmergencyRequestStatus oldStatus = request.getStatus();
                request.setStatus(newStatus);

                if (newStatus == EmergencyRequestStatus.COMPLETED || newStatus == EmergencyRequestStatus.CANCELLED) {
                        request.setCompletedAt(LocalDateTime.now());
                        // Free up the ambulance
                        if (request.getAmbulance() != null) {
                                ambulanceService.updateAmbulanceStatus(request.getAmbulance().getAmbulanceId(),
                                                AmbulanceStatus.AVAILABLE);
                        }
                }

                request = emergencyRequestRepository.save(request);

                logger.info("Emergency request status updated: requestId={}, oldStatus={}, newStatus={}",
                                requestId, oldStatus, newStatus);

                return buildEmergencyRequestResponse(request);
        }

        /**
         * Sends patient location to paramedic via WebSocket
         */
        private void sendPatientLocationToParamedic(Ambulance ambulance, User patient, double latitude,
                        double longitude) {
                try {
                        UserLocationDto patientLocation = UserLocationDto.builder()
                                        .userId(patient.getId())
                                        .latitude(latitude)
                                        .longitude(longitude)
                                        .build();

                        // Send to ambulance-specific topic:
                        // /topic/ambulance/{ambulanceId}/patient-location
                        String destination = "/topic/ambulance/" + ambulance.getAmbulanceId() + "/patient-location";
                        messagingTemplate.convertAndSend(destination, patientLocation);

                        logger.debug("Sent patient location to paramedic: ambulanceId={}, patientId={}, destination={}",
                                        ambulance.getAmbulanceId(), patient.getId(), destination);
                } catch (Exception e) {
                        logger.error("Error sending patient location to paramedic: {}", e.getMessage(), e);
                        // Don't throw exception - WebSocket failure shouldn't break the request
                        // creation
                }
        }

        /**
         * Initiates a voice call to notify paramedic about the emergency request.
         * Uses Twilio API to call the paramedic's phone and play an emergency
         * notification.
         */
        private void initiateParamedicVoiceCall(Ambulance ambulance, User patient, Integer requestId) {
                try {
                        User paramedic = ambulance.getParamedic();
                        if (paramedic == null) {
                                logger.warn("Cannot send voice call: no paramedic assigned to ambulanceId={}",
                                                ambulance.getAmbulanceId());
                                return;
                        }

                        Long phoneNumber = paramedic.getPhoneNumber();
                        if (phoneNumber == null) {
                                logger.warn("Cannot send voice call: paramedic phone number not available. paramedicId={}, ambulanceId={}",
                                                paramedic.getId(), ambulance.getAmbulanceId());
                                return;
                        }

                        String callSid = voiceCallService.initiateEmergencyCall(
                                        phoneNumber.toString(),
                                        patient.getName() != null ? patient.getName() : "Unknown Patient",
                                        requestId);

                        if (callSid != null) {
                                logger.info("Voice call notification sent to paramedic: paramedicId={}, phone={}, callSid={}, requestId={}",
                                                paramedic.getId(), phoneNumber, callSid, requestId);
                        } else {
                                logger.warn("Voice call failed but emergency request created successfully: paramedicId={}, requestId={}",
                                                paramedic.getId(), requestId);
                        }
                } catch (Exception e) {
                        // Don't fail the emergency request if voice call fails
                        logger.error("Failed to send voice call notification, but emergency request created successfully: requestId={}, error={}",
                                        requestId, e.getMessage(), e);
                }
        }

        /**
         * Builds EmergencyRequestResponseDto from EmergencyRequest entity
         */
        private EmergencyRequestResponseDto buildEmergencyRequestResponse(EmergencyRequest request) {
                return EmergencyRequestResponseDto.builder()
                                .requestId(request.getRequestId())
                                .patientId(request.getPatient().getId())
                                .patientName(request.getPatient().getName())
                                .ambulanceId(request.getAmbulance() != null ? request.getAmbulance().getAmbulanceId()
                                                : null)
                                .plateNumber(request.getAmbulance() != null ? request.getAmbulance().getPlateNumber()
                                                : null)
                                .paramedicId(request.getAmbulance() != null
                                                && request.getAmbulance().getParamedic() != null
                                                                ? request.getAmbulance().getParamedic().getId()
                                                                : null)
                                .paramedicName(request.getAmbulance() != null
                                                && request.getAmbulance().getParamedic() != null
                                                                ? request.getAmbulance().getParamedic().getName()
                                                                : null)
                                .patientLatitude(request.getPatientLocation() != null
                                                ? request.getPatientLocation().getY()
                                                : 0)
                                .patientLongitude(request.getPatientLocation() != null
                                                ? request.getPatientLocation().getX()
                                                : 0)
                                .status(request.getStatus())
                                .createdAt(request.getCreatedAt())
                                .assignedAt(request.getAssignedAt())
                                .notes(request.getNotes())
                                .build();
        }

        /**
         * Validates GPS coordinates are within valid ranges
         */
        private void validateCoordinates(double latitude, double longitude) {
                if (latitude < -90 || latitude > 90) {
                        throw new IllegalArgumentException(
                                        String.format("Latitude must be between -90 and 90. Received: %f", latitude));
                }
                if (longitude < -180 || longitude > 180) {
                        throw new IllegalArgumentException(
                                        String.format("Longitude must be between -180 and 180. Received: %f",
                                                        longitude));
                }
        }
}
