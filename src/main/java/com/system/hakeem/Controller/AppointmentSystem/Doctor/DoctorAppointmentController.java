package com.system.hakeem.Controller.AppointmentSystem.Doctor;

import com.system.hakeem.Dto.AppointmentSystem.Appointment.AppointmentDto;
import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentsDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    // getting all appointments scheduled by patients and doctors
    @GetMapping("/doctors/scheduled")
    @PreAuthorize("hasAnyRole('DOCTOR')")
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
    public ResponseEntity<DoctorAppointmentScheduleRequest> doctorSchedule(
            @RequestBody DoctorAppointmentScheduleRequest request) {
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
    public ResponseEntity<DoctorAppointmentsDto> doctorCompletedAppointment(@RequestParam int appointmentId) {
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
