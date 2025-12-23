package com.system.hakeem.Repository.MedicalRecordsSystem;

import com.system.hakeem.Model.MedicalRecordsSystem.RiskFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskFactorRepository extends JpaRepository<RiskFactor, Integer> {
    List<RiskFactor> findByPatientId(Integer patientId);
}
