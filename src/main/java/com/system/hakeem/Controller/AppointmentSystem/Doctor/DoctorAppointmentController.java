package com.system.hakeem.Controller.AppointmentSystem.Doctor;

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
    public ResponseEntity<List<Appointment>> getDoctorsScheduled() {
        List<Appointment> appointments = appointmentService.getAllScheduledApps();
        return ResponseEntity.ok().body(appointments);
    }

    // opening slots for scheduling by user
    @PostMapping("/doctor/schedule")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<DoctorAppointmentScheduleRequest> doctorSchedule(@RequestBody DoctorAppointmentScheduleRequest request) {
        appointmentService.doctorInsert(request.getAppointmentDateTime());
        return ResponseEntity.ok().body(request);
    }


    //get a doctor all his appointment
    @GetMapping("/doctor/scheduled")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<List<DoctorAppointmentsDto>> getDoctorScheduled() {
        List<DoctorAppointmentsDto> appointments = appointmentService.getDoctorApps();
        return ResponseEntity.ok().body(appointments);
    }

    @PutMapping("/doctor/complete")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<DoctorAppointmentsDto> doctorCompletedAppointment (@RequestParam int appointmentId) {
        DoctorAppointmentsDto appointment = appointmentService.updateCompletedAppointment(appointmentId);
        return ResponseEntity.ok().body(appointment);
    }

}
