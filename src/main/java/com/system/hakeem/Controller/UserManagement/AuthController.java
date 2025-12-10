package com.system.hakeem.Controller.UserManagement;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorDto;
import com.system.hakeem.Dto.UserManagement.AuthDto.LoginResponse;
import com.system.hakeem.Dto.UserManagement.AuthDto.LoginUserDto;
import com.system.hakeem.Dto.UserManagement.AuthDto.SignUpResponse;
import com.system.hakeem.Dto.UserManagement.AuthDto.SignUpUserDto;
import com.system.hakeem.Dto.UserManagement.password.ForgotPasswordRequest;
import com.system.hakeem.Dto.UserManagement.password.ResetPasswordRequest;
import com.system.hakeem.Service.UserManagement.AuthService;
import com.system.hakeem.Service.UserManagement.JwtService;
import com.system.hakeem.Service.UserManagement.ResetPasswordService;
import com.system.hakeem.Service.UserManagement.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication, registration, and password management")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final ResetPasswordService resetPasswordService;
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account in the system. Supports registration for patients, doctors, and paramedics with role-specific information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignUpResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Username already exists or invalid data")
    })
    public ResponseEntity<SignUpResponse> signUp(
            @Parameter(description = "User registration information including username, password, role, and role-specific details", required = true) @RequestBody SignUpUserDto request) {
        try {
            SignUpResponse newUser = authService.signUp(request);
            return ResponseEntity.ok(newUser);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user with username and password, returns a JWT token for subsequent API calls")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful - Returns JWT token and user information", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "Login credentials (username and password)", required = true) @RequestBody LoginUserDto request) {
        LoginResponse authenticatedUser = authService.authenticate(request);
        authenticatedUser.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(authenticatedUser);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset code", description = "Generates and sends a verification code to the user's email for password reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully to user's email"),
            @ApiResponse(responseCode = "400", description = "Invalid username or user not found")
    })
    public ResponseEntity<String> forgotPassword(
            @Parameter(description = "Username for password reset", required = true) @RequestBody ForgotPasswordRequest request) {
        String code = resetPasswordService.generateCode(request.getUsername());
        return ResponseEntity.ok("the verification code has been sent ");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with verification code", description = "Resets the user's password using the verification code sent to their email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid verification code or expired code")
    })
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "Password reset request with username, verification code, and new password", required = true) @RequestBody ResetPasswordRequest request) {
        try {
            resetPasswordService.resetPassword(request);
            return ResponseEntity.ok("the password has been changed");
        } catch (Exception e) {
            return ResponseEntity.ok("the password has not been changed , " + e.getMessage());
        }
    }

    @GetMapping("/doctors")
    @Operation(summary = "Get all doctors (public)", description = "Retrieves a list of all doctors in the system. This endpoint is publicly accessible without authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of doctors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorDto.class)))
    })
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = userService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

}
