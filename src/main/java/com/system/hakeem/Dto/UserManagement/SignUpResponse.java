package com.system.hakeem.Dto.UserManagement;

import com.system.hakeem.Model.UserManagement.Role;
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
