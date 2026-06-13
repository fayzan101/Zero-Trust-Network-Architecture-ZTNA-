package com.yourname.zerotrust.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.Attack;

public interface AttackRepository extends JpaRepository<Attack, Long> {
    List<Attack> findAllByOrderBySimulatedAtDesc();
    List<Attack> findByAttackTypeOrderBySimulatedAtDesc(String attackType);
    long countByDetectedTrue();
    long countByDetectedFalse();
    List<Attack> findByTargetUserIdAndSimulatedAtBetweenOrderBySimulatedAtDesc(
            Long targetUserId, LocalDateTime from, LocalDateTime to);
}
