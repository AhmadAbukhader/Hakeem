package com.system.hakeem.Controller.EmergencySystem;


import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceUnitRequest;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Service.EmergencySystem.AmbulanceUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ambulanceUnit")
public class UnitController {

    private final AmbulanceUnitService ambulanceUnitService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    public ResponseEntity<AmbulanceUnit> createAmbulanceUnit(@RequestBody CreateAmbulanceUnitRequest ambulanceUnit) {
        try {
            return ResponseEntity.ok(ambulanceUnitService.createUnit(ambulanceUnit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<AmbulanceUnit> getAmbulanceUnit(@PathVariable String name) {
        try {
            AmbulanceUnit unit = ambulanceUnitService.getUnit(name);
            if (unit == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(unit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
