package com.system.hakeem.Dto.EmergencySystem.LocationDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLocationDto {
    private int userId;
    private double latitude;
    private double longitude;
}
