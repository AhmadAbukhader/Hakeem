package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Rating.RatingDTO;
import com.system.hakeem.Service.AppointmentSystem.DoctorRatingService;
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
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Tag(name = "Doctor Rating Management", description = "APIs for rating doctors and retrieving doctor ratings")
public class DoctorRatingController {

    private final DoctorRatingService doctorRatingService;

    @PostMapping("/rate")
    @PreAuthorize("hasAnyRole('PATIENT')")
    @Operation(summary = "Rate a doctor", description = "Allows a patient to submit a rating and review for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating submitted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RatingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid rating data or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Patient role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RatingDTO> ratingDoctor(
            @Parameter(description = "Rating information including doctor username, rating value, and description", required = true) @RequestBody RatingDTO rating) {
        try {
            doctorRatingService.rate(rating);
            return ResponseEntity.ok(rating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(rating);
        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rating")
    @Operation(summary = "Get doctor ratings", description = "Retrieves all ratings and reviews for a specific doctor by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor ratings", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RatingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid doctor username"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<RatingDTO>> getDoctorRating(
            @Parameter(description = "Doctor username to retrieve ratings for", required = true, example = "doctor123") @RequestParam String username) {
        try {
            List<RatingDTO> ratings = doctorRatingService.getDoctorRatings(username);
            return ResponseEntity.ok(ratings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
