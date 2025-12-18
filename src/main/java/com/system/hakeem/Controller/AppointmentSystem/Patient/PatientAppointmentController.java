package com.system.hakeem.Controller.AppointmentSystem.Patient;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import com.system.hakeem.Service.UserManagement.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Tag(name = "Patient Appointment Management", description = "APIs for patients to manage appointments and search for doctors")
public class PatientAppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    // patient
    @PostMapping("/patient/schedule")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Book an appointment", description = "Allows a patient to book an available appointment slot with a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment booked successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientAppointmentScheduleRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request - missing doctor ID or invalid appointment data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PatientAppointmentScheduleRequest> patientSchedule(
            @Parameter(description = "Appointment booking request with doctor ID, type, and date/time", required = true) @RequestBody PatientAppointmentScheduleRequest request) {
        try {
            if (request.getDoctorId() == null) {
                return ResponseEntity.badRequest().build();
            }
            appointmentService.patientInsert(request.getAppointmentType(), request.getAppointmentDateTime(),
                    request.getDoctorId());
            return ResponseEntity.ok().body(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patient/scheduled")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Get patient's scheduled appointments", description = "Retrieves all appointments scheduled by the authenticated patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved patient appointments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientAppointmentsDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PatientAppointmentsDto>> getPatientScheduled() {
        try {
            List<PatientAppointmentsDto> appointments = appointmentService.getPatientApps();
            return ResponseEntity.ok().body(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // if the user want to cancel an appointment he could cancel it with this
    @PutMapping("/patient/cancel")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Cancel an appointment", description = "Allows a patient to cancel one of their scheduled appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientAppointmentsDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Patient does not own this appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PatientAppointmentsDto> cancelAppointment(
            @Parameter(description = "Appointment ID to cancel", required = true, example = "1") @RequestParam int appointmentId) {
        try {
            PatientAppointmentsDto appointment = appointmentService.cancelAppointment(appointmentId);
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

    @GetMapping("/patient/doctors/rated")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Get doctors with filters", description = "Retrieves a list of doctors filtered by specialization, location (same city as patient), and optionally sorted by rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DoctorDto>> getDoctorsRated(
            @Parameter(description = "Filter by doctor specialization", required = false, example = "Cardiology") @RequestParam(required = false) String specialization,
            @Parameter(description = "If true, filter doctors by same city as the patient (within 50km radius)", required = false, example = "true") @RequestParam(required = false) Boolean location,
            @Parameter(description = "Sort by rating if true", required = false, example = "true") @RequestParam(required = false) Boolean rated,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        List<DoctorDto> doctors = userService.getDoctors(specialization, rated, location, pageable);
        return ResponseEntity.ok().body(doctors);
    }

    @GetMapping("/patient/doctors/search")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Search doctors by name", description = "Searches for doctors by name (case-insensitive partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DoctorDto>> searchDoctorsByName(
            @Parameter(description = "Doctor name to search for (partial match)", required = true, example = "John") @RequestParam String name,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        try {
            List<DoctorDto> doctors = userService.searchDoctorsByName(name, pageable);
            return ResponseEntity.ok().body(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
