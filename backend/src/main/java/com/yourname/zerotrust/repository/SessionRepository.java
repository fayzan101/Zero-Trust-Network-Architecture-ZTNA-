package com.yourname.zerotrust.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);
    List<Session> findByStatus(String status);
    List<Session> findByUserIdAndStatus(Long userId, String status);
    List<Session> findByStatusAndLastActivityAtBefore(String status, LocalDateTime cutoff);
    long countByUserIdAndStatus(Long userId, String status);
    long countByStatus(String status);
}
