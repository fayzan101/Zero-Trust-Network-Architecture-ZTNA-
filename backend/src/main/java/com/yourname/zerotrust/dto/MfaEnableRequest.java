package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class MfaEnableRequest {
    @NotBlank
    private String otp;

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
