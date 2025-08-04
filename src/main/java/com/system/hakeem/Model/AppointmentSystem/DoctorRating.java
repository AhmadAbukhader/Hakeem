package com.system.hakeem.Model.AppointmentSystem;

import com.system.hakeem.Model.UserManagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "Doctor_Rating")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer rating; // between 1-5

    @Column(name = "ratedAt", nullable = false)
    private LocalDate ratedAt;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "Doctor_id", referencedColumnName = "id", nullable = false)
    private User doctor;

}
