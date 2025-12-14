package com.system.hakeem.Dto.EmergencySystem.LocationDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmbulanceLocationDto {
    // Note: @NotNull not needed on primitives (int, double) - they can't be null
    @Min(value = 1, message = "Ambulance ID must be greater than 0")
    private int ambulanceId;

    @Min(value = 0, message = "Speed must be 0 or greater")
    @Max(value = 300, message = "Speed must not exceed 300 km/h")
    private double speed;

    @Min(value = 0, message = "Direction must be 0 or greater")
    @Max(value = 360, message = "Direction must not exceed 360 degrees")
    private double direction;

    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private double latitude;

    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private double longitude;
}
