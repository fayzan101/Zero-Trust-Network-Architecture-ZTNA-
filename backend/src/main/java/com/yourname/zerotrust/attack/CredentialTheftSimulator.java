package com.yourname.zerotrust.attack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.dto.CredentialTheftRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.RiskCalculateRequest;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.exception.BadRequestException;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.RiskService;

@Component
public class CredentialTheftSimulator {

    private static final int HIGH_RISK_THRESHOLD = 65;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiskService riskService;

    @Autowired
    private PolicyEvaluator policyEvaluator;

    public SimulationResult simulate(CredentialTheftRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("username is required");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUsername()));

        String attackerIp = request.getStolenFromIp() != null ? request.getStolenFromIp() : "203.0.113.50";

        RiskCalculateRequest riskRequest = new RiskCalculateRequest();
        riskRequest.setUserId(user.getId());
        riskRequest.setDeviceId(request.getAttackerDeviceId());
        riskRequest.setIpAddress(attackerIp);
        RiskScoreResponse risk = riskService.calculateRisk(riskRequest);

        PolicyEvaluateRequest policyRequest = new PolicyEvaluateRequest();
        policyRequest.setUserId(user.getId());
        policyRequest.setResource("login");
        policyRequest.setAction("access");
        policyRequest.setDeviceId(request.getAttackerDeviceId());
        policyRequest.setIpAddress(attackerIp);
        PolicyEvaluateResponse policy = policyEvaluator.evaluate(policyRequest);

        boolean detected = false;
        String detectionMethod = null;
        String detectionDetails = null;
        String severity = "LOW";

        if (!policy.isAllowed()) {
            detected = true;
            detectionMethod = "POLICY_ENGINE";
            detectionDetails = "Login blocked: " + policy.getReason();
            severity = "CRITICAL";
        } else if (risk.getFinalRisk() >= HIGH_RISK_THRESHOLD) {
            detected = true;
            detectionMethod = "RISK_ENGINE";
            detectionDetails = "High risk score " + risk.getFinalRisk() + " from untrusted context";
            severity = "HIGH";
        } else if (!user.isMfaEnabled()) {
            detected = true;
            detectionMethod = "MFA_GAP";
            detectionDetails = "Credential theft would succeed without MFA — zero trust recommends MFA";
            severity = "MEDIUM";
        }

        String details = "Simulated login from IP " + attackerIp
                + " using stolen credentials for user " + user.getUsername();

        return new SimulationResult(user, attackerIp, details, detected, detectionMethod,
                detectionDetails, severity, risk.getFinalRisk());
    }
}
