package com.yourname.zerotrust.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.AnomalyCheckResponse;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.dto.SessionResponse;
import com.yourname.zerotrust.dto.SessionTerminateRequest;
import com.yourname.zerotrust.entity.Session;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.monitoring.AnomalyDetector;
import com.yourname.zerotrust.repository.SessionRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AuditLogService;
import com.yourname.zerotrust.service.SessionService;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AnomalyDetector anomalyDetector;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public SessionResponse createSession(User user, String deviceId, String ipAddress, RiskScoreResponse risk) {
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(user.getId());
        session.setDeviceId(deviceId);
        session.setIpAddress(ipAddress);
        session.setUserRisk(risk.getUserRisk());
        session.setDeviceRisk(risk.getDeviceRisk());
        session.setContextRisk(risk.getContextRisk());
        session.setFinalRisk(risk.getFinalRisk());
        session.setStatus("ACTIVE");

        List<String> anomalies = anomalyDetector.detect(session);
        if (!anomalies.isEmpty()) {
            session.setAnomalyDetected(true);
            session.setAnomalyReason(String.join("; ", anomalies));
            auditLogService.logWarn("ANOMALY_DETECTED", user.getId(), user.getUsername(),
                    ipAddress, String.join("; ", anomalies));
        }

        session = sessionRepository.save(session);
        return toResponse(session);
    }

    @Override
    public List<SessionResponse> getActiveSessions() {
        return sessionRepository.findByStatus("ACTIVE").stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AnomalyCheckResponse checkAnomaly(String sessionId) {
        AnomalyCheckResponse response = new AnomalyCheckResponse();
        response.setSessionId(sessionId);

        Session session = sessionRepository.findBySessionId(sessionId).orElse(null);
        if (session == null) {
            response.setMessage("Session not found");
            return response;
        }

        List<String> anomalies = anomalyDetector.detect(session);
        response.setAnomalyDetected(!anomalies.isEmpty());
        response.setAnomalies(anomalies);
        response.setFinalRisk(session.getFinalRisk());
        response.setMessage(anomalies.isEmpty() ? "No anomalies detected" : "Anomalies detected");

        if (!anomalies.isEmpty()) {
            session.setAnomalyDetected(true);
            session.setAnomalyReason(String.join("; ", anomalies));
            session.setLastActivityAt(LocalDateTime.now());
            sessionRepository.save(session);

            String username = userRepository.findById(session.getUserId())
                    .map(User::getUsername).orElse("unknown");
            auditLogService.logWarn("ANOMALY_DETECTED", session.getUserId(), username,
                    session.getIpAddress(), String.join("; ", anomalies));
        }

        return response;
    }

    @Override
    public SessionResponse terminateSession(SessionTerminateRequest request) {
        Session session = sessionRepository.findBySessionId(request.getSessionId()).orElse(null);
        if (session == null) {
            return null;
        }

        session.setStatus("TERMINATED");
        session.setTerminatedAt(LocalDateTime.now());
        session.setTerminationReason(
                request.getReason() != null ? request.getReason() : "Manual termination");
        session = sessionRepository.save(session);

        String username = userRepository.findById(session.getUserId())
                .map(User::getUsername).orElse("unknown");
        auditLogService.logWarn("SESSION_TERMINATED", session.getUserId(), username,
                session.getIpAddress(), "Session " + session.getSessionId() + " terminated: "
                        + session.getTerminationReason());

        return toResponse(session);
    }

    @Override
    public void terminateSessionsForUser(Long userId, String reason) {
        List<Session> sessions = sessionRepository.findByUserIdAndStatus(userId, "ACTIVE");
        for (Session session : sessions) {
            session.setStatus("TERMINATED");
            session.setTerminatedAt(LocalDateTime.now());
            session.setTerminationReason(reason);
            sessionRepository.save(session);
        }
    }

    private SessionResponse toResponse(Session session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setSessionId(session.getSessionId());
        response.setUserId(session.getUserId());
        response.setDeviceId(session.getDeviceId());
        response.setIpAddress(session.getIpAddress());
        response.setUserRisk(session.getUserRisk());
        response.setDeviceRisk(session.getDeviceRisk());
        response.setContextRisk(session.getContextRisk());
        response.setFinalRisk(session.getFinalRisk());
        response.setStatus(session.getStatus());
        response.setAnomalyDetected(session.isAnomalyDetected());
        response.setAnomalyReason(session.getAnomalyReason());
        if (session.getStartedAt() != null) response.setStartedAt(session.getStartedAt().toString());
        if (session.getLastActivityAt() != null) response.setLastActivityAt(session.getLastActivityAt().toString());
        if (session.getTerminatedAt() != null) response.setTerminatedAt(session.getTerminatedAt().toString());
        response.setTerminationReason(session.getTerminationReason());
        return response;
    }
}
