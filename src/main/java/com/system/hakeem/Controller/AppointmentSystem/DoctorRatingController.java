package com.system.hakeem.Controller.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Rating.RatingDTO;
import com.system.hakeem.Service.AppointmentSystem.DoctorRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorRatingController {

    private final DoctorRatingService doctorRatingService;

    @PostMapping("/rate")
    @PreAuthorize("hasAnyRole('PATIENT')")
    public ResponseEntity<RatingDTO> ratingDoctor(@RequestBody RatingDTO rating){
        try {
            doctorRatingService.rate(rating);
            return ResponseEntity.ok(rating);
        } catch (RuntimeException e) {
            rating.setDescription(e.getMessage());
            return ResponseEntity.badRequest().body(rating);
        }
    }

    @GetMapping("/rating")
    public ResponseEntity<List<RatingDTO>> getDoctorRating(@RequestParam String username){
        try {
            return ResponseEntity.ok(doctorRatingService.getDoctorRatings(username));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
