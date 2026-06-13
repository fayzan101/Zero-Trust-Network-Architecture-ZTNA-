package com.yourname.zerotrust.attack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.dto.LateralMovementRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.entity.Session;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.exception.BadRequestException;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.repository.SessionRepository;
import com.yourname.zerotrust.repository.UserRepository;

@Component
public class LateralMovementSimulator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PolicyEvaluator policyEvaluator;

    public SimulationResult simulate(LateralMovementRequest request) {
        if (request.getUserId() == null) {
            throw new BadRequestException("userId is required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String targetResource = request.getTargetResource() != null ? request.getTargetResource() : "sensitive";
        String targetIp = request.getTargetIp() != null ? request.getTargetIp() : "10.0.0.99";

        boolean ipMismatch = false;
        String sessionIp = null;
        if (request.getSourceSessionId() != null) {
            Session session = sessionRepository.findBySessionId(request.getSourceSessionId()).orElse(null);
            if (session != null) {
                sessionIp = session.getIpAddress();
                ipMismatch = sessionIp != null && !sessionIp.equals(targetIp);
            }
        }

        PolicyEvaluateRequest policyRequest = new PolicyEvaluateRequest();
        policyRequest.setUserId(user.getId());
        policyRequest.setResource(targetResource);
        policyRequest.setAction("access");
        policyRequest.setIpAddress(targetIp);
        policyRequest.setSessionId(request.getSourceSessionId());
        PolicyEvaluateResponse policy = policyEvaluator.evaluate(policyRequest);

        boolean detected = false;
        String detectionMethod = null;
        String detectionDetails = null;
        String severity = "LOW";

        if (ipMismatch) {
            detected = true;
            detectionMethod = "ANOMALY_DETECTOR";
            detectionDetails = "IP changed from " + sessionIp + " to " + targetIp + " during session";
            severity = "HIGH";
        }

        if (!policy.isAllowed()) {
            detected = true;
            detectionMethod = detectionMethod != null
                    ? detectionMethod + "+POLICY_ENGINE" : "POLICY_ENGINE";
            String policyDetail = "Access to " + targetResource + " denied: " + policy.getReason();
            detectionDetails = detectionDetails != null
                    ? detectionDetails + "; " + policyDetail : policyDetail;
            severity = "CRITICAL";
        }

        if (!detected) {
            detectionMethod = "NONE";
            detectionDetails = "Lateral movement to " + targetResource + " was not blocked";
            severity = "HIGH";
        }

        String details = "User " + user.getUsername() + " attempted lateral movement to "
                + targetResource + " from IP " + targetIp;

        return new SimulationResult(user, targetIp, details, detected,
                detectionMethod, detectionDetails, severity, policy.getFinalRisk());
    }
}
