package com.system.hakeem.Service.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Appointment.AppointmentDto;
import com.system.hakeem.Dto.AppointmentSystem.Appointment.AppointmentMapper;
import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorAppointmentsDto;
import com.system.hakeem.Dto.AppointmentSystem.Patient.PatientAppointmentsDto;
import com.system.hakeem.Model.AppointmentSystem.Appointment;
import com.system.hakeem.Model.AppointmentSystem.AppointmentStatus;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.AppointmentSystem.AppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public void doctorInsert(LocalDateTime appDateTime) throws DuplicateKeyException {
        // Validate that appointment date is not in the past
        if (appDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create appointment in the past");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // Check if this doctor already has an appointment at this time
        Appointment existingApp = appointmentRepository.findByAppointmentDateAndDoctorId(appDateTime, user.getId());
        if (existingApp != null) {
            throw new DuplicateKeyException("this doctor already has an appointment at this time");
        }

        Appointment appointment = Appointment
                .builder()
                .doctor(user)
                .appointmentDate(appDateTime)
                .isAvailable(true)
                .status(null) // Status will be set when patient books
                .build();

        appointmentRepository.save(appointment);
    }

    @Transactional
    public void patientInsert(AppointmentType appType, LocalDateTime appDateTime, int doctorId) {
        // Validate that appointment date is not in the past
        if (appDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book appointment in the past");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // First, check if there's any appointment with this doctor and time (regardless
        // of availability)
        Appointment existingAppointment = appointmentRepository.findByAppointmentDateAndDoctorId(appDateTime, doctorId);

        if (existingAppointment == null) {
            throw new IllegalArgumentException("Appointment slot not found for this doctor at the specified time");
        }

        // Check if the appointment is already booked
        if (existingAppointment.getPatient() != null) {
            if (existingAppointment.getPatient().getId() == user.getId()) {
                throw new IllegalArgumentException("You have already booked this appointment");
            } else {
                throw new IllegalArgumentException("This appointment slot is already booked by another patient");
            }
        }

        // Check if the appointment is marked as available
        if (!Boolean.TRUE.equals(existingAppointment.getIsAvailable())) {
            throw new IllegalArgumentException("This appointment slot is no longer available");
        }

        // Book the appointment
        existingAppointment.setIsAvailable(false);
        existingAppointment.setPatient(user);
        existingAppointment.setAppType(appType);
        existingAppointment.setStatus(AppointmentStatus.scheduled);
        appointmentRepository.save(existingAppointment);

    }

    public List<AppointmentDto> getAllApps() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointmentMapper.mapAppointments(appointments);
    }

    public List<AppointmentDto> getAllAvailableApps(int id) {
        List<Appointment> appointments = appointmentRepository.findByIsAvailableAndDoctorId(true, id);
        return appointmentMapper.mapAppointments(appointments);
    }

    public List<AppointmentDto> getAllScheduledApps() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User doctor = (User) auth.getPrincipal();
        // Filter by status=Scheduled instead of isAvailable=false to exclude
        // cancelled/completed appointments
        List<Appointment> appointments = appointmentRepository.findByStatusAndDoctorId(AppointmentStatus.scheduled,
                doctor.getId());
        return appointmentMapper.mapAppointments(appointments);
    }

    public List<PatientAppointmentsDto> getPatientApps() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<Appointment> appointments = appointmentRepository.findByPatientId(user.getId());
        return appointments.stream()
                .filter(app -> app.getPatient() != null) // Filter out appointments without patient
                .map(app -> PatientAppointmentsDto
                        .builder()
                        .id(app.getId())
                        .doctorId(app.getDoctor() != null ? app.getDoctor().getId() : 0)
                        .patientId(app.getPatient() != null ? app.getPatient().getId() : 0)
                        .doctorName(app.getDoctor() != null ? app.getDoctor().getName() : null)
                        .patientName(app.getPatient() != null ? app.getPatient().getName() : null)
                        .doctorUsername(app.getDoctor() != null ? app.getDoctor().getUsername() : null)
                        .patientUsername(app.getPatient() != null ? app.getPatient().getUsername() : null)
                        .doctorLocation(app.getDoctor() != null ? app.getDoctor().getLocation() : null)
                        .appointmentDate(app.getAppointmentDate())
                        .appointmentType(app.getAppType())
                        .appointmentStatus(app.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public List<DoctorAppointmentsDto> getDoctorApps() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<Appointment> appointments = appointmentRepository.findByDoctorId(user.getId());

        return appointments.stream().map(
                app -> DoctorAppointmentsDto
                        .builder()
                        .id(app.getId())
                        .doctorId(app.getDoctor() != null ? app.getDoctor().getId() : 0)
                        .patientId(app.getPatient() != null ? app.getPatient().getId() : 0)
                        .doctorName(app.getDoctor() != null ? app.getDoctor().getName() : null)
                        .patientName(app.getPatient() != null ? app.getPatient().getName() : null)
                        .doctorUsername(app.getDoctor() != null ? app.getDoctor().getUsername() : null)
                        .patientUsername(app.getPatient() != null ? app.getPatient().getUsername() : null)
                        .appointmentDate(app.getAppointmentDate())
                        .appointmentType(app.getAppType())
                        .appointmentStatus(app.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientAppointmentsDto cancelAppointment(int appointmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }

        // Authorization check: Verify the logged-in patient owns this appointment
        if (appointment.getPatient() == null || appointment.getPatient().getId() != currentUser.getId()) {
            throw new SecurityException("You are not authorized to cancel this appointment");
        }

        // Free up the slot for reuse
        appointment.setStatus(AppointmentStatus.cancelled);
        appointment.setIsAvailable(true);
        appointment.setPatient(null);
        appointment.setAppType(null);
        appointmentRepository.save(appointment);

        return PatientAppointmentsDto.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : 0)
                .patientId(0) // Patient is now null, so set to 0
                .doctorName(appointment.getDoctor() != null ? appointment.getDoctor().getName() : null)
                .patientName(null) // Patient is now null
                .doctorUsername(appointment.getDoctor() != null ? appointment.getDoctor().getUsername() : null)
                .patientUsername(null) // Patient is now null
                .doctorLocation(appointment.getDoctor() != null ? appointment.getDoctor().getLocation() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentType(null) // Cleared
                .appointmentStatus(appointment.getStatus())
                .build();
    }

    @Transactional
    public DoctorAppointmentsDto updateCompletedAppointment(int appointmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentDoctor = (User) auth.getPrincipal();

        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }

        // Authorization check: Verify the logged-in doctor owns this appointment
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() != currentDoctor.getId()) {
            throw new SecurityException("You are not authorized to complete this appointment");
        }

        appointment.setStatus(AppointmentStatus.completed);
        appointmentRepository.save(appointment);

        return DoctorAppointmentsDto.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : 0)
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : 0)
                .doctorName(appointment.getDoctor() != null ? appointment.getDoctor().getName() : null)
                .patientName(appointment.getPatient() != null ? appointment.getPatient().getName() : null)
                .doctorUsername(appointment.getDoctor() != null ? appointment.getDoctor().getUsername() : null)
                .patientUsername(appointment.getPatient() != null ? appointment.getPatient().getUsername() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentType(appointment.getAppType())
                .appointmentStatus(appointment.getStatus())
                .build();
    }

}
