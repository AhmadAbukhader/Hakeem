package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.DoctorAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.PatientAppointmentScheduleRequest;
import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import com.system.hakeem.Service.UserManagement.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllApps();
        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Appointment>> getAvailableAppointments() {
        List<Appointment> appointments = appointmentService.getAllAvailableApps();
        return ResponseEntity.ok().body(appointments);
    }

    @PostMapping("/doctor/schedule")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<DoctorAppointmentScheduleRequest> doctorSchedule(@RequestBody DoctorAppointmentScheduleRequest request) {
        appointmentService.doctorInsert(request.getAppointmentDateTime());
        return ResponseEntity.ok().body(new DoctorAppointmentScheduleRequest());
    }

    @PostMapping("/patient/schedule")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<PatientAppointmentScheduleRequest> doctorSchedule(@RequestBody PatientAppointmentScheduleRequest request) {
        appointmentService.patientInsert(request.getAppointmentType() , request.getAppointmentDateTime());
        return ResponseEntity.ok().body(new PatientAppointmentScheduleRequest());
    }

    @GetMapping("/doctor/scheduled")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<List<Appointment>> getDoctorScheduled() {
        List<Appointment> appointments = appointmentService.getAllScheduledApps();
        return ResponseEntity.ok().body(appointments);
    }



}
