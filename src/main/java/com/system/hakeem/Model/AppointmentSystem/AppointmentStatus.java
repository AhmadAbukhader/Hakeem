package com.system.hakeem.Model.AppointmentSystem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Appointment status enumeration", allowableValues = { "scheduled", "completed",
        "cancelled" }, example = "scheduled")
public enum AppointmentStatus {
    @Schema(description = "Appointment is scheduled and pending", example = "scheduled")
    scheduled,

    @Schema(description = "Appointment has been completed", example = "completed")
    completed,

    @Schema(description = "Appointment has been cancelled", example = "cancelled")
    cancelled
}
