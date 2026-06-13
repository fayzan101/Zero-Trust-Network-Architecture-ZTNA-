package com.yourname.zerotrust.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.AuditLogResponse;
import com.yourname.zerotrust.entity.AuditLog;
import com.yourname.zerotrust.repository.AuditLogRepository;
import com.yourname.zerotrust.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void log(String eventType, Long userId, String username, String ipAddress, String details, String severity) {
        AuditLog entry = new AuditLog();
        entry.setEventType(eventType);
        entry.setUserId(userId);
        entry.setUsername(username);
        entry.setIpAddress(ipAddress);
        entry.setDetails(details);
        entry.setSeverity(severity);
        auditLogRepository.save(entry);
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

    private AuditLogResponse toResponse(AuditLog log) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(log.getId());
        response.setEventType(log.getEventType());
        response.setUserId(log.getUserId());
        response.setUsername(log.getUsername());
        response.setIpAddress(log.getIpAddress());
        response.setDetails(log.getDetails());
        response.setSeverity(log.getSeverity());
        if (log.getCreatedAt() != null) {
            response.setCreatedAt(log.getCreatedAt().toString());
        }
        return response;
    }
}
