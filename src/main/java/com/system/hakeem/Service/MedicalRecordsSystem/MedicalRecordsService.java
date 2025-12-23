package com.system.hakeem.Service.MedicalRecordsSystem;

import com.system.hakeem.Dto.MedicalRecordsSystem.CreateInterviewRequest;
import com.system.hakeem.Dto.MedicalRecordsSystem.InterviewResponse;
import com.system.hakeem.Model.MedicalRecordsSystem.Diagnose;
import com.system.hakeem.Model.MedicalRecordsSystem.DoctorRecommendation;
import com.system.hakeem.Model.MedicalRecordsSystem.Interview;
import com.system.hakeem.Model.MedicalRecordsSystem.QuestionAnswer;
import com.system.hakeem.Model.MedicalRecordsSystem.Symptom;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.MedicalRecordsSystem.DiagnoseRepository;
import com.system.hakeem.Repository.MedicalRecordsSystem.DoctorRecommendationRepository;
import com.system.hakeem.Repository.MedicalRecordsSystem.InterviewRepository;
import com.system.hakeem.Repository.MedicalRecordsSystem.QuestionAnswerRepository;
import com.system.hakeem.Repository.MedicalRecordsSystem.SymptomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordsService {

        private final InterviewRepository interviewRepository;
        private final QuestionAnswerRepository questionAnswerRepository;
        private final SymptomRepository symptomRepository;
        private final DiagnoseRepository diagnoseRepository;
        private final DoctorRecommendationRepository doctorRecommendationRepository;

        @Transactional
        public InterviewResponse createInterview(CreateInterviewRequest request) {
                // Get the current authenticated user (patient)
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User patient = (User) auth.getPrincipal();

                // Validate that we have at least one question and answer
                if (request.getQuestionsAndAnswers() == null || request.getQuestionsAndAnswers().isEmpty()) {
                        throw new IllegalArgumentException("At least one question and answer pair is required");
                }

                // Create and save one Interview record (header)
                LocalDateTime startedAt = LocalDateTime.now();
                Interview interview = Interview.builder()
                                .patient(patient)
                                .startedAt(startedAt)
                                .triage(request.getTriage())
                                .build();

                final Interview savedInterview = interviewRepository.save(interview);

                // Create and save QuestionAnswer records (detail) for each Q&A pair
                List<QuestionAnswer> questionAnswers = request.getQuestionsAndAnswers().stream()
                                .map(qa -> QuestionAnswer.builder()
                                                .interview(savedInterview)
                                                .question(qa.getQuestion())
                                                .answer(qa.getAnswer())
                                                .build())
                                .collect(Collectors.toList());

                questionAnswers = questionAnswerRepository.saveAll(questionAnswers);

                // Save symptoms - link them to the interview
                List<Symptom> symptoms = List.of();
                if (request.getSymptoms() != null && !request.getSymptoms().isEmpty()) {
                        symptoms = request.getSymptoms().stream()
                                        .filter(symptomName -> symptomName != null && !symptomName.trim().isEmpty())
                                        .map(symptomName -> Symptom.builder()
                                                        .interview(savedInterview)
                                                        .name(symptomName.trim())
                                                        .build())
                                        .collect(Collectors.toList());

                        if (!symptoms.isEmpty()) {
                                symptoms = symptomRepository.saveAll(symptoms);
                        }
                }

                // Save diagnoses from request - link them to the interview
                List<Diagnose> diagnoses = List.of();
                if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
                        diagnoses = request.getDiagnoses().stream()
                                        .filter(d -> d.getName() != null && !d.getName().trim().isEmpty())
                                        .map(d -> Diagnose.builder()
                                                        .interview(savedInterview)
                                                        .name(d.getName().trim())
                                                        .probability(d.getProbability())
                                                        .build())
                                        .collect(Collectors.toList());

                        if (!diagnoses.isEmpty()) {
                                diagnoses = diagnoseRepository.saveAll(diagnoses);
                        }
                }

                // Save doctor recommendation from request - link it to the interview
                DoctorRecommendation recommendation = null;
                if (request.getDoctorRecommendation() != null
                                && request.getDoctorRecommendation().getName() != null
                                && !request.getDoctorRecommendation().getName().trim().isEmpty()) {
                        recommendation = DoctorRecommendation.builder()
                                        .interview(savedInterview)
                                        .name(request.getDoctorRecommendation().getName().trim())
                                        .build();

                        recommendation = doctorRecommendationRepository.save(recommendation);
                }

                // Build and return response
                return buildInterviewResponse(savedInterview, questionAnswers, symptoms, diagnoses, recommendation);
        }

        /**
         * Build the response DTO from entities
         */
        private InterviewResponse buildInterviewResponse(
                        Interview interview,
                        List<QuestionAnswer> questionAnswers,
                        List<Symptom> symptoms,
                        List<Diagnose> diagnoses,
                        DoctorRecommendation recommendation) {

                // Build Q&A list from question answers
                List<InterviewResponse.QuestionAnswerDto> qaList = questionAnswers.stream()
                                .map(qa -> InterviewResponse.QuestionAnswerDto.builder()
                                                .question(qa.getQuestion())
                                                .answer(qa.getAnswer())
                                                .build())
                                .collect(Collectors.toList());

                List<InterviewResponse.SymptomDto> symptomDtos = symptoms.stream()
                                .map(s -> InterviewResponse.SymptomDto.builder()
                                                .id(s.getId())
                                                .name(s.getName())
                                                .build())
                                .collect(Collectors.toList());

                List<InterviewResponse.DiagnoseDto> diagnoseDtos = diagnoses.stream()
                                .map(d -> InterviewResponse.DiagnoseDto.builder()
                                                .id(d.getId())
                                                .name(d.getName())
                                                .probability(d.getProbability())
                                                .build())
                                .collect(Collectors.toList());

                InterviewResponse.DoctorRecommendationDto recommendationDto = null;
                if (recommendation != null) {
                        recommendationDto = InterviewResponse.DoctorRecommendationDto.builder()
                                        .id(recommendation.getId())
                                        .name(recommendation.getName())
                                        .build();
                }

                return InterviewResponse.builder()
                                .interviewId(interview.getId())
                                .patientId(interview.getPatient().getId())
                                .questionsAndAnswers(qaList)
                                .startedAt(interview.getStartedAt())
                                .symptoms(symptomDtos)
                                .diagnoses(diagnoseDtos)
                                .doctorRecommendation(recommendationDto)
                                .triage(interview.getTriage())
                                .build();
        }

        /**
         * Get interview data by interview ID
         */
        public InterviewResponse getInterviewById(Integer interviewId) {
                Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
                if (interviewOpt.isEmpty()) {
                        throw new IllegalArgumentException("Interview not found with id: " + interviewId);
                }

                Interview interview = interviewOpt.get();

                // Load all related data
                List<QuestionAnswer> questionAnswers = questionAnswerRepository.findByInterviewId(interviewId);
                List<Symptom> symptoms = symptomRepository.findByInterviewId(interviewId);
                List<Diagnose> diagnoses = diagnoseRepository.findByInterviewId(interviewId);
                List<DoctorRecommendation> recommendations = doctorRecommendationRepository
                                .findByInterviewId(interviewId);

                DoctorRecommendation recommendation = recommendations.isEmpty() ? null : recommendations.get(0);

                return buildInterviewResponse(interview, questionAnswers, symptoms, diagnoses, recommendation);
        }

        /**
         * Get all interviews for a specific user by user ID
         */
        public List<InterviewResponse> getInterviewsByUserId(Integer userId) {
                // Validate userId
                if (userId == null || userId <= 0) {
                        throw new IllegalArgumentException("Invalid user ID: " + userId);
                }

                // Get all interviews for this user
                List<Interview> interviews = interviewRepository.findByPatientId(userId);

                if (interviews.isEmpty()) {
                        return new ArrayList<>();
                }

                // Build response for each interview
                return interviews.stream()
                                .map(interview -> {
                                        Integer interviewId = interview.getId();
                                        List<QuestionAnswer> questionAnswers = questionAnswerRepository
                                                        .findByInterviewId(interviewId);
                                        List<Symptom> symptoms = symptomRepository.findByInterviewId(interviewId);
                                        List<Diagnose> diagnoses = diagnoseRepository.findByInterviewId(interviewId);
                                        List<DoctorRecommendation> recommendations = doctorRecommendationRepository
                                                        .findByInterviewId(interviewId);

                                        DoctorRecommendation recommendation = recommendations.isEmpty() ? null
                                                        : recommendations.get(0);

                                        return buildInterviewResponse(interview, questionAnswers, symptoms, diagnoses,
                                                        recommendation);
                                })
                                .collect(Collectors.toList());
        }
}
