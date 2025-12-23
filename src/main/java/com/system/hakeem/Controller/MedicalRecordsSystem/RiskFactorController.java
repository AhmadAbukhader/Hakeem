package com.system.hakeem.Controller.MedicalRecordsSystem;

import com.system.hakeem.Dto.MedicalRecordsSystem.CreateRiskFactorRequest;
import com.system.hakeem.Dto.MedicalRecordsSystem.RiskFactorResponse;
import com.system.hakeem.Dto.MedicalRecordsSystem.UpdateRiskFactorRequest;
import com.system.hakeem.Service.MedicalRecordsSystem.RiskFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-records/risk-factors")
@RequiredArgsConstructor
@Tag(name = "Risk Factor Management", description = "APIs for managing patient risk factors")
public class RiskFactorController {

    private final RiskFactorService riskFactorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Create a new risk factor", description = "Creates a new risk factor for the authenticated patient. Only patients can create risk factors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risk factor created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskFactorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RiskFactorResponse> createRiskFactor(
            @Parameter(description = "Risk factor data including factor name", required = true) @RequestBody CreateRiskFactorRequest request) {
        try {
            RiskFactorResponse response = riskFactorService.createRiskFactor(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Get risk factor by ID", description = "Retrieves a risk factor by its unique ID. Patients can only access their own risk factors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risk factor found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskFactorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid risk factor ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient or Doctor role required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot access this risk factor"),
            @ApiResponse(responseCode = "404", description = "Risk factor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RiskFactorResponse> getRiskFactorById(
            @Parameter(description = "Risk factor unique identifier", required = true, example = "1") @PathVariable Integer id) {
        try {
            RiskFactorResponse response = riskFactorService.getRiskFactorById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Get all risk factors for a patient", description = "Retrieves all risk factors for the specified patient ID. Patients can only view their own risk factors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved risk factors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskFactorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid patient ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient or Doctor role required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot access these risk factors"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<RiskFactorResponse>> getRiskFactorsByPatientId(
            @Parameter(description = "Patient ID to retrieve risk factors for", required = true, example = "1") @PathVariable Integer patientId) {
        try {
            List<RiskFactorResponse> riskFactors = riskFactorService.getRiskFactorsByPatientId(patientId);
            return ResponseEntity.ok(riskFactors);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Update a risk factor", description = "Updates an existing risk factor by its ID. Only patients can update their own risk factors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risk factor updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskFactorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot update this risk factor"),
            @ApiResponse(responseCode = "404", description = "Risk factor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RiskFactorResponse> updateRiskFactor(
            @Parameter(description = "Risk factor unique identifier", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "Updated risk factor data", required = true) @RequestBody UpdateRiskFactorRequest request) {
        try {
            RiskFactorResponse response = riskFactorService.updateRiskFactor(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Delete a risk factor", description = "Deletes a risk factor by its ID. Only patients can delete their own risk factors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risk factor deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid risk factor ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete this risk factor"),
            @ApiResponse(responseCode = "404", description = "Risk factor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteRiskFactor(
            @Parameter(description = "Risk factor unique identifier", required = true, example = "1") @PathVariable Integer id) {
        try {
            riskFactorService.deleteRiskFactor(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
