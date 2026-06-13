package com.yourname.zerotrust.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.PolicyRequest;
import com.yourname.zerotrust.dto.PolicyResponse;
import com.yourname.zerotrust.entity.Policy;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AuditLogService;
import com.yourname.zerotrust.service.PolicyService;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PolicyEvaluator policyEvaluator;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PolicyResponse createPolicy(PolicyRequest request) {
        Policy policy = new Policy();
        applyRequest(policy, request);
        if (policy.getResource() == null) policy.setResource("*");
        if (policy.getAction() == null) policy.setAction("*");
        policy = policyRepository.save(policy);
        return toResponse(policy);
    }

    @Override
    public List<PolicyResponse> listPolicies() {
        return policyRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyResponse updatePolicy(Long id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id).orElse(null);
        if (policy == null) return null;
        applyRequest(policy, request);
        policy = policyRepository.save(policy);
        return toResponse(policy);
    }

    @Override
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }

    @Override
    public PolicyEvaluateResponse evaluate(PolicyEvaluateRequest request) {
        PolicyEvaluateResponse result = policyEvaluator.evaluate(request);
        if (!result.isAllowed()) {
            String username = userRepository.findById(request.getUserId())
                    .map(User::getUsername).orElse("unknown");
            auditLogService.logCritical("POLICY_DENIED", request.getUserId(), username,
                    request.getIpAddress(),
                    "Resource=" + request.getResource() + ", action=" + request.getAction()
                            + ", reason=" + result.getReason());
        }
        return result;
    }

    private void applyRequest(Policy policy, PolicyRequest request) {
        if (request.getName() != null) policy.setName(request.getName());
        if (request.getDescription() != null) policy.setDescription(request.getDescription());
        if (request.getResource() != null) policy.setResource(request.getResource());
        if (request.getAction() != null) policy.setAction(request.getAction());
        if (request.getRequiredRole() != null) policy.setRequiredRole(request.getRequiredRole());
        if (request.getMinDeviceTrust() != null) policy.setMinDeviceTrust(request.getMinDeviceTrust());
        if (request.getMaxRiskThreshold() != null) policy.setMaxRiskThreshold(request.getMaxRiskThreshold());
        if (request.getEnabled() != null) policy.setEnabled(request.getEnabled());
    }

    private PolicyResponse toResponse(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        response.setId(policy.getId());
        response.setName(policy.getName());
        response.setDescription(policy.getDescription());
        response.setResource(policy.getResource());
        response.setAction(policy.getAction());
        response.setRequiredRole(policy.getRequiredRole());
        response.setMinDeviceTrust(policy.getMinDeviceTrust());
        response.setMaxRiskThreshold(policy.getMaxRiskThreshold());
        response.setEnabled(policy.isEnabled());
        if (policy.getCreatedAt() != null) {
            response.setCreatedAt(policy.getCreatedAt().toString());
        }
        return response;
    }
}
