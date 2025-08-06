package com.system.hakeem.Dto.AppointmentSystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private int rating;
    private LocalDate ratedAt ;
    private String description;
    private String username ;
}
