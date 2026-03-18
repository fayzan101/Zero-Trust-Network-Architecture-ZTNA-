package com.yourname.zerotrust.dto;

public class DeviceRegisterRequest {
    private String deviceId;
    private String userId;
    private String deviceType;
    private String os;
    private String ipAddress;

    // Getters and setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
