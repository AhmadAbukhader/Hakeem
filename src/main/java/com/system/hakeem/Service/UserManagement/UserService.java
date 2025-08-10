package com.system.hakeem.Service.UserManagement;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorDto;
import com.system.hakeem.Model.UserManagement.Role;
import com.system.hakeem.Model.UserManagement.Type;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.AppointmentSystem.DoctorRatingRepository;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private DoctorRatingRepository doctorRatingRepository;
    @Autowired
    private UserRepository userRepository;

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public List<DoctorDto> getDoctors(String location , String specialization, Boolean rated, Pageable pageable){
        Role role = Role.builder().id(2).role(Type.DOCTOR).build();
        List<DoctorDto> doctors = null;
        Page<User> users = null;

        if (location == null && specialization == null){
             users = userRepository.findAllByRole(role , pageable);
        }else if (location == null && specialization != null) {
            users = userRepository.findAllByRoleAndSpecialization(role, specialization , pageable);
        }else if (location != null && specialization == null){
            users = userRepository.findAllByRoleAndLocation(role, location ,pageable);
        }else{
            users = userRepository.findAllByRoleAndSpecializationAndLocation(role, specialization, location , pageable);
        }
        doctors = users.stream().map(
                user -> DoctorDto.builder()
                        .rating(doctorRatingRepository.findAverageRatingByDoctorId(user.getId())
                                != null ?
                                doctorRatingRepository.findAverageRatingByDoctorId(user.getId()) : 0 )
                        .doctorId(user.getId())
                        .doctorName(user.getName())
                        .username(user.getUsername())
                        .dob(user.getDob())
                        .age(user.getAge())
                        .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber().longValue() : 0L)
                        .specialization(user.getSpecialization())
                        .location(user.getLocation())
                        .gender(user.getGender())
                        .build()
        ).collect(Collectors.toList());

        if (rated != null && rated)
            doctors.sort(Comparator.comparingDouble(DoctorDto::getRating).reversed());

        return doctors;
    }

}
