package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.EmergencyRequestDto;
import com.system.hakeem.Dto.EmergencySystem.EmergencyRequestResponseDto;
import com.system.hakeem.Model.EmergencySystem.EmergencyRequestStatus;
import com.system.hakeem.Service.EmergencySystem.EmergencyService;
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

import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/emergency")
@Tag(name = "Emergency Request Management", description = "APIs for managing emergency requests, ambulance dispatch, and real-time location tracking")
public class EmergencyController {

    private final EmergencyService emergencyService;

    @PostMapping("/request")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Request emergency ambulance", description = "Creates a new emergency request, finds the closest available ambulance, assigns it, "
            +
            "and sends patient location to the assigned paramedic via WebSocket. " +
            "The patient can then subscribe to /topic/ambulance/{ambulanceId}/location to receive ambulance location updates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergency request created successfully and ambulance assigned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmergencyRequestResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid coordinates or active request exists"),
            @ApiResponse(responseCode = "404", description = "No available ambulances found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required")
    })
    public ResponseEntity<EmergencyRequestResponseDto> requestAmbulance(
            @Parameter(description = "Emergency request with patient location and optional notes", required = true) @Valid @RequestBody EmergencyRequestDto requestDto) {
        EmergencyRequestResponseDto response = emergencyService.createEmergencyRequest(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-request")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Get my active emergency request", description = "Retrieves the current active emergency request for the authenticated patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergency request found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmergencyRequestResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No active emergency request found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required")
    })
    public ResponseEntity<EmergencyRequestResponseDto> getMyEmergencyRequest() {
        EmergencyRequestResponseDto response = emergencyService.getMyEmergencyRequest();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyRole('PARAMEDIC')")
    @Operation(summary = "Get my emergency requests", description = "Retrieves all emergency requests assigned to the authenticated paramedic's ambulance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergency requests retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmergencyRequestResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Paramedic role required")
    })
    public ResponseEntity<List<EmergencyRequestResponseDto>> getMyParamedicEmergencyRequests() {
        List<EmergencyRequestResponseDto> requests = emergencyService.getMyParamedicEmergencyRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/status")
    @PreAuthorize("hasAnyRole('PARAMEDIC', 'ADMIN')")
    @Operation(summary = "Update emergency request status", description = "Updates the status of an emergency request (ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED). "
            +
            "When status is set to COMPLETED or CANCELLED, the ambulance is automatically set to AVAILABLE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergency request status updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmergencyRequestResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Emergency request not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Paramedic or Admin role required")
    })
    public ResponseEntity<EmergencyRequestResponseDto> updateEmergencyRequestStatus(
            @Parameter(description = "Emergency request ID", required = true, example = "1") @PathVariable Integer requestId,
            @Parameter(description = "New status", required = true, example = "IN_PROGRESS") @RequestParam EmergencyRequestStatus status) {
        EmergencyRequestResponseDto response = emergencyService.updateEmergencyRequestStatus(requestId, status);
        return ResponseEntity.ok(response);
    }
}
