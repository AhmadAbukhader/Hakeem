package com.system.hakeem.Controller.MedicalRecordsSystem;

import com.system.hakeem.Dto.MedicalRecordsSystem.CreateInterviewRequest;
import com.system.hakeem.Dto.MedicalRecordsSystem.InterviewResponse;
import com.system.hakeem.Service.MedicalRecordsSystem.MedicalRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records Management", description = "APIs for managing medical interviews, symptoms, diagnoses, and doctor recommendations")
public class MedicalRecordsController {

    private final MedicalRecordsService medicalRecordsService;

    @PostMapping("/interview")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Create a new medical interview", description = "Creates a new medical interview record with questions, answers, and symptoms. "
            +
            "The system automatically calculates diagnoses and generates doctor specialty recommendations based on the provided information. "
            +
            "Only patients can create interviews.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interview created successfully with diagnoses and doctor recommendation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InterviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InterviewResponse> createInterview(
            @Parameter(description = "Interview data including question, answer, and list of symptoms", required = true) @RequestBody CreateInterviewRequest request) {
        try {
            InterviewResponse response = medicalRecordsService.createInterview(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/interview/{id}")
    @Operation(summary = "Get interview by ID", description = "Retrieves a complete medical interview record by its unique ID, including all questions, answers, symptoms, diagnoses, and doctor recommendations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interview found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InterviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid interview ID"),
            @ApiResponse(responseCode = "404", description = "Interview not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InterviewResponse> getInterviewById(
            @Parameter(description = "Interview unique identifier", required = true, example = "1") @PathVariable Integer id) {
        try {
            InterviewResponse response = medicalRecordsService.getInterviewById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/interviews/{userId}")
    @PreAuthorize("hasAnyRole('PATIENT' , 'DOCTOR')")
    @Operation(summary = "Get all interviews for a user", description = "Retrieves all medical interview records for the specified user ID. The user ID is provided as a path variable. Returns a list of all interviews with their complete data including questions, answers, symptoms, diagnoses, and doctor recommendations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved interviews", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InterviewResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient or Doctor role required"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InterviewResponse>> getInterviewsByUser(
            @Parameter(description = "User ID to retrieve interviews for", required = true, example = "1") @PathVariable Integer userId) {
        try {
            List<InterviewResponse> interviews = medicalRecordsService.getInterviewsByUserId(userId);
            return ResponseEntity.ok(interviews);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
