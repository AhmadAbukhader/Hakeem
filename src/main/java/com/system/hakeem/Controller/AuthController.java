package com.system.hakeem.Controller;


import com.system.hakeem.Dto.LoginResponse;
import com.system.hakeem.Dto.LoginUserDto;
import com.system.hakeem.Dto.SignUpResponse;
import com.system.hakeem.Dto.SignUpUserDto;
import com.system.hakeem.Model.User;
import com.system.hakeem.Service.AuthService;
import com.system.hakeem.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthService authService;

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

}
