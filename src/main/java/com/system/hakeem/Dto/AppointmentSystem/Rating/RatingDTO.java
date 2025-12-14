package com.system.hakeem.Dto.AppointmentSystem.Rating;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("rating")
    private int rating;

    @JsonProperty("description")
    private String description;

    @JsonProperty("rated_at")
    private LocalDate ratedAt;

    @JsonProperty("username")
    private String username ;

    @JsonProperty("name")
    private String name ;
}
