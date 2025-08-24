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

    // if the user want to cancel an appointment he could cancel it with this
    @PutMapping("/patient/cancel")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<PatientAppointmentsDto> cancelAppointment(@RequestParam int appointmentId) {
        PatientAppointmentsDto appointment = appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok().body(appointment);
    }

    @GetMapping("/patient/doctors/rated")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<List<DoctorDto>> getDoctorsRated(
            @RequestParam(required = false) String specialization ,
            @RequestParam(required = false) Boolean rated,
            Pageable pageable) {
        List<DoctorDto> doctors = userService.getDoctors(specialization ,rated ,pageable);
        return ResponseEntity.ok().body(doctors);
    }

}
