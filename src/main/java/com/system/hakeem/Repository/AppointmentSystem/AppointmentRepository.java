package com.system.hakeem.Repository.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment findByAppointmentDate(LocalDateTime appointmentDate);

    List<Appointment> findByIsAvailable(Boolean isAvailable);
}
