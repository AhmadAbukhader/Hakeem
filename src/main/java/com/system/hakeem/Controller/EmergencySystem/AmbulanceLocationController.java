package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Exceptions.BadRequestException;
import com.system.hakeem.Service.EmergencySystem.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class AmbulanceLocationController {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceLocationController.class);
    private final AmbulanceService ambulanceService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket endpoint for paramedics to update ambulance location.
     * Sends location update to ambulance-specific topic so only subscribed patients
     * receive it.
     * 
     * Paramedic sends to: /app/ambulance/updateLocation
     * Patient subscribes to: /topic/ambulance/{ambulanceId}/location
     */
    @MessageMapping("/ambulance/updateLocation")
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
