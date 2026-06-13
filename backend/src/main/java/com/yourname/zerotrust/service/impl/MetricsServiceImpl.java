package com.yourname.zerotrust.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.ComparisonMetricsResponse;
import com.yourname.zerotrust.repository.AttackRepository;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.RiskScoreRepository;
import com.yourname.zerotrust.repository.SessionRepository;
import com.yourname.zerotrust.service.MetricsService;

@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RiskScoreRepository riskScoreRepository;

    @Autowired
    private AttackRepository attackRepository;

    @Override
    public ComparisonMetricsResponse getComparison() {
        long enabledPolicies = policyRepository.countByEnabledTrue();
        long activeSessions = sessionRepository.countByStatus("ACTIVE");
        Double avgRisk = riskScoreRepository.findAverageFinalRisk();
        long totalAttacks = attackRepository.count();
        long detected = attackRepository.countByDetectedTrue();

        ComparisonMetricsResponse response = new ComparisonMetricsResponse();

        ComparisonMetricsResponse.ModelMetrics traditional = new ComparisonMetricsResponse.ModelMetrics();
        traditional.setAccessControl("Static RBAC — binary allow/deny");
        traditional.setDeviceVerification("None — any device accepted");
        traditional.setRiskScoring("Not used");
        traditional.setContinuousMonitoring("Fixed session timeout only");
        traditional.setPoliciesEnforced(0);
        traditional.setActiveSessions(0);
        traditional.setAverageRiskScore(0);
        traditional.setAttacksSimulated(totalAttacks);
        traditional.setAttacksDetected(0);
        traditional.setDetectionRate(0);

        ComparisonMetricsResponse.ModelMetrics zeroTrust = new ComparisonMetricsResponse.ModelMetrics();
        zeroTrust.setAccessControl("Dynamic — policy + risk threshold");
        zeroTrust.setDeviceVerification("Per-device trust score");
        zeroTrust.setRiskScoring("Weighted user / device / context");
        zeroTrust.setContinuousMonitoring("Live sessions + anomaly detection");
        zeroTrust.setPoliciesEnforced(enabledPolicies);
        zeroTrust.setActiveSessions(activeSessions);
        zeroTrust.setAverageRiskScore(avgRisk != null ? avgRisk : 0);
        zeroTrust.setAttacksSimulated(totalAttacks);
        zeroTrust.setAttacksDetected(detected);
        zeroTrust.setDetectionRate(totalAttacks > 0 ? (double) detected / totalAttacks * 100.0 : 0);

        response.setTraditional(traditional);
        response.setZeroTrust(zeroTrust);
        return response;
    }
}
