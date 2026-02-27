package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class MfaRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String otp;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
