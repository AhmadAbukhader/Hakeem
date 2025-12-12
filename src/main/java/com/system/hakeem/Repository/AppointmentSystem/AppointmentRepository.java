package com.system.hakeem.Repository.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
        Appointment findByAppointmentDate(LocalDateTime appointmentDate);

        Appointment findByAppointmentDateAndDoctorId(LocalDateTime appointmentDate, int doctorId);

        Appointment findByAppointmentDateAndDoctorIdAndIsAvailable(LocalDateTime appointmentDate, int doctorId,
                        Boolean isAvailable);

        List<Appointment> findByPatientId(int patientId);

        List<Appointment> findByDoctorId(int doctorId);

        Appointment findById(int id);

        List<Appointment> findByIsAvailable(Boolean isAvailable);

        List<Appointment> findByIsAvailableAndDoctorId(Boolean isAvailable, int doctorId);

        List<Appointment> findByStatusAndDoctorId(com.system.hakeem.Model.AppointmentSystem.AppointmentStatus status,
                        int doctorId);
}
