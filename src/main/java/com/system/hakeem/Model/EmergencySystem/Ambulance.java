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

    @Column(nullable = false, unique = true, length = 50)
    private String plateNumber;

    @OneToOne
    @JoinColumn(name = "paramedic_id", nullable = false, unique = true)
    @JsonIgnore
    private User paramedic;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ambulance_status default 'AVAILABLE'")
    private AmbulanceStatus status;

}
