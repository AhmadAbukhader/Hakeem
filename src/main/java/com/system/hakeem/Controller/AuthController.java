package com.system.hakeem.Controller;


import com.system.hakeem.Dto.*;
import com.system.hakeem.Dto.password.ForgotPasswordRequest;
import com.system.hakeem.Dto.password.ResetPasswordRequest;
import com.system.hakeem.Service.AuthService;
import com.system.hakeem.Service.JwtService;
import com.system.hakeem.Service.ResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ResetTokenService resetTokenService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp (@RequestBody SignUpUserDto request){
        SignUpResponse newUser = authService.signUp(request);
        System.out.println("i am in the controller");
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginUserDto request){
        LoginResponse authenticatedUser = authService.authenticate(request);
        authenticatedUser.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(authenticatedUser);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
        resetTokenService.initiateResetPassword(request.getUsername());
        return ResponseEntity.ok("the reset token has been initiated");
    }

    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword (@RequestBody ResetPasswordRequest request){
        try{
            resetTokenService.resetPassword(request.getNewPassword() , request.getResetToken());
            return ResponseEntity.ok("Reset password completed");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
