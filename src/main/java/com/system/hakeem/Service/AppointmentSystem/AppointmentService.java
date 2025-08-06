package com.system.hakeem.Service.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentsDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Model.AppointmentSystem.AppointmentStatus;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.AppointmentSystem.AppointmentRepository;
import com.system.hakeem.Repository.AppointmentSystem.DoctorRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRatingRepository doctorRatingRepository;

    public void doctorInsert (LocalDateTime appDateTime) throws DuplicateKeyException {
        Appointment app = appointmentRepository.findByAppointmentDate(appDateTime);
        if (app != null) {
            throw new DuplicateKeyException("this appointment already exist");
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Appointment appointment = Appointment
                .builder()
                .doctor(user)
                .appointmentDate(appDateTime)
                .isAvailable(true)
                .build();

        appointmentRepository.save(appointment);
    }

    public void patientInsert (AppointmentType appType ,LocalDateTime appDateTime){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Appointment appointment = appointmentRepository.findByAppointmentDate(appDateTime);

        if (appointment == null)
            throw new IllegalArgumentException("appointment not found");

        appointment.setAppointmentDate(appDateTime);
        appointment.setIsAvailable(false);
        appointment.setPatient(user);
        appointment.setAppType(appType);
        appointment.setStatus(AppointmentStatus.Scheduled);
        appointmentRepository.save(appointment);

    }

    public List<Appointment> getAllApps(){
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAllAvailableApps(int id){
        return appointmentRepository.findByIsAvailableAndDoctorId(true , id);
    }

    public List<Appointment> getAllScheduledApps(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User doctor = (User) auth.getPrincipal();
        return appointmentRepository.findByIsAvailableAndDoctorId(false , doctor.getId() );
    }

    public List<PatientAppointmentsDto> getPatientApps() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<Appointment> appointments = appointmentRepository.findByPatientId(user.getId());
        List<PatientAppointmentsDto> patientAppointments = appointments.stream().map(
                app -> PatientAppointmentsDto
                        .builder()
                        .id(app.getId())
                        .doctorId(app.getDoctor().getId())
                        .patientId(app.getPatient().getId())
                        .doctorName(app.getDoctor().getName())
                        .patientName(app.getPatient().getName())
                        .doctorUsername(app.getDoctor().getUsername())
                        .patientUsername(app.getPatient().getUsername())
                        .doctorLocation(app.getDoctor().getLocation())
                        .appointmentDate(app.getAppointmentDate())
                        .appointmentType(app.getAppType())
                        .appointmentStatus(app.getStatus())
                        .build()
        ).collect(Collectors.toList());
        return patientAppointments;
    }

    public List<DoctorAppointmentsDto> getDoctorApps() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<Appointment> appointments = appointmentRepository.findByDoctorId(user.getId());

        List<DoctorAppointmentsDto> doctorAppointments = appointments.stream().map(
                app -> DoctorAppointmentsDto
                        .builder()
                        .id(app.getId())
                        .doctorId(app.getDoctor().getId())
                        .patientId(app.getPatient().getId())
                        .doctorName(app.getDoctor().getName())
                        .patientName(app.getPatient().getName())
                        .doctorUsername(app.getDoctor().getUsername())
                        .patientUsername(app.getPatient().getUsername())
                        .appointmentDate(app.getAppointmentDate())
                        .appointmentType(app.getAppType())
                        .appointmentStatus(app.getStatus())
                        .build()
        ).collect(Collectors.toList());

        return doctorAppointments ;
    }

}
