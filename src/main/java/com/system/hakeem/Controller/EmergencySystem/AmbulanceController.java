package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceResponse;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Service.EmergencySystem.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/ambulance")
public class AmbulanceController {

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
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/location")
    public ResponseEntity<AmbulanceLocationDto> updateLocation(@RequestBody AmbulanceLocationDto location) {
        try {
            AmbulanceLocationDto updatedLocation = ambulanceService.updateAmbulanceLocation(location);
            return ResponseEntity.ok(updatedLocation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/closest")
    public ResponseEntity<Ambulance> findClosestAmbulance(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            Ambulance ambulance = ambulanceService.findClosestAvailableAmbulance(latitude, longitude);
            if (ambulance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ambulance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ambulance> getAmbulance(@PathVariable int id) {
        return ambulanceService.getAmbulanceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

