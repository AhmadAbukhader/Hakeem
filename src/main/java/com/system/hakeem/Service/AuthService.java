package com.system.hakeem.Service;

import com.system.hakeem.Dto.LoginResponse;
import com.system.hakeem.Dto.LoginUserDto;
import com.system.hakeem.Dto.SignUpUserDto;
import com.system.hakeem.Dto.SignUpResponse;
import com.system.hakeem.Model.Role;
import com.system.hakeem.Model.Type;
import com.system.hakeem.Model.User;
import com.system.hakeem.Repositories.RoleRepository;
import com.system.hakeem.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    public SignUpResponse signUp(SignUpUserDto inputUser) {

        Role role = roleRepository.findByRole(Type.valueOf(inputUser.getRole()));
        if(role == null){
            role = roleRepository.save(new Role(Type.valueOf(inputUser.getRole())));
        }

        User user = User.builder()
                .username(inputUser.getUsername())
                .password(passwordEncoder.encode(inputUser.getPassword()))
                .name(inputUser.getName())
                .role(role)
                .age(inputUser.getAge())
                .phNum(inputUser.getPhNum())
                .dob(inputUser.getDob())
                .bloodType(inputUser.getBloodType())
                .gender(inputUser.getGender())
                .license(inputUser.getLicense())
                .location(inputUser.getLocation())
                .specialization(inputUser.getSpecialization())
                .weight(inputUser.getWeight())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return SignUpResponse.builder().token(token).role(role).username(user.getUsername()).build();

    }

    public LoginResponse authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow();
        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }
}

