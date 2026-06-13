package com.yourname.zerotrust.dto;
import jakarta.validation.constraints.NotBlank;
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private String deviceId;
    private String ipAddress;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
