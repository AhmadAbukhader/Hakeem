package com.system.hakeem.Repository.EmergencySystem;

import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbulanceUnitRepository extends JpaRepository<AmbulanceUnit, Integer> {

    AmbulanceUnit findByUnitName(String unitName);

}

