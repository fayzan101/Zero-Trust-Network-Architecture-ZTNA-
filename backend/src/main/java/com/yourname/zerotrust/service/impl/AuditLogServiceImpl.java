package com.yourname.zerotrust.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.AuditLogResponse;
import com.yourname.zerotrust.dto.SecurityEventMessage;
import com.yourname.zerotrust.entity.AuditLog;
import com.yourname.zerotrust.repository.AuditLogRepository;
import com.yourname.zerotrust.service.AuditLogService;
import com.yourname.zerotrust.websocket.SecurityEventBroadcaster;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SecurityEventBroadcaster securityEventBroadcaster;

    @Override
    public void log(String eventType, Long userId, String username, String ipAddress, String details, String severity) {
        log(eventType, userId, username, ipAddress, details, severity, null);
    }

    @Override
    public void log(String eventType, Long userId, String username, String ipAddress,
            String details, String severity, String correlationId) {
        AuditLog entry = new AuditLog();
        entry.setEventType(eventType);
        entry.setUserId(userId);
        entry.setUsername(username);
        entry.setIpAddress(ipAddress);
        entry.setDetails(details);
        entry.setSeverity(severity);
        entry.setCorrelationId(correlationId);
        auditLogRepository.save(entry);
        broadcast(entry);
    }

    @Override
    public void logInfo(String eventType, Long userId, String username, String ipAddress, String details) {
        log(eventType, userId, username, ipAddress, details, "INFO");
    }

    @Override
    public void logWarn(String eventType, Long userId, String username, String ipAddress, String details) {
        log(eventType, userId, username, ipAddress, details, "WARN");
    }

    @Override
    public void logCritical(String eventType, Long userId, String username, String ipAddress, String details) {
        log(eventType, userId, username, ipAddress, details, "CRITICAL");
    }

    @Override
    public void logInfo(String eventType, Long userId, String username, String ipAddress,
            String details, String correlationId) {
        log(eventType, userId, username, ipAddress, details, "INFO", correlationId);
    }

    @Override
    public void logWarn(String eventType, Long userId, String username, String ipAddress,
            String details, String correlationId) {
        log(eventType, userId, username, ipAddress, details, "WARN", correlationId);
    }

    @Override
    public void logCritical(String eventType, Long userId, String username, String ipAddress,
            String details, String correlationId) {
        log(eventType, userId, username, ipAddress, details, "CRITICAL", correlationId);
    }

    @Override
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponse> getLogsByEventType(String eventType) {
        return auditLogRepository.findByEventTypeOrderByCreatedAtDesc(eventType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponse> getLogsByUsername(String username) {
        return auditLogRepository.findByUsernameOrderByCreatedAtDesc(username).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponse> getLogsBySeverity(String severity) {
        return auditLogRepository.findBySeverityOrderByCreatedAtDesc(severity.toUpperCase()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void broadcast(AuditLog entry) {
        SecurityEventMessage message = new SecurityEventMessage();
        message.setType("SECURITY_EVENT");
        message.setEventType(entry.getEventType());
        message.setSeverity(entry.getSeverity());
        message.setUsername(entry.getUsername());
        message.setUserId(entry.getUserId());
        message.setIpAddress(entry.getIpAddress());
        message.setDetails(entry.getDetails());
        if (entry.getCreatedAt() != null) {
            message.setTimestamp(entry.getCreatedAt().toString());
        } else {
            message.setTimestamp(LocalDateTime.now().toString());
        }
        securityEventBroadcaster.broadcast(message);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(log.getId());
        response.setEventType(log.getEventType());
        response.setUserId(log.getUserId());
        response.setUsername(log.getUsername());
        response.setIpAddress(log.getIpAddress());
        response.setDetails(log.getDetails());
        response.setSeverity(log.getSeverity());
        response.setCorrelationId(log.getCorrelationId());
        if (log.getCreatedAt() != null) {
            response.setCreatedAt(log.getCreatedAt().toString());
        }
        return response;
    }
}
