package com.yourname.zerotrust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Additional query methods if needed
}
