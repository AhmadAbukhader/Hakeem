package com.system.hakeem.Controller.EmergencySystem;


import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceUnitRequest;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Service.EmergancySystem.AmbulanceUnitService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Builder
@RestController
@RequestMapping("AmbulanceUnit")
public class UnitController {

    private final AmbulanceUnitService ambulanceUnitService;

    @PostMapping("/create")
    public ResponseEntity<AmbulanceUnit> createAmbulanceUnit(@RequestBody CreateAmbulanceUnitRequest ambulanceUnit) {
        return ResponseEntity.ok(ambulanceUnitService.createUnit(ambulanceUnit));
    }
}
