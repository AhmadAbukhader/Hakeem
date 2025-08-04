package com.system.hakeem.Dto.UserManagement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginUserDto {

    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;

}
