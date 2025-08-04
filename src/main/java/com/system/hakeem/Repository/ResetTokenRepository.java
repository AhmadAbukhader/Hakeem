package com.system.hakeem.Repository;

import com.system.hakeem.Model.ResetToken;
import com.system.hakeem.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken , Integer> {
    Optional<ResetToken> findByToken(String token);
    Optional<ResetToken> findByUser(User user);

    void deleteByUser(User user);
}
