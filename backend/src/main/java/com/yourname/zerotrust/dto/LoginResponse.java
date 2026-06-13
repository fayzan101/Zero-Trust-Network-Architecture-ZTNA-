package com.yourname.zerotrust.dto;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private String message;
    private String sessionId;
    private int userRisk;
    private int deviceRisk;
    private int contextRisk;
    private int finalRisk;
    private boolean accessAllowed = true;

    // Getters and setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
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
    public boolean isAccessAllowed() { return accessAllowed; }
    public void setAccessAllowed(boolean accessAllowed) { this.accessAllowed = accessAllowed; }
}
