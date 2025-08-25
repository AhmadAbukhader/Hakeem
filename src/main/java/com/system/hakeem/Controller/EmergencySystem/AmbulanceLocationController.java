package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.AmbulanceLocationDto;
import com.system.hakeem.Service.EmergancySystem.AmbulanceService;
import lombok.Builder;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Builder
public class AmbulanceLocationController {
    private final AmbulanceService ambulanceService;

    //URL of the API that get data
    @MessageMapping("/ambulance/updateLocation")
    //URL that front end subscribe to
    @SendTo("/topic/ambulance/locations")
    public AmbulanceLocationDto updateAmbulanceLocation(AmbulanceLocationDto location) {
        // Update database with the new ambulance location
        ambulanceService.updateAmbulanceLocation(location);
        return location;
    }
}
