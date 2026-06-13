package com.yourname.zerotrust.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.repository.AuditLogRepository;
import com.yourname.zerotrust.service.AuditLogService;

@SpringBootTest
@ActiveProfiles("test")
class AuditLogServiceTest {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    void getLogsBySeverity_filtersCriticalEvents() {
        auditLogService.logInfo("LOGIN_SUCCESS", 1L, "user1", "127.0.0.1", "ok");
        auditLogService.logCritical("POLICY_DENIED", 1L, "user1", "127.0.0.1", "denied");
        auditLogService.logCritical("ACCESS_DENIED", 2L, "user2", "10.0.0.1", "blocked");

        var critical = auditLogService.getLogsBySeverity("CRITICAL");

        assertEquals(2, critical.size());
        assertTrue(critical.stream().allMatch(l -> "CRITICAL".equals(l.getSeverity())));
    }
}
