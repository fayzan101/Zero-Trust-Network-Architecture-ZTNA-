package com.yourname.zerotrust.dto;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private String message;

    // Getters and setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
