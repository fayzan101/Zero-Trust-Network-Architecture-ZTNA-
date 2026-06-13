package com.yourname.zerotrust.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.Policy;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.DeviceRepository;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.RiskService;

@Component
public class PolicyEvaluator {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RiskService riskService;

    public PolicyEvaluateResponse evaluate(PolicyEvaluateRequest request) {
        PolicyEvaluateResponse response = new PolicyEvaluateResponse();

        if (request.getUserId() == null || request.getResource() == null || request.getAction() == null) {
            response.setAllowed(false);
            response.setDecision("DENY");
            response.setReason("userId, resource, and action are required");
            return response;
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            response.setAllowed(false);
            response.setDecision("DENY");
            response.setReason("User not found");
            return response;
        }

        RiskScoreResponse risk = riskService.calculateRisk(toRiskRequest(request));
        response.setUserRisk(risk.getUserRisk());
        response.setDeviceRisk(risk.getDeviceRisk());
        response.setContextRisk(risk.getContextRisk());
        response.setFinalRisk(risk.getFinalRisk());

        List<Policy> matchingPolicies = findMatchingPolicies(request.getResource(), request.getAction());
        if (matchingPolicies.isEmpty()) {
            response.setAllowed(false);
            response.setDecision("DENY");
            response.setReason("No matching policy found");
            return response;
        }

        Device device = request.getDeviceId() != null
                ? deviceRepository.findByDeviceId(request.getDeviceId())
                : null;

        for (Policy policy : matchingPolicies) {
            String failure = checkPolicy(policy, user, device, risk.getFinalRisk());
            if (failure != null) {
                response.setAllowed(false);
                response.setDecision("DENY");
                response.setReason(failure);
                response.setMatchedPolicy(policy.getName());
                return response;
            }
        }

        Policy primaryPolicy = matchingPolicies.get(0);
        response.setAllowed(true);
        response.setDecision("ALLOW");
        response.setReason("All policy checks passed");
        response.setMatchedPolicy(primaryPolicy.getName());
        return response;
    }

    private List<Policy> findMatchingPolicies(String resource, String action) {
        List<Policy> allEnabled = policyRepository.findByEnabledTrue();
        List<Policy> matches = new ArrayList<>();

        for (Policy policy : allEnabled) {
            if (matchesResource(policy.getResource(), resource)
                    && matchesAction(policy.getAction(), action)) {
                matches.add(policy);
            }
        }
        return matches;
    }

    private boolean matchesResource(String policyResource, String requestedResource) {
        return "*".equals(policyResource) || policyResource.equalsIgnoreCase(requestedResource);
    }

    private boolean matchesAction(String policyAction, String requestedAction) {
        return "*".equals(policyAction) || policyAction.equalsIgnoreCase(requestedAction);
    }

    private String checkPolicy(Policy policy, User user, Device device, int finalRisk) {
        if (policy.getRequiredRole() != null && !policy.getRequiredRole().isBlank()) {
            Set<String> userRoles = user.getRoles() == null ? Set.of() :
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            List<String> requiredRoles = Arrays.stream(policy.getRequiredRole().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            boolean hasRole = requiredRoles.stream().anyMatch(userRoles::contains);
            if (!hasRole) {
                return "Missing required role: " + policy.getRequiredRole();
            }
        }

        if (policy.getMinDeviceTrust() != null) {
            if (device == null) {
                return "Device required for trust check (min trust: " + policy.getMinDeviceTrust() + ")";
            }
            if (device.getTrustScore() < policy.getMinDeviceTrust()) {
                return "Device trust score " + device.getTrustScore()
                        + " below minimum " + policy.getMinDeviceTrust();
            }
        }

        if (policy.getMaxRiskThreshold() != null && finalRisk > policy.getMaxRiskThreshold()) {
            return "Risk score " + finalRisk + " exceeds threshold " + policy.getMaxRiskThreshold();
        }

        return null;
    }

    private com.yourname.zerotrust.dto.RiskCalculateRequest toRiskRequest(PolicyEvaluateRequest request) {
        com.yourname.zerotrust.dto.RiskCalculateRequest riskRequest =
                new com.yourname.zerotrust.dto.RiskCalculateRequest();
        riskRequest.setUserId(request.getUserId());
        riskRequest.setDeviceId(request.getDeviceId());
        riskRequest.setIpAddress(request.getIpAddress());
        riskRequest.setSessionId(request.getSessionId());
        return riskRequest;
    }
}
