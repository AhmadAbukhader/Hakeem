package com.system.hakeem.Controller.EmergencySystem;


import com.system.hakeem.Dto.EmergencySystem.LocationDto.UserLocationDto;
import com.system.hakeem.Service.UserManagement.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserEmergencyController {

    private final UserService userService;

    @PostMapping("/location")
    public ResponseEntity<UserLocationDto> updateUserLocation(
            @RequestParam double lat,
            @RequestParam double lng) {
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
