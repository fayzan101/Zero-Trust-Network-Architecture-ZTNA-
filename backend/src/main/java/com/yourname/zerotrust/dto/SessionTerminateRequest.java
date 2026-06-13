package com.yourname.zerotrust.dto;

public class SessionTerminateRequest {
    private String sessionId;
    private String reason;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
