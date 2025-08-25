package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceResponse;
import com.system.hakeem.Dto.EmergencySystem.AmbulanceLocationDto;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Service.EmergancySystem.AmbulanceService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Builder
@RestController
@RequestMapping("/ambulance")
public class   AmbulanceController {

    private final AmbulanceService ambulanceService;



    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    public ResponseEntity<CreateAmbulanceResponse> createAmbulance(@RequestBody CreateAmbulanceRequest ambulance) {
        CreateAmbulanceResponse response = ambulanceService.createAmbulance(ambulance);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<Ambulance> getAmbulance(@PathVariable String plate) {
        try {
            return ResponseEntity.ok(ambulanceService.getAmbulanceByPlateNumber(plate));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/location")
    public ResponseEntity<AmbulanceLocationDto> updateLocation(@RequestBody AmbulanceLocationDto location) {

        AmbulanceLocationDto updatedLocation = ambulanceService.updateAmbulanceLocation(location);
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

