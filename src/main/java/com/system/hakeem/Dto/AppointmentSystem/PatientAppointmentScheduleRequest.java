package com.system.hakeem.Dto.AppointmentSystem;

import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientAppointmentScheduleRequest {
  private AppointmentType appointmentType;
  private LocalDateTime appointmentDateTime;
}

//appointment table
/*
Status         ENUM
is_available    Boolean
*/