package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentsDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
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




}
