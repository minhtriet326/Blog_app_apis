package com.blog.auth.repositories;

import com.blog.auth.entities.ForgotPassword;
import com.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;
import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    @Query("SELECT f FROM ForgotPassword f WHERE f.otp = ?1 AND f.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

    Optional<ForgotPassword> findByUser(User user);
}
