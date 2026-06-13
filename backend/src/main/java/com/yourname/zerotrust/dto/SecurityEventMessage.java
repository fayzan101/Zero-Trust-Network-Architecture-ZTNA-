package com.yourname.zerotrust.dto;

public class SecurityEventMessage {
    private String type;
    private String eventType;
    private String severity;
    private String username;
    private Long userId;
    private String ipAddress;
    private String details;
    private String timestamp;
    private Integer finalRisk;
    private String stepUpLevel;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public Integer getFinalRisk() { return finalRisk; }
    public void setFinalRisk(Integer finalRisk) { this.finalRisk = finalRisk; }
    public String getStepUpLevel() { return stepUpLevel; }
    public void setStepUpLevel(String stepUpLevel) { this.stepUpLevel = stepUpLevel; }
}
