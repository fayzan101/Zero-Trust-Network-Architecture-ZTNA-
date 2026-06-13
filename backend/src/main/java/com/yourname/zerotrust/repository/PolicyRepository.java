package com.yourname.zerotrust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByEnabledTrue();
    List<Policy> findByResourceAndActionAndEnabledTrue(String resource, String action);
}
