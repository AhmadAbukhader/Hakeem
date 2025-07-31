package com.system.hakeem.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.hakeem.Model.Role;
import com.system.hakeem.Model.Type;
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

    @JsonProperty("location")
    private String location;

    @JsonProperty("role")
    private String role;

}
