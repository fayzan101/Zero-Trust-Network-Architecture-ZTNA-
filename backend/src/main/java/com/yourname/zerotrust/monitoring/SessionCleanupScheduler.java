package com.yourname.zerotrust.monitoring;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.entity.Session;
import com.yourname.zerotrust.repository.SessionRepository;
import com.yourname.zerotrust.service.AuditLogService;

@Component
public class SessionCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(SessionCleanupScheduler.class);
    private static final int STALE_HOURS = 24;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Scheduled(fixedRate = 3600000)
    public void cleanupStaleSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(STALE_HOURS);
        List<Session> stale = sessionRepository.findByStatusAndLastActivityAtBefore("ACTIVE", cutoff);

        for (Session session : stale) {
            session.setStatus("TERMINATED");
            session.setTerminatedAt(LocalDateTime.now());
            session.setTerminationReason("Stale session cleanup (>24h inactivity)");
            sessionRepository.save(session);

            auditLogService.logWarn("SESSION_TERMINATED", session.getUserId(), null,
                    session.getIpAddress(),
                    "Auto-terminated stale session " + session.getSessionId());
        }

        if (!stale.isEmpty()) {
            log.info("Terminated {} stale ACTIVE sessions", stale.size());
        }
    }
}
