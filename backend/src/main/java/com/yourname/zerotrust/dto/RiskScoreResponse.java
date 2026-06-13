package com.yourname.zerotrust.dto;

import java.util.List;

public class RiskScoreResponse {
    private Long id;
    private Long userId;
    private String sessionId;
    private int userRisk;
    private int deviceRisk;
    private int contextRisk;
    private int finalRisk;
    private String calculatedAt;
    private List<String> reasons;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public int getUserRisk() { return userRisk; }
    public void setUserRisk(int userRisk) { this.userRisk = userRisk; }
    public int getDeviceRisk() { return deviceRisk; }
    public void setDeviceRisk(int deviceRisk) { this.deviceRisk = deviceRisk; }
    public int getContextRisk() { return contextRisk; }
    public void setContextRisk(int contextRisk) { this.contextRisk = contextRisk; }
    public int getFinalRisk() { return finalRisk; }
    public void setFinalRisk(int finalRisk) { this.finalRisk = finalRisk; }
    public String getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(String calculatedAt) { this.calculatedAt = calculatedAt; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}
