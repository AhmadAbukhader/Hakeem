package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Exceptions.BadRequestException;
import com.system.hakeem.Service.EmergencySystem.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class AmbulanceLocationController {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceLocationController.class);
    private final AmbulanceService ambulanceService;

    // URL of the API that get data
    @MessageMapping("/ambulance/updateLocation")
    // URL that front end subscribe to
    @SendTo("/topic/ambulance/locations")
    public AmbulanceLocationDto updateAmbulanceLocation(AmbulanceLocationDto location) {
        if (location == null) {
            logger.error("Received null location update via WebSocket");
            throw new IllegalArgumentException("Location data cannot be null");
        }

        try {
            // Update database with the new ambulance location
            ambulanceService.updateAmbulanceLocation(location);
            logger.debug("WebSocket location update successful for ambulanceId={}", location.getAmbulanceId());
            return location;
        } catch (IllegalArgumentException | BadRequestException e) {
            // Validation errors - log and rethrow to prevent invalid data propagation
            logger.warn("Validation error in WebSocket location update for ambulanceId={}: {}",
                    location.getAmbulanceId(), e.getMessage());
            throw e; // Let GlobalExceptionHandler handle it
        } catch (Exception e) {
            logger.error("Error updating ambulance location via WebSocket for ambulanceId={}: {}",
                    location.getAmbulanceId(), e.getMessage(), e);
            // For other errors, return location to prevent WebSocket connection issues
            // The error is logged and can be handled by monitoring systems
            return location;
        }
    }
}
