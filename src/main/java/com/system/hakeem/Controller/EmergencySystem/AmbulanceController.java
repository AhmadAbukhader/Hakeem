package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Model.EmergencySystem.AmbulanceLocation;
import com.system.hakeem.Service.EmergancySystem.AmbulanceService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Builder
@RestController
@RequestMapping("ambulance")
public class   AmbulanceController {

    private final AmbulanceService ambulanceService;



    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    public ResponseEntity<Ambulance> createAmbulance(@RequestBody CreateAmbulanceRequest ambulance) {
        Ambulance response = ambulanceService.createAmbulance(ambulance);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{ambulanceId}/location")
    public ResponseEntity<AmbulanceLocation> updateLocation(
            @PathVariable int ambulanceId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double speed,
            @RequestParam double direction) {

        AmbulanceLocation updatedLocation = ambulanceService.updateAmbulanceLocation(
                ambulanceId, latitude, longitude, speed, direction
        );
        return ResponseEntity.ok(updatedLocation);
    }

    @GetMapping("/closest")
    public ResponseEntity<Ambulance> findClosestAmbulance(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        Ambulance ambulance = ambulanceService.findClosestAvailableAmbulance(latitude, longitude);
        return ResponseEntity.ok(ambulance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ambulance> getAmbulance(@PathVariable int id) {
        return ambulanceService.getAmbulanceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

