package com.system.hakeem.Dto.EmergencySystem;

import com.system.hakeem.Model.EmergencySystem.EmergencyRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyRequestResponseDto {
    private Integer requestId;
    private Integer patientId;
    private String patientName;
    private Integer ambulanceId;
    private String plateNumber;
    private Integer paramedicId;
    private String paramedicName;
    private double patientLatitude;
    private double patientLongitude;
    private EmergencyRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private String notes;
}
