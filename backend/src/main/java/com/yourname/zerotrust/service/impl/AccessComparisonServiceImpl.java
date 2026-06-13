package com.yourname.zerotrust.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.AccessComparisonResponse;
import com.yourname.zerotrust.dto.AccessDecisionResponse;
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.policy.TraditionalAccessEvaluator;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AccessComparisonService;
import com.yourname.zerotrust.service.AuditLogService;

@Service
public class AccessComparisonServiceImpl implements AccessComparisonService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraditionalAccessEvaluator traditionalAccessEvaluator;

    @Autowired
    private PolicyEvaluator policyEvaluator;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public AccessComparisonResponse compare(PolicyEvaluateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AccessDecisionResponse traditional = traditionalAccessEvaluator.evaluate(
                user, request.getResource(), request.getAction());

        PolicyEvaluateResponse zeroTrustResult = policyEvaluator.evaluate(request);
        AccessDecisionResponse zeroTrust = new AccessDecisionResponse();
        zeroTrust.setModel("ZERO_TRUST");
        zeroTrust.setAllowed(zeroTrustResult.isAllowed());
        zeroTrust.setDecision(zeroTrustResult.getDecision());
        zeroTrust.setReason(zeroTrustResult.getReason());
        zeroTrust.setMatchedPolicy(zeroTrustResult.getMatchedPolicy());
        zeroTrust.setFinalRisk(zeroTrustResult.getFinalRisk());
        zeroTrust.setUserRisk(zeroTrustResult.getUserRisk());
        zeroTrust.setDeviceRisk(zeroTrustResult.getDeviceRisk());
        zeroTrust.setContextRisk(zeroTrustResult.getContextRisk());

        AccessComparisonResponse response = new AccessComparisonResponse();
        response.setTraditional(traditional);
        response.setZeroTrust(zeroTrust);
        response.setOutcomesDiffer(traditional.isAllowed() != zeroTrust.isAllowed());

        auditLogService.logInfo("ACCESS_COMPARED", user.getId(), user.getUsername(),
                request.getIpAddress(),
                "traditional=" + traditional.isAllowed() + " zeroTrust=" + zeroTrust.isAllowed()
                        + " resource=" + request.getResource());

        return response;
    }
}
