package com.system.hakeem.Dto.EmergencySystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAmbulanceRequest {

    @JsonProperty("UnitId")
    private int unitId ;
    @JsonProperty("y")
    private double longitude ;
    @JsonProperty("x")
    private double latitude;
    @JsonProperty("PlateNumber")
    private String plateNumber;
}
