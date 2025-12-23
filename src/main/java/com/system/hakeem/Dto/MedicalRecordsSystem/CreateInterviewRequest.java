package com.system.hakeem.Dto.MedicalRecordsSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInterviewRequest {

    @JsonProperty("questions_and_answers")
    private List<QuestionAnswerDto> questionsAndAnswers;

    @JsonProperty("symptoms")
    private List<String> symptoms;

    @JsonProperty("diagnoses")
    private List<DiagnoseRequestDto> diagnoses;

    @JsonProperty("doctor_recommendation")
    private DoctorRecommendationRequestDto doctorRecommendation;

    @JsonProperty("triage")
    private String triage;

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
    public static class DiagnoseRequestDto {
        @JsonProperty("name")
        private String name;

        @JsonProperty("probability")
        private Float probability;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DoctorRecommendationRequestDto {
        @JsonProperty("name")
        private String name;
    }

}
