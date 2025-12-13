package com.system.hakeem.Dto.AppointmentSystem.Patient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Request to schedule an appointment with a doctor")
public class PatientAppointmentScheduleRequest {
  @JsonProperty("appointment_type")
  @Schema(description = "Type of appointment", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
      "checkup", "followup", "consultation", "emergency" }, example = "checkup")
  private AppointmentType appointmentType;

  @JsonProperty("appointment_date")
  @Schema(description = "Date and time of the appointment", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-12-20T10:00:00")
  private LocalDateTime appointmentDateTime;

  @JsonProperty("doctor_id")
  @Schema(description = "ID of the doctor for the appointment", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
  private Integer doctorId;
}

// appointment table
/*
 * Status ENUM
 * is_available Boolean
 */