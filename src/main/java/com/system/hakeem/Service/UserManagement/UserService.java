package com.system.hakeem.Service.UserManagement;

import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.UserManagement.RoleRepository;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public List<User> allUsers(){
        return userRepository.findAll();
    }


}
