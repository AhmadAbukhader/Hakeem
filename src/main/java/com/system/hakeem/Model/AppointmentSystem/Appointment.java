package com.system.hakeem.Model.AppointmentSystem;

import com.system.hakeem.Model.UserManagement.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "hakeem_schema", name = "Appointment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ADate", nullable = false)
    private LocalDateTime appointmentDate;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "APPType")
    private AppointmentType appType;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "Doctor_id", referencedColumnName = "id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "Patient_id", referencedColumnName = "id")
    private User patient;

}
