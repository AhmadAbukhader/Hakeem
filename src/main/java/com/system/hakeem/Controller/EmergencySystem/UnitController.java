package com.system.hakeem.Controller.EmergencySystem;


import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceUnitRequest;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Service.EmergancySystem.AmbulanceUnitService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Builder
@RestController
@RequestMapping("/ambulanceUnit")
public class UnitController {

    private final AmbulanceUnitService ambulanceUnitService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    public ResponseEntity<AmbulanceUnit> createAmbulanceUnit(@RequestBody CreateAmbulanceUnitRequest ambulanceUnit) {
        System.out.println(ambulanceUnit.getLicense());
        return ResponseEntity.ok(ambulanceUnitService.createUnit(ambulanceUnit));
    }
    @GetMapping("/{name}")
    public ResponseEntity<AmbulanceUnit> getAmbulanceUnit(@PathVariable String name) {
        return ResponseEntity.ok(ambulanceUnitService.getUnit(name));
    }
}
