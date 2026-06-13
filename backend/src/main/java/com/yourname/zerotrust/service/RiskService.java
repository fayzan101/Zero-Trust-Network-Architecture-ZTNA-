package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.RiskCalculateRequest;
import com.yourname.zerotrust.dto.RiskScoreResponse;

import java.util.List;

public interface RiskService {
    RiskScoreResponse calculateRisk(RiskCalculateRequest request);
    List<RiskScoreResponse> getUserRiskHistory(Long userId);
    RiskScoreResponse getSessionRisk(String sessionId);
}
