package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Appointment.AppointmentDto;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping()
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        try {
            List<AppointmentDto> appointments = appointmentService.getAllApps();
            return ResponseEntity.ok().body(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available/{Id}")
    public ResponseEntity<List<AppointmentDto>> getDoctorAvailableAppointments(@PathVariable int Id) {
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
