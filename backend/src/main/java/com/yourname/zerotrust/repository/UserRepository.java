package com.yourname.zerotrust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Additional query methods if needed
}
