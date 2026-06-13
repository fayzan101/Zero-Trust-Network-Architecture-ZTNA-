package com.yourname.zerotrust.dto;

public class DeviceTrustScoreResponse {
    private String deviceId;
    private int trustScore;

    public DeviceTrustScoreResponse(String deviceId, int trustScore) {
        this.deviceId = deviceId;
        this.trustScore = trustScore;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public int getTrustScore() { return trustScore; }
    public void setTrustScore(int trustScore) { this.trustScore = trustScore; }
}
