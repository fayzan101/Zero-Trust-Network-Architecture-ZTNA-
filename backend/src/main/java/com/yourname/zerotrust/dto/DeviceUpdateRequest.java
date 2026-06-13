package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class DeviceUpdateRequest {
    private String deviceType;
    private String os;

    @NotBlank
    private String ipAddress;

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
