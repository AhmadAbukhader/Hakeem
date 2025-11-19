package com.system.hakeem.Controller.UserManagement;

import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Service.UserManagement.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<User> info() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

}
