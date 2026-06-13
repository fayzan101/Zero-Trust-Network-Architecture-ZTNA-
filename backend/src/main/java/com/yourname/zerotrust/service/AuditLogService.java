package com.yourname.zerotrust.service;

import java.util.List;

import com.yourname.zerotrust.dto.AuditLogResponse;

public interface AuditLogService {
    void log(String eventType, Long userId, String username, String ipAddress, String details, String severity);
    void log(String eventType, Long userId, String username, String ipAddress, String details, String severity, String correlationId);
    void logInfo(String eventType, Long userId, String username, String ipAddress, String details);
    void logWarn(String eventType, Long userId, String username, String ipAddress, String details);
    void logCritical(String eventType, Long userId, String username, String ipAddress, String details);
    void logInfo(String eventType, Long userId, String username, String ipAddress, String details, String correlationId);
    void logWarn(String eventType, Long userId, String username, String ipAddress, String details, String correlationId);
    void logCritical(String eventType, Long userId, String username, String ipAddress, String details, String correlationId);
    List<AuditLogResponse> getAllLogs();
    List<AuditLogResponse> getLogsByEventType(String eventType);
    List<AuditLogResponse> getLogsByUsername(String username);
    List<AuditLogResponse> getLogsBySeverity(String severity);
}
