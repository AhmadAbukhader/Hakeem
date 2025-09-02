package com.system.hakeem.Dto.AppointmentSystem.Appointment;

import com.system.hakeem.Model.AppointmentSystem.Appointment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentMapper {

    public List<AppointmentDto> mapAppointments(List<Appointment> appointments){
        return appointments.stream().map(
                app -> AppointmentDto.builder()
                        .appointmentId(app.getId())
                        .appointmentTime(app.getAppointmentDate())
                        .isAvailable(app.getIsAvailable())
                        .appointmentType(app.getAppType())
                        .appointmentStatus(app.getStatus())
                        .doctorId(app.getDoctor().getId())
                        .doctorUserName(app.getDoctor().getUsername())
                        .build()
        ).toList();

    }

}
