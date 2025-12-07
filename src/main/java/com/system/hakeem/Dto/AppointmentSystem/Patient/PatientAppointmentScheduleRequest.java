package com.system.hakeem.Dto.AppointmentSystem.Patient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientAppointmentScheduleRequest {
  @JsonProperty("appointment_type")
  private AppointmentType appointmentType;
  @JsonProperty("appointment_date")
  private LocalDateTime appointmentDateTime;
  @JsonProperty("doctor_id")
  private Integer doctorId;
}

// appointment table
/*
 * Status ENUM
 * is_available Boolean
 */