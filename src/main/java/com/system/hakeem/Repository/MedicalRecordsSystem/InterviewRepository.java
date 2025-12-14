package com.system.hakeem.Repository.MedicalRecordsSystem;

import com.system.hakeem.Model.MedicalRecordsSystem.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    List<Interview> findByPatientId(Integer patientId);
}
