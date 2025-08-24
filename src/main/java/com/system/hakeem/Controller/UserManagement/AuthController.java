package com.system.hakeem.Controller.UserManagement;


import com.system.hakeem.Dto.UserManagement.LoginResponse;
import com.system.hakeem.Dto.UserManagement.LoginUserDto;
import com.system.hakeem.Dto.UserManagement.SignUpResponse;
import com.system.hakeem.Dto.UserManagement.SignUpUserDto;
import com.system.hakeem.Dto.UserManagement.password.ForgotPasswordRequest;
import com.system.hakeem.Dto.UserManagement.password.ResetPasswordRequest;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Model.UserManagement.Role;
import com.system.hakeem.Service.UserManagement.AuthService;
import com.system.hakeem.Service.UserManagement.JwtService;
import com.system.hakeem.Service.UserManagement.ResetPasswordService;
import com.system.hakeem.Service.UserManagement.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final ResetPasswordService resetPasswordService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp (@RequestBody SignUpUserDto request){
        SignUpResponse newUser = authService.signUp(request);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginUserDto request){
        LoginResponse authenticatedUser = authService.authenticate(request);
        authenticatedUser.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(authenticatedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword (@RequestBody ForgotPasswordRequest request){
        String code = resetPasswordService.generateCode(request.getUsername());
        return ResponseEntity.ok("the verification code has been sent ");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword (@RequestBody ResetPasswordRequest request){
        try{
            resetPasswordService.resetPassword(request);
            return ResponseEntity.ok("the password has been changed");
        }catch (Exception e){
            return ResponseEntity.ok("the password has not been changed , "+e.getMessage());
        }
    }

}
