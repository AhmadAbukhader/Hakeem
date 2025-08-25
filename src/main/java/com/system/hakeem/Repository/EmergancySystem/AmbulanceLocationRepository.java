package com.system.hakeem.Repository.EmergancySystem;

import com.system.hakeem.Model.EmergencySystem.AmbulanceLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbulanceLocationRepository extends JpaRepository<AmbulanceLocation, Integer> {

    AmbulanceLocation findAmbulanceLocationByAmbulance_AmbulanceId(int ambulanceId);
}
