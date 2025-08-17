package com.system.hakeem.Dto.UserManagement.password;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @JsonProperty("username")
    private String email;

    @JsonProperty("password")
    private String newPassword;

    @JsonProperty("code")
    private String code ;
}
