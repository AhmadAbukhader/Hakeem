package com.system.hakeem.Dto.EmergencySystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAmbulanceRequest {

    /**
     * Note: Property name "x" maps to latitude (north-south coordinate)
     * This naming is kept for API compatibility with existing frontend
     * Note: @NotNull not needed on primitives - validation done in service layer
     */
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    @JsonProperty("x")
    private double latitude;

    /**
     * Note: Property name "y" maps to longitude (east-west coordinate)
     * This naming is kept for API compatibility with existing frontend
     * Note: @NotNull not needed on primitives - validation done in service layer
     */
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    @JsonProperty("y")
    private double longitude;

    @NotBlank(message = "Plate number is required")
    @JsonProperty("PlateNumber")
    private String plateNumber;
}
