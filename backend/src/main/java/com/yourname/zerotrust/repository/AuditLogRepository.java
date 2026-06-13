package com.yourname.zerotrust.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourname.zerotrust.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
    List<AuditLog> findByEventTypeOrderByCreatedAtDesc(String eventType);
    List<AuditLog> findByUsernameOrderByCreatedAtDesc(String username);
    List<AuditLog> findBySeverityOrderByCreatedAtDesc(String severity);
    List<AuditLog> findBySeverityInOrderByCreatedAtDesc(List<String> severities);
    List<AuditLog> findByCorrelationIdOrderByCreatedAtAsc(String correlationId);
    List<AuditLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long userId, LocalDateTime from, LocalDateTime to);
}
