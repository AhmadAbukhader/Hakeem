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
public class RiskFactorResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("patient_id")
    private Integer patientId;

    @JsonProperty("factor_name")
    private String factorName;

}
