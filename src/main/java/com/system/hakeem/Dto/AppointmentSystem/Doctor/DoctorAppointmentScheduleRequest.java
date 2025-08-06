package com.system.hakeem.Dto.AppointmentSystem.Doctor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorAppointmentScheduleRequest {
    @JsonProperty("appointment_date")
    private LocalDateTime appointmentDateTime;
}

//appointment table
/*
DoctorId    INTEGER
PatientId    INTEGER
APPType      ENUM
Status         ENUM
is_available    Boolean
*/