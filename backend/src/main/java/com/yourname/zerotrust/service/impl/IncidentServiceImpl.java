package com.yourname.zerotrust.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.AttackSimulationResponse;
import com.yourname.zerotrust.dto.IncidentSummaryResponse;
import com.yourname.zerotrust.dto.IncidentTimelineResponse;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.dto.SessionResponse;
import com.yourname.zerotrust.dto.TimelineEventResponse;
import com.yourname.zerotrust.entity.Attack;
import com.yourname.zerotrust.entity.AuditLog;
import com.yourname.zerotrust.entity.Session;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.repository.AttackRepository;
import com.yourname.zerotrust.repository.AuditLogRepository;
import com.yourname.zerotrust.repository.SessionRepository;
import com.yourname.zerotrust.service.IncidentService;
import com.yourname.zerotrust.service.RiskService;
import com.yourname.zerotrust.service.SessionService;

@Service
public class IncidentServiceImpl implements IncidentService {

    private static final List<String> INCIDENT_SEVERITIES = List.of("WARN", "CRITICAL");
    private static final Pattern SESSION_PATTERN = Pattern.compile("session=([a-f0-9\\-]+)", Pattern.CASE_INSENSITIVE);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AttackRepository attackRepository;

    @Autowired
    private RiskService riskService;

    @Autowired
    private SessionService sessionService;

    @Override
    public List<IncidentSummaryResponse> listIncidents() {
        return auditLogRepository.findBySeverityInOrderByCreatedAtDesc(INCIDENT_SEVERITIES).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public IncidentTimelineResponse getTimeline(Long incidentId) {
        AuditLog anchor = auditLogRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));

        List<AuditLog> related = gatherRelatedLogs(anchor);
        List<TimelineEventResponse> timeline = related.stream()
                .map(this::toTimelineEvent)
                .sorted(Comparator.comparing(TimelineEventResponse::getTimestamp))
                .collect(Collectors.toList());

        IncidentTimelineResponse response = new IncidentTimelineResponse();
        response.setIncident(toSummary(anchor));
        response.setTimeline(timeline);

        String sessionId = extractSessionId(anchor.getDetails());
        if (sessionId == null) {
            for (AuditLog log : related) {
                sessionId = extractSessionId(log.getDetails());
                if (sessionId != null) break;
            }
        }

        if (sessionId != null) {
            Optional<Session> session = sessionRepository.findBySessionId(sessionId);
            session.ifPresent(s -> response.setSession(toSessionResponse(s)));
            RiskScoreResponse risk = riskService.getSessionRisk(sessionId);
            response.setRiskScore(risk);
        }

        if (anchor.getUserId() != null) {
            Attack attack = findRecentAttack(anchor);
            if (attack != null) {
                response.setAttack(toAttackResponse(attack));
            }
        }

        return response;
    }

    private List<AuditLog> gatherRelatedLogs(AuditLog anchor) {
        List<AuditLog> logs = new ArrayList<>();

        if (anchor.getCorrelationId() != null && !anchor.getCorrelationId().isBlank()) {
            logs.addAll(auditLogRepository.findByCorrelationIdOrderByCreatedAtAsc(anchor.getCorrelationId()));
        }

        if (anchor.getUserId() != null && anchor.getCreatedAt() != null) {
            LocalDateTime from = anchor.getCreatedAt().minusMinutes(10);
            LocalDateTime to = anchor.getCreatedAt().plusMinutes(10);
            for (AuditLog log : auditLogRepository
                    .findByUserIdAndCreatedAtBetweenOrderByCreatedAtAsc(anchor.getUserId(), from, to)) {
                if (logs.stream().noneMatch(l -> l.getId().equals(log.getId()))) {
                    logs.add(log);
                }
            }
        }

        if (logs.stream().noneMatch(l -> l.getId().equals(anchor.getId()))) {
            logs.add(anchor);
        }

        logs.sort(Comparator.comparing(AuditLog::getCreatedAt));
        return logs;
    }

    private Attack findRecentAttack(AuditLog anchor) {
        if (anchor.getCreatedAt() == null) return null;
        LocalDateTime from = anchor.getCreatedAt().minusMinutes(5);
        LocalDateTime to = anchor.getCreatedAt().plusMinutes(5);
        return attackRepository.findByTargetUserIdAndSimulatedAtBetweenOrderBySimulatedAtDesc(
                anchor.getUserId(), from, to).stream().findFirst().orElse(null);
    }

    private String extractSessionId(String details) {
        if (details == null) return null;
        Matcher matcher = SESSION_PATTERN.matcher(details);
        return matcher.find() ? matcher.group(1) : null;
    }

    private IncidentSummaryResponse toSummary(AuditLog log) {
        IncidentSummaryResponse summary = new IncidentSummaryResponse();
        summary.setId(log.getId());
        summary.setEventType(log.getEventType());
        summary.setSeverity(log.getSeverity());
        summary.setUserId(log.getUserId());
        summary.setUsername(log.getUsername());
        summary.setIpAddress(log.getIpAddress());
        summary.setDetails(log.getDetails());
        summary.setCorrelationId(log.getCorrelationId());
        if (log.getCreatedAt() != null) {
            summary.setCreatedAt(log.getCreatedAt().toString());
        }
        return summary;
    }

    private TimelineEventResponse toTimelineEvent(AuditLog log) {
        TimelineEventResponse event = new TimelineEventResponse();
        if (log.getCreatedAt() != null) {
            event.setTimestamp(log.getCreatedAt().toString());
        }
        event.setPhase(mapPhase(log.getEventType()));
        event.setEventType(log.getEventType());
        event.setSeverity(log.getSeverity());
        event.setSummary(log.getEventType().replace('_', ' '));
        event.setDetails(log.getDetails());
        return event;
    }

    private String mapPhase(String eventType) {
        return switch (eventType) {
            case "LOGIN_FAILED", "LOGIN_SUCCESS", "MFA_REQUIRED", "STEP_UP_REQUIRED", "STEP_UP_SUCCESS" -> "AUTHENTICATION";
            case "ACCESS_DENIED", "POLICY_DENIED", "ACCESS_COMPARED" -> "POLICY";
            case "ANOMALY_DETECTED" -> "MONITORING";
            case "SESSION_TERMINATED" -> "SESSION";
            case "ATTACK_SIMULATED" -> "ATTACK";
            default -> "AUDIT";
        };
    }

    private SessionResponse toSessionResponse(Session session) {
        return sessionService.getActiveSessions().stream()
                .filter(s -> s.getSessionId().equals(session.getSessionId()))
                .findFirst()
                .orElseGet(() -> {
                    SessionResponse resp = new SessionResponse();
                    resp.setSessionId(session.getSessionId());
                    resp.setUserId(session.getUserId());
                    resp.setFinalRisk(session.getFinalRisk());
                    resp.setStatus(session.getStatus());
                    return resp;
                });
    }

    private AttackSimulationResponse toAttackResponse(Attack attack) {
        AttackSimulationResponse response = new AttackSimulationResponse();
        response.setId(attack.getId());
        response.setAttackType(attack.getAttackType());
        response.setDetected(attack.isDetected());
        response.setDetectionMethod(attack.getDetectionMethod());
        response.setDetectionDetails(attack.getDetectionDetails());
        response.setSeverity(attack.getSeverity());
        if (attack.getSimulatedAt() != null) {
            response.setSimulatedAt(attack.getSimulatedAt().toString());
        }
        return response;
    }
}
