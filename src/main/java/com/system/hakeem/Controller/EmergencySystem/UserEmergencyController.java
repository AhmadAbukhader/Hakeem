package com.system.hakeem.Controller.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.LocationDto.UserLocationDto;
import com.system.hakeem.Service.UserManagement.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User Location Management", description = "APIs for managing user GPS locations for emergency services")
public class UserEmergencyController {

    private final UserService userService;

    @PostMapping("/location")
    @Operation(summary = "Update user location", description = "Updates the GPS location of the currently authenticated user. Used for emergency services to locate users in need of assistance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User location updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLocationDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid coordinates"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserLocationDto> updateUserLocation(
            @Parameter(description = "Latitude coordinate", required = true, example = "31.9522") @RequestParam double lat,
            @Parameter(description = "Longitude coordinate", required = true, example = "35.2332") @RequestParam double lng) {
        try {
            UserLocationDto location = userService.updateUserLocation(lat, lng);
            return ResponseEntity.ok(location);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/location")
    @Operation(summary = "Get user location", description = "Retrieves the current GPS location of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User location retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLocationDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "User location not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserLocationDto> getLocation() {
        try {
            UserLocationDto location = userService.getUserLocation();
            if (location == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
