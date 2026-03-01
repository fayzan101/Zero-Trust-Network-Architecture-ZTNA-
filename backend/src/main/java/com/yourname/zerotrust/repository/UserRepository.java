package com.yourname.zerotrust.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
