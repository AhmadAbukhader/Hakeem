package com.system.hakeem.Repository.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRatingRepository extends JpaRepository<DoctorRating, Integer> {
    List<DoctorRating> findByDoctorId(int doctorId);
}
