package com.system.hakeem.Controller.AppointmentSystem.Patient;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import com.system.hakeem.Service.UserManagement.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class PatientAppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    // patient
    @PostMapping("/patient/schedule")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<PatientAppointmentScheduleRequest> patientSchedule(
            @RequestBody PatientAppointmentScheduleRequest request) {
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
    public ResponseEntity<PatientAppointmentsDto> cancelAppointment(@RequestParam int appointmentId) {
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
    public ResponseEntity<List<DoctorDto>> getDoctorsRated(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Boolean rated,
            Pageable pageable) {
        List<DoctorDto> doctors = userService.getDoctors(specialization, rated, pageable);
        return ResponseEntity.ok().body(doctors);
    }

}
