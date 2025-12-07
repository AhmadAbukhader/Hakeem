package com.system.hakeem.Service.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceUnitRequest;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Repository.EmergencySystem.AmbulanceUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AmbulanceUnitService {

    private final AmbulanceUnitRepository ambulanceUnitRepository;

    public AmbulanceUnit createUnit(CreateAmbulanceUnitRequest unit){
        AmbulanceUnit ambulanceUnit = AmbulanceUnit.builder()
                .contactNumber(unit.getContactNumber())
                .unitName(unit.getUnitName())
                .address(unit.getAddress())
                .license(unit.getLicense())
                .build();
        ambulanceUnitRepository.save(ambulanceUnit);
        return ambulanceUnit;
    }

    public AmbulanceUnit getUnit(String name){
        return ambulanceUnitRepository.findByUnitName(name);
    }
}

