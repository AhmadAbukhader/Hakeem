package com.system.hakeem.Service.UserManagement;

import com.system.hakeem.Dto.UserManagement.AuthDto.LoginResponse;
import com.system.hakeem.Dto.UserManagement.AuthDto.LoginUserDto;
import com.system.hakeem.Dto.UserManagement.AuthDto.SignUpUserDto;
import com.system.hakeem.Dto.UserManagement.AuthDto.SignUpResponse;
import com.system.hakeem.Model.UserManagement.Role;
import com.system.hakeem.Model.UserManagement.Type;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.EmergencySystem.AmbulanceUnitRepository;
import com.system.hakeem.Repository.UserManagement.RoleRepository;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GeometryFactory geometryFactory;
    private final AmbulanceUnitRepository ambulanceUnitRepository;


    public SignUpResponse signUp(SignUpUserDto inputUser) {

        Role role = roleRepository.findByRole(Type.valueOf(inputUser.getRole()));
        if(role == null){
            role = roleRepository.save(new Role(Type.valueOf(inputUser.getRole())));
        }

        Point point = geometryFactory.createPoint(
                new Coordinate(inputUser.getLongitude(), inputUser.getLatitude())
        );
        point.setSRID(4326);

        User user = User.builder()
                .username(inputUser.getUsername())
                .password(passwordEncoder.encode(inputUser.getPassword()))
                .name(inputUser.getName())
                .role(role)
                .age(inputUser.getAge())
                .phoneNumber(inputUser.getPhNum())
                .dob(inputUser.getDob())
                .bloodType(inputUser.getBloodType())
                .gender(inputUser.getGender())
                .license(inputUser.getLicense())
                .location(point)
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

