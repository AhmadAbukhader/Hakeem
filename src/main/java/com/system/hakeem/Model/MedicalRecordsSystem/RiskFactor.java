package com.system.hakeem.Model.MedicalRecordsSystem;

import com.system.hakeem.Model.UserManagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "hakeem_schema", name = "risk_factor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private User patient;

    @Column(name = "factor_name", nullable = false, length = 255)
    private String factorName;

}
