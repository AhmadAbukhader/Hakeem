package com.system.hakeem.Model.EmergencySystem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.hakeem.Model.UserManagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "emergency_request", schema = "hakeem_schema")
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private User patient;

    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    @JsonIgnore
    private Ambulance ambulance;

    @Column(nullable = false, columnDefinition = "geography(Point,4326)")
    private Point patientLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "emergency_request_status default 'PENDING'")
    @Builder.Default
    private EmergencyRequestStatus status = EmergencyRequestStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    private String notes; // Optional notes about the emergency
}
