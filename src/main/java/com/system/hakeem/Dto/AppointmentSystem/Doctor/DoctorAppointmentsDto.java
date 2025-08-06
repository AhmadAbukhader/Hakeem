package com.system.hakeem.Dto.AppointmentSystem.Doctor;

import com.system.hakeem.Model.AppointmentSystem.AppointmentStatus;
import com.system.hakeem.Model.AppointmentSystem.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAppointmentsDto {
    private int id ;
    private int doctorId ;
    private int patientId ;
    private String doctorName ;
    private String patientName ;

    private String patientUsername ;
    private String doctorUsername ;

    private LocalDateTime appointmentDate ;
    private AppointmentType appointmentType ;
    private AppointmentStatus appointmentStatus ;

}
