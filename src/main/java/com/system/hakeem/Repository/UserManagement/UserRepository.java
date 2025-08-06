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


    Page<User> findAllByRole(Role role , Pageable pageable);
    Page<User> findAllByRoleAndLocation(Role role , String location , Pageable pageable);
    Page<User> findAllByRoleAndSpecialization(Role role , String Specialization , Pageable pageable);

    Page<User> findAllByRoleAndSpecializationAndLocation(Role role , String Specialization , String location , Pageable pageable );


}
