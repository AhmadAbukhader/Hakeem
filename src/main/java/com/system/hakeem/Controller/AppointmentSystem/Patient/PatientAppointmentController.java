package com.system.hakeem.Controller.AppointmentSystem.Patient;

import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class PatientAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // patient
    @PostMapping("/patient/schedule")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<PatientAppointmentScheduleRequest> patientSchedule(@RequestBody PatientAppointmentScheduleRequest request) {
        appointmentService.patientInsert(request.getAppointmentType() , request.getAppointmentDateTime());
        return ResponseEntity.ok().body(request);
    }

    @GetMapping("/patient/scheduled")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<List<PatientAppointmentsDto>> getPatientScheduled() {
        List<PatientAppointmentsDto> appointments = appointmentService.getPatientApps();
        return ResponseEntity.ok().body(appointments);
    }

}
