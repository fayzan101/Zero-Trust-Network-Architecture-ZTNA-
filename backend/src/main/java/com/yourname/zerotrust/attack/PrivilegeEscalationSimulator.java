package com.yourname.zerotrust.attack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.PrivilegeEscalationRequest;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.exception.BadRequestException;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.repository.UserRepository;

@Component
public class PrivilegeEscalationSimulator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyEvaluator policyEvaluator;

    public SimulationResult simulate(PrivilegeEscalationRequest request) {
        if (request.getUserId() == null) {
            throw new BadRequestException("userId is required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String resource = request.getResource() != null ? request.getResource() : "admin";
        String action = request.getAction() != null ? request.getAction() : "access";
        String targetRole = request.getTargetRole() != null ? request.getTargetRole() : "ADMIN";

        PolicyEvaluateRequest policyRequest = new PolicyEvaluateRequest();
        policyRequest.setUserId(user.getId());
        policyRequest.setResource(resource);
        policyRequest.setAction(action);
        PolicyEvaluateResponse policy = policyEvaluator.evaluate(policyRequest);

        boolean detected = !policy.isAllowed();
        String detectionMethod;
        String detectionDetails;
        String severity;

        if (detected) {
            detectionMethod = "POLICY_ENGINE";
            detectionDetails = "Access to " + resource + "/" + action + " denied: " + policy.getReason();
            severity = "CRITICAL";
        } else {
            detected = true;
            detectionMethod = "RBAC_GAP";
            detectionDetails = "Policy allowed access but user lacks " + targetRole + " role — configuration gap";
            severity = "HIGH";
        }

        String details = "User " + user.getUsername() + " attempted privilege escalation to "
                + targetRole + " on resource " + resource;

        return new SimulationResult(user, null, details, detected,
                detectionMethod, detectionDetails, severity, policy.getFinalRisk());
    }
}
