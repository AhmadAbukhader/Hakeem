package com.system.hakeem.Dto.UserManagement.UserDto;

import com.system.hakeem.Model.UserManagement.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeDto {
    private int id;
    private String username;
    private String name;
    private Date dob;
    private Boolean gender;
    private String bloodType;
    private Integer age;
    private Integer weight;
    private Long phoneNumber;
    private String specialization;
    private Integer license;
    private String description;
    private Double latitude;
    private Double longitude;
    private Type role;
}
