package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceResponse;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Service.EmergencySystem.AmbulanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ambulance")
@Tag(name = "Ambulance Management", description = "APIs for managing ambulances, tracking locations, and finding available ambulances")
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    @Operation(summary = "Create a new ambulance", description = "Registers a new ambulance in the system with plate number, unit assignment, and initial location. Only paramedics can create ambulances.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ambulance created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateAmbulanceResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Paramedic role required"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid data or duplicate plate number")
    })
    public ResponseEntity<CreateAmbulanceResponse> createAmbulance(
            @Parameter(description = "Ambulance creation request with plate number, unit name, and location", required = true) @RequestBody CreateAmbulanceRequest ambulance) {
        CreateAmbulanceResponse response = ambulanceService.createAmbulance(ambulance);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plate/{plate}")
    @Operation(summary = "Get ambulance by plate number", description = "Retrieves ambulance information using its license plate number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ambulance found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ambulance.class))),
            @ApiResponse(responseCode = "404", description = "Ambulance not found with the given plate number"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Ambulance> getAmbulance(
            @Parameter(description = "Ambulance license plate number", required = true, example = "ABC123") @PathVariable String plate) {
        try {
            return ResponseEntity.ok(ambulanceService.getAmbulanceByPlateNumber(plate));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/location")
    @Operation(summary = "Update ambulance location", description = "Updates the current GPS location of an ambulance. Used for real-time tracking of ambulance positions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ambulance location updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AmbulanceLocationDto.class))),
            @ApiResponse(responseCode = "404", description = "Ambulance not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AmbulanceLocationDto> updateLocation(
            @Parameter(description = "Ambulance location update with ambulance ID, latitude, and longitude", required = true) @RequestBody AmbulanceLocationDto location) {
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
    @Operation(summary = "Find closest available ambulance", description = "Finds the nearest available ambulance to a given location using GPS coordinates. Useful for emergency dispatch.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Closest available ambulance found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ambulance.class))),
            @ApiResponse(responseCode = "404", description = "No available ambulances found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Ambulance> findClosestAmbulance(
            @Parameter(description = "Latitude coordinate", required = true, example = "31.9522") @RequestParam double latitude,
            @Parameter(description = "Longitude coordinate", required = true, example = "35.2332") @RequestParam double longitude) {
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
    @Operation(summary = "Get ambulance by ID", description = "Retrieves ambulance information using its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ambulance found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ambulance.class))),
            @ApiResponse(responseCode = "404", description = "Ambulance not found with the given ID")
    })
    public ResponseEntity<Ambulance> getAmbulance(
            @Parameter(description = "Ambulance unique identifier", required = true, example = "1") @PathVariable int id) {
        return ambulanceService.getAmbulanceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
