package com.system.hakeem.Controller.UserManagement;


import com.system.hakeem.Dto.UserManagement.LoginResponse;
import com.system.hakeem.Dto.UserManagement.LoginUserDto;
import com.system.hakeem.Dto.UserManagement.SignUpResponse;
import com.system.hakeem.Dto.UserManagement.SignUpUserDto;
import com.system.hakeem.Dto.UserManagement.password.ForgotPasswordCodeRequest;
import com.system.hakeem.Dto.UserManagement.password.ForgotPasswordRequest;
import com.system.hakeem.Dto.UserManagement.password.ResetPasswordRequest;
import com.system.hakeem.Service.UserManagement.AuthService;
import com.system.hakeem.Service.UserManagement.JwtService;
import com.system.hakeem.Service.UserManagement.ResetPasswordService;
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
    private ResetPasswordService resetPasswordService;

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

    @PostMapping("/forgot-password/code")
    public ResponseEntity<String> forgotPasswordCode(@RequestBody ForgotPasswordRequest request){
        resetPasswordService.sendCode(request.getUsername());
        return ResponseEntity.ok("the verification code has been sent");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPasswordToken(@RequestBody ForgotPasswordCodeRequest request){
        resetPasswordService.initiateResetPassword(request.getUsername() , request.getCode());
        return ResponseEntity.ok("the reset token has been initiated");
    }


    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword (@RequestBody ResetPasswordRequest request){
        try{
            resetPasswordService.resetPassword(request.getNewPassword() , request.getResetToken());
            return ResponseEntity.ok("Reset password completed");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
