package com.system.hakeem.Repository.MedicalRecordsSystem;

import com.system.hakeem.Model.MedicalRecordsSystem.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Integer> {
    List<QuestionAnswer> findByInterviewId(Integer interviewId);
}
