package com.system.hakeem.Model.EmergencySystem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.hakeem.Model.UserManagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ambulance", schema = "hakeem_schema")
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ambulanceId;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    @JsonIgnore
    private AmbulanceUnit unit;

    @Column(nullable = false, unique = true, length = 50)
    private String plateNumber;

    @ManyToOne
    @JoinColumn(name = "paramedic_id")
    @JsonIgnore
    private User paramedic;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ambulance_status default 'AVAILABLE'")
    private AmbulanceStatus status;

}

