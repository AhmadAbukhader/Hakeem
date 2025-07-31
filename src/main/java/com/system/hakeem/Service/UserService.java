package com.system.hakeem.Service;

import com.system.hakeem.Model.User;
import com.system.hakeem.Repositories.RoleRepository;
import com.system.hakeem.Repositories.UserRepository;
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
