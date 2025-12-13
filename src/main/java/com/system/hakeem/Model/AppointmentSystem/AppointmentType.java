package com.system.hakeem.Model.AppointmentSystem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Appointment type enumeration", allowableValues = { "checkup", "followup", "consultation",
        "emergency" }, example = "checkup")
public enum AppointmentType {
    @Schema(description = "Regular checkup appointment", example = "checkup")
    checkup,

    @Schema(description = "Follow-up appointment", example = "followup")
    followup,

    @Schema(description = "Consultation appointment", example = "consultation")
    consultation,

    @Schema(description = "Emergency appointment", example = "emergency")
    emergency
}
