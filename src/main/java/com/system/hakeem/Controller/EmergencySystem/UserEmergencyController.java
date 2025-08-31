package com.system.hakeem.Controller.EmergencySystem;


import com.system.hakeem.Dto.EmergencySystem.LocationDto.UserLocationDto;
import com.system.hakeem.Service.UserManagement.UserService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Builder
@RestController
@RequestMapping("/user")
public class UserEmergencyController {

    private final UserService userService;

    @PostMapping("/location")
    public ResponseEntity<UserLocationDto> updateUserLocation(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(userService.updateUserLocation(lat , lng));
    }

    @GetMapping("/location")
    public ResponseEntity<UserLocationDto> getLocation() {
        return ResponseEntity.ok(userService.getUserLocation());
    }
}
