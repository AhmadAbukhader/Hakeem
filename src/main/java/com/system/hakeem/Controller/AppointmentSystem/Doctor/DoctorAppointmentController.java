package com.system.hakeem.Controller.AppointmentSystem.Doctor;

import com.system.hakeem.Dto.AppointmentSystem.Appointment.AppointmentDto;
import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentsDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Tag(name = "Doctor Appointment Management", description = "APIs for doctors to manage their appointments")
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    // getting all appointments scheduled by patients and doctors
    @GetMapping("/doctors/scheduled")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Operation(summary = "Get all scheduled appointments", description = "Retrieves all scheduled appointments for the authenticated doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved scheduled appointments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Doctor role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AppointmentDto>> getDoctorsScheduled() {
        try {
            List<AppointmentDto> appointments = appointmentService.getAllScheduledApps();
            return ResponseEntity.ok().body(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // opening slots for scheduling by doctor
    @PostMapping("/doctor/schedule")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Operation(summary = "Create appointment slot", description = "Allows a doctor to create an available appointment slot for patients to book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment slot created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorAppointmentScheduleRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment date/time"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Doctor role required"),
            @ApiResponse(responseCode = "409", description = "Conflict - Appointment slot already exists at this time"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DoctorAppointmentScheduleRequest> doctorSchedule(
            @Parameter(description = "Appointment schedule request with date and time", required = true) @RequestBody DoctorAppointmentScheduleRequest request) {
        try {
            appointmentService.doctorInsert(request.getAppointmentDateTime());
            return ResponseEntity.ok().body(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // get a doctor all his appointment
    @GetMapping("/doctor/scheduled")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Operation(summary = "Get doctor's all appointments", description = "Retrieves all appointments (scheduled, completed, cancelled) for the authenticated doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor appointments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorAppointmentsDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Doctor role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DoctorAppointmentsDto>> getDoctorScheduled() {
        try {
            List<DoctorAppointmentsDto> appointments = appointmentService.getDoctorApps();
            return ResponseEntity.ok().body(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/doctor/complete")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Operation(summary = "Mark appointment as completed", description = "Allows a doctor to mark an appointment as completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment marked as completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorAppointmentsDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Doctor role required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Doctor does not own this appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DoctorAppointmentsDto> doctorCompletedAppointment(
            @Parameter(description = "Appointment ID to mark as completed", required = true, example = "1") @RequestParam int appointmentId) {
        try {
            DoctorAppointmentsDto appointment = appointmentService.updateCompletedAppointment(appointmentId);
            if (appointment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(appointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
