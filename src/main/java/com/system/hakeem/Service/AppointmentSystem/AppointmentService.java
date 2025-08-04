package com.system.hakeem.Service.AppointmentSystem;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRatingRepository doctorRatingRepository;

    public void doctorInsert (LocalDateTime appDateTime){
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

    public List<Appointment> getAllAvailableApps(){
        return appointmentRepository.findByIsAvailable(true);
    }

    public List<Appointment> getAllScheduledApps(){
        return appointmentRepository.findByIsAvailable(false);
    }

}
