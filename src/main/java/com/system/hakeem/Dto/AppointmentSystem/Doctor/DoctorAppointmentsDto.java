package com.system.hakeem.Dto.AppointmentSystem.Doctor;

import com.system.hakeem.Model.AppointmentSystem.AppointmentStatus;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Doctor appointment information")
public class DoctorAppointmentsDto {
    @Schema(description = "Appointment ID", example = "1")
    private int id;

    @Schema(description = "Doctor ID", example = "1")
    private int doctorId;

    @Schema(description = "Patient ID", example = "1")
    private int patientId;

    @Schema(description = "Doctor's full name", example = "Dr. John Smith")
    private String doctorName;

    @Schema(description = "Patient's full name", example = "Jane Doe")
    private String patientName;

    @Schema(description = "Patient's username", example = "jane_doe")
    private String patientUsername;

    @Schema(description = "Doctor's username", example = "dr_smith")
    private String doctorUsername;

    @Schema(description = "Appointment date and time", example = "2024-12-20T10:00:00")
    private LocalDateTime appointmentDate;

    @Schema(description = "Type of appointment", allowableValues = { "checkup", "followup", "consultation",
            "emergency" }, example = "checkup")
    private AppointmentType appointmentType;

    @Schema(description = "Status of the appointment", allowableValues = { "scheduled", "completed",
            "cancelled" }, example = "scheduled")
    private AppointmentStatus appointmentStatus;
}
