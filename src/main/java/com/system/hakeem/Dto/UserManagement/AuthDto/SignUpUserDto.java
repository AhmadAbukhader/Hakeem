package com.system.hakeem.Dto.UserManagement.AuthDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;

    @JsonProperty("dob")
    private Date dob;

    @JsonProperty("gender")
    private Boolean gender;

    @JsonProperty("blood_type")
    private String bloodType;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("ph_num")
    private Long phNum;

    @JsonProperty("specialization")
    private String specialization;

    @JsonProperty("license")
    private Integer license;

    @JsonProperty("description")
    private String description;

    @JsonProperty("y")
    private double longitude;

    @JsonProperty("x")
    private double latitude;

    @JsonProperty("role")
    private String role;

}
