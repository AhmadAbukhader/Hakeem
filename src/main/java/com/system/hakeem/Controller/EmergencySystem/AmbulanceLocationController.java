package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.UserLocationDto;
import com.system.hakeem.Exceptions.BadRequestException;
import com.system.hakeem.Exceptions.NotFoundException;
import com.system.hakeem.Model.EmergencySystem.EmergencyRequestStatus;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.EmergencySystem.EmergencyRequestRepository;
import com.system.hakeem.Service.EmergencySystem.AmbulanceService;
import com.system.hakeem.Service.UserManagement.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/websocket")
@Tag(name = "WebSocket - Ambulance Location", description = "WebSocket endpoints for real-time ambulance location tracking. Connect to ws://host:port/ws and use STOMP protocol.")
public class AmbulanceLocationController {

        private static final Logger logger = LoggerFactory.getLogger(AmbulanceLocationController.class);
        private final AmbulanceService ambulanceService;
        private final UserService userService;
        private final EmergencyRequestRepository emergencyRequestRepository;
        private final SimpMessagingTemplate messagingTemplate;

        /**
         * REST endpoint to get WebSocket API documentation.
         * This endpoint appears in Swagger UI to document the WebSocket endpoints.
         */
        @GetMapping("/ambulance/documentation")
        @Operation(summary = "Get WebSocket API documentation for ambulance location tracking", description = "Returns documentation for WebSocket endpoints used for real-time ambulance location tracking. "
                        +
                        "This is a REST endpoint that documents the WebSocket API usage.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "WebSocket API documentation retrieved successfully")
        })
        public ResponseEntity<Map<String, Object>> getWebSocketDocumentation() {
                Map<String, Object> documentation = new HashMap<>();

                // WebSocket Connection Info
                Map<String, String> connection = new HashMap<>();
                connection.put("endpoint", "ws://host:port/ws");
                connection.put("protocol", "STOMP");
                connection.put("fallback", "SockJS (if WebSocket not supported)");
                documentation.put("connection", connection);

                // Paramedic Endpoint (Sending Location Updates)
                Map<String, Object> paramedicEndpoint = new HashMap<>();
                paramedicEndpoint.put("destination", "/app/ambulance/updateLocation");
                paramedicEndpoint.put("method", "SEND (STOMP)");
                paramedicEndpoint.put("description", "Paramedic sends ambulance location updates");
                paramedicEndpoint.put("payload", Map.of(
                                "ambulanceId", "int (required)",
                                "latitude", "double (required, -90 to 90)",
                                "longitude", "double (required, -180 to 180)",
                                "speed", "double (optional, 0 to 300 km/h)",
                                "direction", "double (optional, 0 to 360 degrees)"));
                paramedicEndpoint.put("example", Map.of(
                                "ambulanceId", 1,
                                "latitude", 31.9522,
                                "longitude", 35.2332,
                                "speed", 60.0,
                                "direction", 90.0));
                documentation.put("paramedicEndpoint", paramedicEndpoint);

                // Patient Endpoint (Receiving Location Updates)
                Map<String, Object> patientEndpoint = new HashMap<>();
                patientEndpoint.put("destination", "/topic/ambulance/{ambulanceId}/location");
                patientEndpoint.put("method", "SUBSCRIBE (STOMP)");
                patientEndpoint.put("description",
                                "Patient subscribes to receive location updates for a specific ambulance");
                patientEndpoint.put("note",
                                "Replace {ambulanceId} with the actual ambulance ID assigned to the patient");
                patientEndpoint.put("example", "/topic/ambulance/1/location");
                patientEndpoint.put("response", Map.of(
                                "ambulanceId", "int",
                                "latitude", "double",
                                "longitude", "double",
                                "speed", "double",
                                "direction", "double"));
                documentation.put("patientEndpoint", patientEndpoint);

                // Paramedic Endpoint (Receiving Patient Location Updates)
                Map<String, Object> paramedicPatientLocationEndpoint = new HashMap<>();
                paramedicPatientLocationEndpoint.put("destination", "/topic/ambulance/{ambulanceId}/patient-location");
                paramedicPatientLocationEndpoint.put("method", "SUBSCRIBE (STOMP)");
                paramedicPatientLocationEndpoint.put("description",
                                "Paramedic subscribes to receive patient location updates for their assigned ambulance");
                paramedicPatientLocationEndpoint.put("note",
                                "Automatically sent when patient creates emergency request. Patient can also send updates via /app/patient/updateLocation");
                paramedicPatientLocationEndpoint.put("example", "/topic/ambulance/1/patient-location");
                paramedicPatientLocationEndpoint.put("response", Map.of(
                                "userId", "int",
                                "latitude", "double",
                                "longitude", "double"));
                documentation.put("paramedicPatientLocationEndpoint", paramedicPatientLocationEndpoint);

                // Patient Endpoint (Sending Location Updates)
                Map<String, Object> patientLocationUpdateEndpoint = new HashMap<>();
                patientLocationUpdateEndpoint.put("destination", "/app/patient/updateLocation");
                patientLocationUpdateEndpoint.put("method", "SEND (STOMP)");
                patientLocationUpdateEndpoint.put("description",
                                "Patient sends their location updates during emergency");
                patientLocationUpdateEndpoint.put("payload", Map.of(
                                "latitude", "double (required, -90 to 90)",
                                "longitude", "double (required, -180 to 180)"));
                patientLocationUpdateEndpoint.put("example", Map.of(
                                "latitude", 31.9522,
                                "longitude", 35.2332));
                documentation.put("patientLocationUpdateEndpoint", patientLocationUpdateEndpoint);

                // Usage Flow
                Map<String, String> flow = new HashMap<>();
                flow.put("step1", "1. Patient creates emergency request via POST /emergency/request");
                flow.put("step2",
                                "2. System finds closest ambulance and sends patient location to paramedic via WebSocket");
                flow.put("step3", "3. Connect to WebSocket: ws://host:port/ws");
                flow.put("step4", "4. Paramedic subscribes to: /topic/ambulance/{ambulanceId}/patient-location");
                flow.put("step5", "5. Patient subscribes to: /topic/ambulance/{ambulanceId}/location");
                flow.put("step6", "6. Paramedic sends ambulance location updates to: /app/ambulance/updateLocation");
                flow.put("step7", "7. Patient receives real-time ambulance location updates");
                flow.put("step8", "8. Patient can send location updates to: /app/patient/updateLocation (optional)");
                documentation.put("usageFlow", flow);

                // Important Notes
                Map<String, String> notes = new HashMap<>();
                notes.put("privacy",
                                "Each ambulance has its own topic, so patients only receive updates for their assigned ambulance");
                notes.put("scalability",
                                "No conflicts between patients - each subscribes to their specific ambulance topic");
                notes.put("realTime", "Updates are broadcasted immediately when paramedic sends location data");
                documentation.put("notes", notes);

                return ResponseEntity.ok(documentation);
        }

        /**
         * WebSocket endpoint for paramedics to update ambulance location.
         * Sends location update to ambulance-specific topic so only subscribed patients
         * receive it.
         * 
         * Paramedic sends to: /app/ambulance/updateLocation
         * Patient subscribes to: /topic/ambulance/{ambulanceId}/location
         */
        @MessageMapping("/ambulance/updateLocation")
        @Operation(summary = "Update ambulance location via WebSocket", description = "WebSocket endpoint (STOMP protocol) for paramedics to send real-time ambulance location updates. "
                        +
                        "Connect to ws://host:port/ws, then send messages to /app/ambulance/updateLocation. " +
                        "Location updates are broadcasted to /topic/ambulance/{ambulanceId}/location for subscribed patients.", tags = {
                                        "WebSocket - Ambulance Location" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Location update processed and broadcasted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AmbulanceLocationDto.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request - Invalid location data or validation failed"),
                        @ApiResponse(responseCode = "404", description = "Ambulance not found")
        })
        public void updateAmbulanceLocation(AmbulanceLocationDto location) {
                if (location == null) {
                        logger.error("Received null location update via WebSocket");
                        throw new IllegalArgumentException("Location data cannot be null");
                }

                try {
                        // Update database with the new ambulance location
                        ambulanceService.updateAmbulanceLocation(location);

                        // Send to ambulance-specific topic: /topic/ambulance/{ambulanceId}/location
                        // Only patients subscribed to this specific ambulance will receive the update
                        String destination = "/topic/ambulance/" + location.getAmbulanceId() + "/location";
                        messagingTemplate.convertAndSend(destination, location);

                        logger.debug("WebSocket location update successful for ambulanceId={}, sent to topic={}",
                                        location.getAmbulanceId(), destination);
                } catch (IllegalArgumentException | BadRequestException e) {
                        // Validation errors - log and rethrow to prevent invalid data propagation
                        logger.warn("Validation error in WebSocket location update for ambulanceId={}: {}",
                                        location.getAmbulanceId(), e.getMessage());
                        throw e; // Let GlobalExceptionHandler handle it
                } catch (Exception e) {
                        logger.error("Error updating ambulance location via WebSocket for ambulanceId={}: {}",
                                        location.getAmbulanceId(), e.getMessage(), e);
                        // Re-throw to let GlobalExceptionHandler handle it
                        throw e;
                }
        }

        /**
         * WebSocket endpoint for patients to update their location during emergency.
         * Sends location update to ambulance-specific topic so paramedic receives it.
         * 
         * Patient sends to: /app/patient/updateLocation
         * Paramedic subscribes to: /topic/ambulance/{ambulanceId}/patient-location
         */
        @MessageMapping("/patient/updateLocation")
        @Operation(summary = "Update patient location via WebSocket", description = "WebSocket endpoint (STOMP protocol) for patients to send real-time location updates during emergency. "
                        +
                        "Connect to ws://host:port/ws, then send messages to /app/patient/updateLocation. " +
                        "Location updates are broadcasted to /topic/ambulance/{ambulanceId}/patient-location for subscribed paramedics.", tags = {
                                        "WebSocket - Ambulance Location" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Patient location update processed and broadcasted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLocationDto.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request - Invalid location data or validation failed"),
                        @ApiResponse(responseCode = "404", description = "No active emergency request found for patient")
        })
        public void updatePatientLocation(UserLocationDto locationDto) {
                if (locationDto == null) {
                        logger.error("Received null patient location update via WebSocket");
                        throw new IllegalArgumentException("Location data cannot be null");
                }

                try {
                        // Get authenticated patient
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        User patient = (User) auth.getPrincipal();

                        // Update database with the new patient location
                        UserLocationDto updatedLocation = userService.updateUserLocation(locationDto.getLatitude(),
                                        locationDto.getLongitude());

                        // Get patient's active emergency request to find assigned ambulance
                        var activeStatuses = java.util.Arrays.asList(
                                        EmergencyRequestStatus.PENDING,
                                        EmergencyRequestStatus.ASSIGNED,
                                        EmergencyRequestStatus.IN_PROGRESS);

                        var emergencyRequest = emergencyRequestRepository
                                        .findByPatientAndStatusIn(patient, activeStatuses)
                                        .orElseThrow(() -> new NotFoundException(
                                                        "No active emergency request found for patient"));

                        if (emergencyRequest.getAmbulance() == null) {
                                logger.warn("Emergency request {} has no assigned ambulance",
                                                emergencyRequest.getRequestId());
                                return;
                        }

                        // Send to ambulance-specific topic:
                        // /topic/ambulance/{ambulanceId}/patient-location
                        String destination = "/topic/ambulance/" + emergencyRequest.getAmbulance().getAmbulanceId()
                                        + "/patient-location";
                        messagingTemplate.convertAndSend(destination, updatedLocation);

                        logger.debug("WebSocket patient location update successful for userId={}, sent to topic={}",
                                        updatedLocation.getUserId(), destination);
                } catch (IllegalArgumentException | BadRequestException | NotFoundException e) {
                        logger.warn("Error in WebSocket patient location update: {}", e.getMessage());
                        throw e;
                } catch (Exception e) {
                        logger.error("Error updating patient location via WebSocket: {}", e.getMessage(), e);
                        throw e;
                }
        }
}
