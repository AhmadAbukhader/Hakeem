package com.system.hakeem.Dto.EmergencySystem;

import com.system.hakeem.Model.EmergencySystem.AmbulanceStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAmbulanceResponse {
    private int ambulance_id;
    private int ambulanceUnitId;
    private String plateNumber;
    private String paramedicName ;
    private int paramedicId ;
    private AmbulanceStatus ambulanceStatus;

}
