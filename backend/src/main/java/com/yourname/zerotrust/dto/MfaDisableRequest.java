package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class MfaDisableRequest {
    @NotBlank
    private String otp;

    @NotBlank
    private String password;

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
