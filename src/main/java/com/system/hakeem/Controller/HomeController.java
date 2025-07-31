package com.system.hakeem.Controller;

import com.system.hakeem.Model.User;
import com.system.hakeem.Repositories.UserRepository;
import com.system.hakeem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Home")
public class HomeController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello World!");
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> authenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('DOCTOR','PARAMEDIC')")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

}
