package com.system.hakeem.Repository.MedicalRecordsSystem;

import com.system.hakeem.Model.MedicalRecordsSystem.Diagnose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnoseRepository extends JpaRepository<Diagnose, Integer> {
    List<Diagnose> findByInterviewId(Integer interviewId);
}
