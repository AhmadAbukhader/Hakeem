package com.system.hakeem.Repository.EmergencySystem;

import com.system.hakeem.Model.EmergencySystem.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AmbulanceRepository extends JpaRepository<Ambulance, Integer> {

    // Find the closest available ambulance to a user location
    @Query(value = "SELECT a.* FROM hakeem_schema.ambulance a " +
            "JOIN hakeem_schema.ambulance_location al ON a.ambulance_id = al.ambulance_id " +
            "WHERE a.status = 'AVAILABLE' " +
            "ORDER BY al.location <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) " +
            "LIMIT 1",
            nativeQuery = true)
    Ambulance findClosestAvailableAmbulance(@Param("lat") double latitude,
                                            @Param("lng") double longitude);


    Optional<Ambulance> findAmbulanceByPlateNumber(String plateNumber);

}

