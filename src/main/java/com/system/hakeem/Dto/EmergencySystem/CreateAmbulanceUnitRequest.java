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
public class CreateAmbulanceUnitRequest {

    @JsonProperty("UnitName")
    private String unitName;

    @JsonProperty("ContactNumber")
    private Integer contactNumber;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("License")
    private Integer license;
}
