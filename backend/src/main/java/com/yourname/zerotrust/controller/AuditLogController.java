package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.AuditLogResponse;
import com.yourname.zerotrust.service.AuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Logs", description = "Security audit trail for policy denials, attacks, and session events (admin only)")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "List audit logs",
            description = "Returns audit logs. Filters are mutually exclusive — severity takes precedence, then eventType, then username.")
    public ResponseEntity<List<AuditLogResponse>> getLogs(
            @Parameter(description = "Filter by severity: INFO, WARN, or CRITICAL")
            @RequestParam(required = false) String severity,
            @Parameter(description = "Filter by event type, e.g. POLICY_DENIED, ACCESS_DENIED, ATTACK_SIMULATED")
            @RequestParam(required = false) String eventType,
            @Parameter(description = "Filter by username")
            @RequestParam(required = false) String username) {
        if (severity != null && !severity.isBlank()) {
            return ResponseEntity.ok(auditLogService.getLogsBySeverity(severity));
        }
        if (eventType != null && !eventType.isBlank()) {
            return ResponseEntity.ok(auditLogService.getLogsByEventType(eventType));
        }
        if (username != null && !username.isBlank()) {
            return ResponseEntity.ok(auditLogService.getLogsByUsername(username));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }
}
