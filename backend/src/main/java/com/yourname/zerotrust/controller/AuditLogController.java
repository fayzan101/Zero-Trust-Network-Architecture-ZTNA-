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

@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getLogs(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String username) {
        if (eventType != null && !eventType.isBlank()) {
            return ResponseEntity.ok(auditLogService.getLogsByEventType(eventType));
        }
        if (username != null && !username.isBlank()) {
            return ResponseEntity.ok(auditLogService.getLogsByUsername(username));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }
}
