package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Appointment>> getDoctorAvailableAppointments(@PathVariable int Id) {
        // id is for the doctor so the patient can get time slots where doctor
        List<Appointment> appointments = appointmentService.getAllAvailableApps(Id);
        return ResponseEntity.ok().body(appointments);
    }



}
