package com.system.hakeem.Dto.EmergencySystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAmbulanceUnitRequest {

    private String unitName;

    private Integer contactNumber;

    private String address;

    private Integer license;


}
