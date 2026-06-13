package com.yourname.zerotrust.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.entity.Session;
import com.yourname.zerotrust.repository.SessionRepository;
import com.yourname.zerotrust.risk.RiskCalculator;

@Component
public class AnomalyDetector {

    private static final int HIGH_RISK_THRESHOLD = 70;
    private static final int MAX_CONCURRENT_SESSIONS = 3;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RiskCalculator riskCalculator;

    public List<String> detect(Session session) {
        List<String> anomalies = new ArrayList<>();

        if (session.getFinalRisk() >= HIGH_RISK_THRESHOLD) {
            anomalies.add("High risk score: " + session.getFinalRisk());
        }

        if (riskCalculator.isOffHours()) {
            anomalies.add("Access during off-hours");
        }

        if (session.getIpAddress() == null || session.getIpAddress().isBlank()) {
            anomalies.add("Missing IP address context");
        }

        long activeSessions = sessionRepository.countByUserIdAndStatus(session.getUserId(), "ACTIVE");
        if (activeSessions > MAX_CONCURRENT_SESSIONS) {
            anomalies.add("Too many concurrent active sessions: " + activeSessions);
        }

        if (session.getDeviceId() == null || session.getDeviceId().isBlank()) {
            anomalies.add("No device associated with session");
        }

        return anomalies;
    }
}
