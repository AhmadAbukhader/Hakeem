package com.system.hakeem.Controller.AppointmentSystem.Patient;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentScheduleRequest;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Service.AppointmentSystem.AppointmentService;
import com.system.hakeem.Service.UserManagement.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class PatientAppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;

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


    //patient can get all the doctors or filter them depending on specialty or location or both
    @GetMapping("/patient/doctors")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<List<DoctorDto>> getDoctors(
            @RequestParam String location ,
            @RequestParam String specialization ,
            Pageable pageable) {
        List<DoctorDto> doctors = userService.getDoctors(location , specialization , pageable);
        return ResponseEntity.ok().body(doctors);
    }

}
