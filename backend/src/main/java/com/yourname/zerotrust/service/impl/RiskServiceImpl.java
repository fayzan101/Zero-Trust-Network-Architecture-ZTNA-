package com.yourname.zerotrust.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.RiskCalculateRequest;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.RiskScore;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.repository.DeviceRepository;
import com.yourname.zerotrust.repository.RiskScoreRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.risk.RiskBreakdown;
import com.yourname.zerotrust.risk.RiskCalculator;
import com.yourname.zerotrust.service.RiskService;

@Service
public class RiskServiceImpl implements RiskService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RiskScoreRepository riskScoreRepository;

    @Autowired
    private RiskCalculator riskCalculator;

    @Override
    public RiskScoreResponse calculateRisk(RiskCalculateRequest request) {
        if (request.getUserId() == null) {
            return new RiskScoreResponse();
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Device device = request.getDeviceId() != null
                ? deviceRepository.findByDeviceId(request.getDeviceId())
                : null;

        boolean newDevice = device == null && request.getDeviceId() != null && !request.getDeviceId().isBlank();

        RiskBreakdown breakdown = riskCalculator.assess(user, device, request.getIpAddress(), newDevice);

        RiskScore riskScore = new RiskScore();
        riskScore.setUserId(user.getId());
        riskScore.setSessionId(request.getSessionId());
        riskScore.setUserRisk(breakdown.getUserRisk());
        riskScore.setDeviceRisk(breakdown.getDeviceRisk());
        riskScore.setContextRisk(breakdown.getContextRisk());
        riskScore.setFinalRisk(breakdown.getFinalRisk());
        riskScore = riskScoreRepository.save(riskScore);

        return toResponse(riskScore, breakdown.getReasons());
    }

    @Override
    public List<RiskScoreResponse> getUserRiskHistory(Long userId) {
        return riskScoreRepository.findByUserIdOrderByCalculatedAtDesc(userId).stream()
                .map(score -> toResponse(score, null))
                .collect(Collectors.toList());
    }

    @Override
    public RiskScoreResponse getSessionRisk(String sessionId) {
        return riskScoreRepository.findFirstBySessionIdOrderByCalculatedAtDesc(sessionId)
                .map(score -> toResponse(score, null))
                .orElse(null);
    }

    private RiskScoreResponse toResponse(RiskScore riskScore, List<String> reasons) {
        RiskScoreResponse response = new RiskScoreResponse();
        response.setId(riskScore.getId());
        response.setUserId(riskScore.getUserId());
        response.setSessionId(riskScore.getSessionId());
        response.setUserRisk(riskScore.getUserRisk());
        response.setDeviceRisk(riskScore.getDeviceRisk());
        response.setContextRisk(riskScore.getContextRisk());
        response.setFinalRisk(riskScore.getFinalRisk());
        response.setReasons(reasons);
        if (riskScore.getCalculatedAt() != null) {
            response.setCalculatedAt(riskScore.getCalculatedAt().toString());
        }
        return response;
    }
}
