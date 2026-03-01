package com.yourname.zerotrust.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
