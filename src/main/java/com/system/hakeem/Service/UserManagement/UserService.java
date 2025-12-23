package com.system.hakeem.Service.UserManagement;

import com.system.hakeem.Dto.AppointmentSystem.Doctor.DoctorDto;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.UserLocationDto;
import com.system.hakeem.Dto.UserManagement.UserDto.MeDto;
import com.system.hakeem.Model.UserManagement.Role;
import com.system.hakeem.Model.UserManagement.Type;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.AppointmentSystem.DoctorRatingRepository;
import com.system.hakeem.Repository.UserManagement.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

        private final DoctorRatingRepository doctorRatingRepository;
        private final UserRepository userRepository;
        private final GeometryFactory geometryFactory;

        // Distance in meters for "same city" filtering (50km radius)
        private static final double SAME_CITY_DISTANCE_METERS = 50000;

        public List<DoctorDto> getDoctors(String specialization, Boolean rated, Boolean location,
                        Pageable pageable) {
                Role role = Role.builder().id(2).role(Type.DOCTOR).build();
                int roleId = role.getId();
                List<DoctorDto> doctors;
                Page<User> users;

                // Get current patient's location if location filter is requested
                Double patientLatitude = null;
                Double patientLongitude = null;
                if (location != null && location) {
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        User patient = (User) auth.getPrincipal();
                        // #region agent log
                        try {
                                java.io.FileWriter fw = new java.io.FileWriter(
                                                "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log",
                                                true);
                                fw.write(String.format(
                                                "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:46\",\"message\":\"Patient location check\",\"data\":{\"patientId\":%d,\"locationIsNull\":%s,\"locationX\":%s,\"locationY\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A,D\"}\n",
                                                System.currentTimeMillis(),
                                                java.util.UUID.randomUUID().toString().substring(0, 8),
                                                System.currentTimeMillis(), patient.getId(),
                                                patient.getLocation() == null,
                                                patient.getLocation() != null ? patient.getLocation().getX() : "null",
                                                patient.getLocation() != null ? patient.getLocation().getY() : "null"));
                                fw.close();
                        } catch (Exception e) {
                        }
                        // #endregion
                        if (patient.getLocation() != null) {
                                patientLatitude = patient.getLocation().getX();
                                patientLongitude = patient.getLocation().getY();
                                // #region agent log
                                try {
                                        java.io.FileWriter fw = new java.io.FileWriter(
                                                        "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log",
                                                        true);
                                        fw.write(String.format(
                                                        "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:52\",\"message\":\"Patient coordinates extracted\",\"data\":{\"patientLat\":%s,\"patientLon\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"B\"}\n",
                                                        System.currentTimeMillis(),
                                                        java.util.UUID.randomUUID().toString().substring(0, 8),
                                                        System.currentTimeMillis(), patientLatitude, patientLongitude));
                                        fw.close();
                                } catch (Exception e) {
                                }
                                // #endregion
                        }
                }

                boolean useLocationFilter = location != null && location && patientLatitude != null
                                && patientLongitude != null;
                // #region agent log
                try {
                        java.io.FileWriter fw = new java.io.FileWriter(
                                        "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log", true);
                        fw.write(String.format(
                                        "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:55\",\"message\":\"Query selection\",\"data\":{\"useLocationFilter\":%s,\"specialization\":%s,\"patientLat\":%s,\"patientLon\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\"}\n",
                                        System.currentTimeMillis(),
                                        java.util.UUID.randomUUID().toString().substring(0, 8),
                                        System.currentTimeMillis(), useLocationFilter,
                                        specialization != null ? "\"" + specialization + "\"" : "null", patientLatitude,
                                        patientLongitude));
                        fw.close();
                } catch (Exception e) {
                }
                // #endregion

                if (specialization == null && !useLocationFilter) {
                        users = userRepository.findAllByRole(role, pageable);
                } else if (specialization != null && !useLocationFilter) {
                        users = userRepository.findAllByRoleAndSpecialization(role, specialization, pageable);
                } else if (specialization == null && useLocationFilter) {
                        users = userRepository.findAllByRoleAndLocationWithinDistance(roleId,
                                        patientLatitude, patientLongitude, SAME_CITY_DISTANCE_METERS, pageable);
                } else {
                        users = userRepository.findAllByRoleAndSpecializationAndLocationWithinDistance(roleId,
                                        specialization, patientLatitude, patientLongitude, SAME_CITY_DISTANCE_METERS,
                                        pageable);
                }
                // #region agent log
                try {
                        java.io.FileWriter fw = new java.io.FileWriter(
                                        "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log", true);
                        fw.write(String.format(
                                        "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:69\",\"message\":\"Query executed\",\"data\":{\"resultCount\":%d,\"useLocationFilter\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C,E\"}\n",
                                        System.currentTimeMillis(),
                                        java.util.UUID.randomUUID().toString().substring(0, 8),
                                        System.currentTimeMillis(), users.getContent().size(), useLocationFilter));
                        fw.close();
                } catch (Exception e) {
                }
                // #endregion
                doctors = users.stream().map(
                                user -> {
                                        // #region agent log
                                        try {
                                                java.io.FileWriter fw = new java.io.FileWriter(
                                                                "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log",
                                                                true);
                                                fw.write(String.format(
                                                                "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:70\",\"message\":\"Mapping doctor to DTO\",\"data\":{\"userId\":%d,\"username\":\"%s\",\"locationIsNull\":%s,\"locationX\":%s,\"locationY\":%s,\"useLocationFilter\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A,C,E\"}\n",
                                                                System.currentTimeMillis(),
                                                                java.util.UUID.randomUUID().toString().substring(0, 8),
                                                                System.currentTimeMillis(), user.getId(),
                                                                user.getUsername(), user.getLocation() == null,
                                                                user.getLocation() != null ? user.getLocation().getX()
                                                                                : "null",
                                                                user.getLocation() != null ? user.getLocation().getY()
                                                                                : "null",
                                                                useLocationFilter));
                                                fw.close();
                                        } catch (Exception e) {
                                        }
                                        // #endregion
                                        double lat = user.getLocation() != null ? user.getLocation().getX() : 0;
                                        double lon = user.getLocation() != null ? user.getLocation().getY() : 0;
                                        // #region agent log
                                        try {
                                                java.io.FileWriter fw = new java.io.FileWriter(
                                                                "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log",
                                                                true);
                                                fw.write(String.format(
                                                                "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:75\",\"message\":\"Extracted coordinates\",\"data\":{\"userId\":%d,\"extractedLat\":%s,\"extractedLon\":%s,\"pointX\":%s,\"pointY\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"B\"}\n",
                                                                System.currentTimeMillis(),
                                                                java.util.UUID.randomUUID().toString().substring(0, 8),
                                                                System.currentTimeMillis(), user.getId(), lat, lon,
                                                                user.getLocation() != null ? user.getLocation().getX()
                                                                                : "null",
                                                                user.getLocation() != null ? user.getLocation().getY()
                                                                                : "null"));
                                                fw.close();
                                        } catch (Exception e) {
                                        }
                                        // #endregion
                                        DoctorDto dto = DoctorDto.builder()
                                                        .rating(doctorRatingRepository
                                                                        .findAverageRatingByDoctorId(
                                                                                        user.getId()) != null
                                                                                                        ? doctorRatingRepository
                                                                                                                        .findAverageRatingByDoctorId(
                                                                                                                                        user.getId())
                                                                                                        : 0)
                                                        .doctorId(user.getId())
                                                        .doctorName(user.getName())
                                                        .username(user.getUsername())
                                                        .dob(user.getDob())
                                                        .age(user.getAge())
                                                        .phoneNumber(user.getPhoneNumber() != null
                                                                        ? user.getPhoneNumber()
                                                                        : 0L)
                                                        .specialization(user.getSpecialization())
                                                        // .location(user.getLocation())
                                                        .latitude(lat)
                                                        .longitude(lon)
                                                        .gender(user.getGender())
                                                        .description(user.getDescription())
                                                        .build();
                                        // #region agent log
                                        try {
                                                java.io.FileWriter fw = new java.io.FileWriter(
                                                                "c:\\Users\\HP\\Desktop\\Hakeem Project\\hakeem\\.cursor\\debug.log",
                                                                true);
                                                fw.write(String.format(
                                                                "{\"id\":\"log_%d_%s\",\"timestamp\":%d,\"location\":\"UserService.java:90\",\"message\":\"DTO built\",\"data\":{\"doctorId\":%d,\"dtoLatitude\":%s,\"dtoLongitude\":%s},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"B,D\"}\n",
                                                                System.currentTimeMillis(),
                                                                java.util.UUID.randomUUID().toString().substring(0, 8),
                                                                System.currentTimeMillis(), dto.getDoctorId(),
                                                                dto.getLatitude(), dto.getLongitude()));
                                                fw.close();
                                        } catch (Exception e) {
                                        }
                                        // #endregion
                                        return dto;
                                })
                                .collect(Collectors.toList());

                if (rated != null && rated)
                        doctors.sort(Comparator.comparingDouble(DoctorDto::getRating).reversed());

                return doctors;
        }

        public UserLocationDto getUserLocation() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();

                return UserLocationDto.builder()
                                .userId(user.getId())
                                .latitude(user.getLocation() != null ? user.getLocation().getX() : 0)
                                .longitude(user.getLocation() != null ? user.getLocation().getY() : 0)
                                .build();
        }

        public UserLocationDto updateUserLocation(double latitude, double longitude) {
                // Validate coordinates
                validateCoordinates(latitude, longitude);

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();

                Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                point.setSRID(4326);
                user.setLocation(point);
                userRepository.save(user);

                return UserLocationDto.builder()
                                .userId(user.getId())
                                .latitude(user.getLocation() != null ? user.getLocation().getX() : 0)
                                .longitude(user.getLocation() != null ? user.getLocation().getY() : 0)
                                .build();
        }

        /**
         * Validates GPS coordinates are within valid ranges
         * 
         * @param latitude  Latitude coordinate (-90 to 90)
         * @param longitude Longitude coordinate (-180 to 180)
         * @throws IllegalArgumentException if coordinates are invalid
         */
        private void validateCoordinates(double latitude, double longitude) {
                if (latitude < -90 || latitude > 90) {
                        throw new IllegalArgumentException(
                                        String.format("Latitude must be between -90 and 90. Received: %f", latitude));
                }
                if (longitude < -180 || longitude > 180) {
                        throw new IllegalArgumentException(
                                        String.format("Longitude must be between -180 and 180. Received: %f",
                                                        longitude));
                }
        }

        public List<DoctorDto> getAllDoctors() {
                Role role = Role.builder().id(2).role(Type.DOCTOR).build();
                List<User> users = userRepository.findByRole(role);
                return users.stream().map(
                                user -> DoctorDto.builder()
                                                .rating(doctorRatingRepository
                                                                .findAverageRatingByDoctorId(user.getId()) != null
                                                                                ? doctorRatingRepository
                                                                                                .findAverageRatingByDoctorId(
                                                                                                                user.getId())
                                                                                : 0)
                                                .doctorId(user.getId())
                                                .doctorName(user.getName())
                                                .username(user.getUsername())
                                                .dob(user.getDob())
                                                .age(user.getAge())
                                                .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : 0L)
                                                .specialization(user.getSpecialization())
                                                // .location(user.getLocation())
                                                .latitude(user.getLocation() != null ? user.getLocation().getX() : 0)
                                                .longitude(user.getLocation() != null ? user.getLocation().getY() : 0)
                                                .gender(user.getGender())
                                                .description(user.getDescription())
                                                .build())
                                .toList();

        }

        public User getUserInfo() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                return (User) auth.getPrincipal();

        }

        public MeDto getUserInfoDto() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();

                return MeDto.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .name(user.getName())
                                .dob(user.getDob())
                                .gender(user.getGender())
                                .bloodType(user.getBloodType())
                                .age(user.getAge())
                                .weight(user.getWeight())
                                .phoneNumber(user.getPhoneNumber())
                                .specialization(user.getSpecialization())
                                .license(user.getLicense())
                                .description(user.getDescription())
                                .latitude(user.getLocation() != null ? user.getLocation().getX() : null)
                                .longitude(user.getLocation() != null ? user.getLocation().getY() : null)
                                .role(user.getRole() != null ? user.getRole().getRole() : null)
                                .build();
        }

        public List<DoctorDto> searchDoctorsByName(String name, Pageable pageable) {
                Role role = Role.builder().id(2).role(Type.DOCTOR).build();
                Page<User> users = userRepository.findAllByRoleAndNameContainingIgnoreCase(role, name, pageable);

                return users.stream().map(
                                user -> DoctorDto.builder()
                                                .rating(doctorRatingRepository
                                                                .findAverageRatingByDoctorId(user.getId()) != null
                                                                                ? doctorRatingRepository
                                                                                                .findAverageRatingByDoctorId(
                                                                                                                user.getId())
                                                                                : 0)
                                                .doctorId(user.getId())
                                                .doctorName(user.getName())
                                                .username(user.getUsername())
                                                .dob(user.getDob())
                                                .age(user.getAge())
                                                .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : 0L)
                                                .specialization(user.getSpecialization())
                                                .latitude(user.getLocation() != null ? user.getLocation().getX() : 0)
                                                .longitude(user.getLocation() != null ? user.getLocation().getY() : 0)
                                                .gender(user.getGender())
                                                .description(user.getDescription())
                                                .build())
                                .collect(Collectors.toList());
        }

}
