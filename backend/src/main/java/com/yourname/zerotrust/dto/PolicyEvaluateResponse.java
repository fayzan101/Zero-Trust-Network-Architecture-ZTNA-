package com.yourname.zerotrust.dto;

public class PolicyEvaluateResponse {
    private boolean allowed;
    private String decision;
    private String reason;
    private String matchedPolicy;
    private int userRisk;
    private int deviceRisk;
    private int contextRisk;
    private int finalRisk;

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getMatchedPolicy() { return matchedPolicy; }
    public void setMatchedPolicy(String matchedPolicy) { this.matchedPolicy = matchedPolicy; }
    public int getUserRisk() { return userRisk; }
    public void setUserRisk(int userRisk) { this.userRisk = userRisk; }
    public int getDeviceRisk() { return deviceRisk; }
    public void setDeviceRisk(int deviceRisk) { this.deviceRisk = deviceRisk; }
    public int getContextRisk() { return contextRisk; }
    public void setContextRisk(int contextRisk) { this.contextRisk = contextRisk; }
    public int getFinalRisk() { return finalRisk; }
    public void setFinalRisk(int finalRisk) { this.finalRisk = finalRisk; }
}
