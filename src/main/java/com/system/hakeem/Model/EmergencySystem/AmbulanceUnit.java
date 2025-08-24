package com.system.hakeem.Model.EmergencySystem;

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
@Table(name = "ambulance_unit", schema = "hakeem_schema")
public class AmbulanceUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int unitId;

    @Column(nullable = false, length = 100)
    private String unitName;

    private Integer contactNumber;

    private String address;

    private Integer license;

}

