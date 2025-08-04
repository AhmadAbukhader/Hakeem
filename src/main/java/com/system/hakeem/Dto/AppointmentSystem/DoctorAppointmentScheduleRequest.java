package com.system.hakeem.Dto.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorAppointmentScheduleRequest {
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