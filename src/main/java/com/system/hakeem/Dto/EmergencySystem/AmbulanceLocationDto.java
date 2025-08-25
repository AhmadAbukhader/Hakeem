package com.system.hakeem.Dto.EmergencySystem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmbulanceLocationDto {
    private int ambulanceId;
    private double speed ;
    private double direction;
    private double latitude;
    private double longitude;
}
