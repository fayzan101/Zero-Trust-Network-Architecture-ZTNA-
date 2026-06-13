package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.AnomalyCheckResponse;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.dto.SessionResponse;
import com.yourname.zerotrust.dto.SessionTerminateRequest;
import com.yourname.zerotrust.entity.User;

import java.util.List;

public interface SessionService {
    SessionResponse createSession(User user, String deviceId, String ipAddress, RiskScoreResponse risk);
    List<SessionResponse> getActiveSessions();
    AnomalyCheckResponse checkAnomaly(String sessionId);
    SessionResponse terminateSession(SessionTerminateRequest request);
    void terminateSessionsForUser(Long userId, String reason);
}
