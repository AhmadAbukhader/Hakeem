package com.system.hakeem.Dto.AppointmentSystem.Doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {
    private int doctorId;
    private String username;
    private String doctorName;
    private Date dob;
    private Boolean gender ;
    private int age ;
    private long phoneNumber ;
    private String location;
    private String specialization ;
}
