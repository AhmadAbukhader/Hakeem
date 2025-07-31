package com.system.hakeem.Dto;

import com.system.hakeem.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private String token;
    private String username ;
    private Role role;
}
