package com.system.hakeem.Model.MedicalRecordsSystem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "hakeem_schema", name = "symptom")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id", nullable = false)
    private Interview interview;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

}
