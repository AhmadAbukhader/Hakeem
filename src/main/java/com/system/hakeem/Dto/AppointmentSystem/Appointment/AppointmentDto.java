package com.system.hakeem.Dto.AppointmentSystem.Appointment;

import com.system.hakeem.Model.AppointmentSystem.AppointmentStatus;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@Schema(description = "Appointment information")
public class AppointmentDto {

    @Schema(description = "Appointment ID", example = "1")
    private int appointmentId;

    @Schema(description = "Appointment date and time", example = "2024-12-20T10:00:00")
    private LocalDateTime appointmentTime;

    @Schema(description = "Whether the appointment slot is available", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Type of appointment", allowableValues = { "checkup", "followup", "consultation",
            "emergency" }, example = "checkup")
    private AppointmentType appointmentType;

    @Schema(description = "Status of the appointment", allowableValues = { "scheduled", "completed",
            "cancelled" }, example = "scheduled")
    private AppointmentStatus appointmentStatus;

    @Schema(description = "Doctor ID", example = "1")
    private int doctorId;

    @Schema(description = "Doctor's username", example = "dr_smith")
    private String doctorUserName;
}
