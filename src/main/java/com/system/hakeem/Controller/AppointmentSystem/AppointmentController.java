package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

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
