package com.system.hakeem.Repository.UserManagement;

import com.system.hakeem.Model.UserManagement.ResetCode;
import com.system.hakeem.Model.UserManagement.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetCodeRepository extends JpaRepository<ResetCode, Integer> {
    void deleteByUser(User user);
    Optional<ResetCode> findByCode (int resetCode);
}
