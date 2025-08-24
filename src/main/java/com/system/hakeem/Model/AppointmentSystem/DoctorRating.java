package com.system.hakeem.Model.AppointmentSystem;

import com.system.hakeem.Model.UserManagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "hakeem_schema" , name = "Doctor_Rating")
public class DoctorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "rating", nullable = false)
    private Integer rating; // between 1-5

    @Column(name = "rated_at")
    private LocalDate ratedAt;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "Doctor_id", referencedColumnName = "id")
    private User doctor;

}
