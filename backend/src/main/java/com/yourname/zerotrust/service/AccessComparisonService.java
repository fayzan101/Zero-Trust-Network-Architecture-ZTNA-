package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.AccessComparisonResponse;
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;

public interface AccessComparisonService {
    AccessComparisonResponse compare(PolicyEvaluateRequest request);
}
