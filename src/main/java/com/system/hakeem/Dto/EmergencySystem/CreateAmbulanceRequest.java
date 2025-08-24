package com.system.hakeem.Dto.EmergencySystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAmbulanceRequest {

    private int unitId ;
    private double longitude ;
    private double latitude;
    private String plateNumber;
}
