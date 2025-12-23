package com.system.hakeem.Dto.MedicalRecordsSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRiskFactorRequest {

    @JsonProperty("factor_name")
    private String factorName;

}
