package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Exceptions.BadRequestException;
import com.system.hakeem.Service.EmergencySystem.AmbulanceService;
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
        patientEndpoint.put("description", "Patient subscribes to receive location updates for a specific ambulance");
        patientEndpoint.put("note", "Replace {ambulanceId} with the actual ambulance ID assigned to the patient");
        patientEndpoint.put("example", "/topic/ambulance/1/location");
        patientEndpoint.put("response", Map.of(
                "ambulanceId", "int",
                "latitude", "double",
                "longitude", "double",
                "speed", "double",
                "direction", "double"));
        documentation.put("patientEndpoint", patientEndpoint);

        // Usage Flow
        Map<String, String> flow = new HashMap<>();
        flow.put("step1", "1. Connect to WebSocket: ws://host:port/ws");
        flow.put("step2", "2. Paramedic sends location updates to: /app/ambulance/updateLocation");
        flow.put("step3", "3. Patient subscribes to: /topic/ambulance/{ambulanceId}/location");
        flow.put("step4", "4. Patient receives real-time location updates for their assigned ambulance");
        documentation.put("usageFlow", flow);

        // Important Notes
        Map<String, String> notes = new HashMap<>();
        notes.put("privacy",
                "Each ambulance has its own topic, so patients only receive updates for their assigned ambulance");
        notes.put("scalability", "No conflicts between patients - each subscribes to their specific ambulance topic");
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
}
