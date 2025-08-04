package com.system.hakeem.Service.AppointmentSystem;

import com.system.hakeem.Repository.AppointmentSystem.AppointmentRepository;
import com.system.hakeem.Repository.AppointmentSystem.DoctorRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorRatingService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRatingRepository doctorRatingRepository;

}
