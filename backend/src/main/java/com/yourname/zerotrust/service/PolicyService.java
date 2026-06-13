package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.PolicyRequest;
import com.yourname.zerotrust.dto.PolicyResponse;

import java.util.List;

public interface PolicyService {
    PolicyResponse createPolicy(PolicyRequest request);
    List<PolicyResponse> listPolicies();
    PolicyResponse updatePolicy(Long id, PolicyRequest request);
    void deletePolicy(Long id);
    PolicyEvaluateResponse evaluate(PolicyEvaluateRequest request);
}
