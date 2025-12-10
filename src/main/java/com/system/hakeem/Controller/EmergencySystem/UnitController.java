package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceUnitRequest;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Service.EmergencySystem.AmbulanceUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ambulanceUnit")
@Tag(name = "Ambulance Unit Management", description = "APIs for managing ambulance units (groups of ambulances)")
public class UnitController {

    private final AmbulanceUnitService ambulanceUnitService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    @Operation(summary = "Create a new ambulance unit", description = "Creates a new ambulance unit (group) in the system. Units are used to organize multiple ambulances. Only paramedics can create units.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ambulance unit created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AmbulanceUnit.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Paramedic role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AmbulanceUnit> createAmbulanceUnit(
            @Parameter(description = "Ambulance unit creation request with unit name and details", required = true) @RequestBody CreateAmbulanceUnitRequest ambulanceUnit) {
        try {
            return ResponseEntity.ok(ambulanceUnitService.createUnit(ambulanceUnit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get ambulance unit by name", description = "Retrieves information about a specific ambulance unit using its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ambulance unit found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AmbulanceUnit.class))),
            @ApiResponse(responseCode = "404", description = "Ambulance unit not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AmbulanceUnit> getAmbulanceUnit(
            @Parameter(description = "Ambulance unit name", required = true, example = "Unit-1") @PathVariable String name) {
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
