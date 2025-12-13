package com.system.hakeem.Repository.UserManagement;

import com.system.hakeem.Model.UserManagement.Role;
import com.system.hakeem.Model.UserManagement.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    Page<User> findAllByRole(Role role, Pageable pageable);

    Page<User> findAllByRoleAndSpecialization(Role role, String Specialization, Pageable pageable);

    Page<User> findAllByRoleAndNameContainingIgnoreCase(Role role, String name, Pageable pageable);

    Page<User> findAllByRoleAndLocationNameContainingIgnoreCase(Role role, String locationName, Pageable pageable);

    Page<User> findAllByRoleAndSpecializationAndLocationNameContainingIgnoreCase(Role role, String specialization,
            String locationName, Pageable pageable);
}

// @Query(value = """
// SELECT
// u.name,
// u.role_id,
// u.dob,
// u.gender,
// u.ph_num,
// u.specialization,
// u.location,
// u.id AS doctor_id,
// COALESCE(AVG(dr.rating), 0) AS avg_rating,
// MAX(dr.rated_at) AS last_rating_date,
// GROUP_CONCAT(dr.description ORDER BY dr.rated_at DESC SEPARATOR '; ') AS
// all_descriptions
// FROM user u
// LEFT JOIN doctor_rating dr
// ON u.id = dr.doctor_id
// WHERE u.role_id = 'DOCTOR'
// AND (:location IS NULL OR u.location = :location)
// AND (:specialization IS NULL OR u.specialization = :specialization)
// GROUP BY u.id
// ORDER BY avg_rating DESC
// """, nativeQuery = true)
// Page<DoctorDto> findDoctorsFilteredAndSorted(
// @Param("location") String location,
// @Param("specialization") String specialization ,
// Pageable pageable
