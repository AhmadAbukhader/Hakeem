package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Appointment.AppointmentDto;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "APIs for managing appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping()
    @Operation(summary = "Get all appointments", description = "Retrieves a list of all appointments in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        try {
            List<AppointmentDto> appointments = appointmentService.getAllApps();
            return ResponseEntity.ok().body(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available/{Id}")
    @Operation(summary = "Get available appointments for a doctor", description = "Retrieves all available appointment slots for a specific doctor by doctor ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available appointments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid doctor ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AppointmentDto>> getDoctorAvailableAppointments(
            @Parameter(description = "Doctor ID", required = true, example = "1") @PathVariable int Id) {
        // id is for the doctor so the patient can get time slots where doctor
        try {
            List<AppointmentDto> appointments = appointmentService.getAllAvailableApps(Id);
            return ResponseEntity.ok().body(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
