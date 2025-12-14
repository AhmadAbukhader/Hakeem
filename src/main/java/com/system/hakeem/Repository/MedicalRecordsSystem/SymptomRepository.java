package com.system.hakeem.Repository.MedicalRecordsSystem;

import com.system.hakeem.Model.MedicalRecordsSystem.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Integer> {
    List<Symptom> findByInterviewId(Integer interviewId);
}
