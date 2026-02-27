package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank
    private String refreshToken;

    // Getters and setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
