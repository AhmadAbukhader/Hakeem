package com.system.hakeem.Dto.MedicalRecordsSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewResponse {

    @JsonProperty("interview_id")
    private Integer interviewId;

    @JsonProperty("patient_id")
    private Integer patientId;

    @JsonProperty("questions_and_answers")
    private List<QuestionAnswerDto> questionsAndAnswers;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("symptoms")
    private List<SymptomDto> symptoms;

    @JsonProperty("diagnoses")
    private List<DiagnoseDto> diagnoses;

    @JsonProperty("doctor_recommendation")
    private DoctorRecommendationDto doctorRecommendation;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SymptomDto {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiagnoseDto {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("probability")
        private Float probability;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionAnswerDto {
        @JsonProperty("question")
        private String question;

        @JsonProperty("answer")
        private String answer;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DoctorRecommendationDto {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;
    }

}
