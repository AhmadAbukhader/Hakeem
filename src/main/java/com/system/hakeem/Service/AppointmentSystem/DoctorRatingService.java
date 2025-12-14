package com.system.hakeem.Service.AppointmentSystem;

import com.system.hakeem.Dto.AppointmentSystem.Rating.RatingDTO;
import com.system.hakeem.Model.AppointmentSystem.DoctorRating;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.AppointmentSystem.DoctorRatingRepository;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorRatingService {

    @Autowired
    private DoctorRatingRepository doctorRatingRepository;
    @Autowired
    private UserRepository userRepository;

    public void rate(RatingDTO ratingDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User patient = (User) auth.getPrincipal();

        Optional<User> doctor = userRepository.findByUsername(ratingDTO.getUsername());
        if(doctor.isEmpty()){
            throw new RuntimeException("Doctor not found");
        }

        DoctorRating rating = DoctorRating
                .builder()
                .ratedAt(ratingDTO.getRatedAt())
                .rating(ratingDTO.getRating())
                .description(ratingDTO.getDescription())
                .patient(patient)
                .doctor(doctor.get())
                .build();
        doctorRatingRepository.save(rating);

    }

    public List<RatingDTO> getDoctorRatings (String username){
        Optional<User> doctor = userRepository.findByUsername(username);
        if(doctor.isEmpty()){
            throw new RuntimeException("Doctor not found");
        }

        List<DoctorRating> doctorRatings = doctorRatingRepository.findByDoctorId(doctor.get().getId());

        return doctorRatings.stream().map(
                e -> RatingDTO.builder()
                        .ratedAt(e.getRatedAt())
                        .rating(e.getRating())
                        .description(e.getDescription())
                        .username(e.getPatient().getUsername())
                        .name(e.getPatient().getName())
                        .build()
        ).collect(Collectors.toList());
    }

}
