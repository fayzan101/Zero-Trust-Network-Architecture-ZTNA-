package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class MfaRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String otp;

    private String deviceId;
    private String ipAddress;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
