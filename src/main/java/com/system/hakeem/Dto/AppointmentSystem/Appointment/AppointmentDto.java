package com.system.hakeem.Dto.AppointmentSystem.Appointment;

import com.system.hakeem.Model.AppointmentSystem.AppointmentStatus;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AppointmentDto {

    private int appointmentId;
    private LocalDateTime appointmentTime;
    private Boolean isAvailable ;
    private AppointmentType appointmentType;
    private AppointmentStatus appointmentStatus;
    private int doctorId ;
    private String doctorUserName;

}
