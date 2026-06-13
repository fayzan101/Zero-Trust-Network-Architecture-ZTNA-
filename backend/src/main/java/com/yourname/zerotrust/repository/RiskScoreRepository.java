package com.yourname.zerotrust.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.RiskScore;

public interface RiskScoreRepository extends JpaRepository<RiskScore, Long> {
    List<RiskScore> findByUserIdOrderByCalculatedAtDesc(Long userId);
    Optional<RiskScore> findFirstBySessionIdOrderByCalculatedAtDesc(String sessionId);
}
