package com.yourname.zerotrust.incident;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.repository.AuditLogRepository;
import com.yourname.zerotrust.service.AuditLogService;
import com.yourname.zerotrust.service.IncidentService;

@SpringBootTest
@ActiveProfiles("test")
class IncidentServiceTest {

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    void getTimeline_buildsCorrelatedEvents() {
        String correlationId = "corr-test-123";
        auditLogService.log("LOGIN_FAILED", 1L, "demo", "10.0.0.1", "bad password", "WARN", correlationId);
        auditLogService.logCritical("ACCESS_DENIED", 1L, "demo", "10.0.0.1",
                "Login denied: risk too high", correlationId);

        var incidents = incidentService.listIncidents();
        assertFalse(incidents.isEmpty());

        var timeline = incidentService.getTimeline(incidents.get(0).getId());
        assertEquals(2, timeline.getTimeline().size());
        assertEquals(correlationId, timeline.getIncident().getCorrelationId());
    }
}
