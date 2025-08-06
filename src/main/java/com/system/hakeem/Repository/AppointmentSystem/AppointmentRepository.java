package com.system.hakeem.Repository.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment findByAppointmentDate(LocalDateTime appointmentDate);

    List<Appointment> findByPatientId(int patientId);

    List<Appointment> findByDoctorId(int doctorId);

    @Query(value = "SELECT * FROM appointment WHERE appointment_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Appointment> findByAppointmentDateBetweenNative(@Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);

    void deleteById(int id);

    List<Appointment> findByIsAvailable(Boolean isAvailable);

    List<Appointment> findByIsAvailableAndDoctorId(Boolean isAvailable , int doctorId);
}
