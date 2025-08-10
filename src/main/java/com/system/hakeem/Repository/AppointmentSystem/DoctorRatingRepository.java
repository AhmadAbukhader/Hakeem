package com.system.hakeem.Repository.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRatingRepository extends JpaRepository<DoctorRating, Integer> {
    List<DoctorRating> findByDoctorId(int doctorId);

    @Query(value = "SELECT AVG(rating) FROM doctor_rating WHERE doctor_id = :doctorId GROUP BY doctor_id", nativeQuery = true)
    Double findAverageRatingByDoctorId(@Param("doctorId") int doctorId);
}
