package com.yourname.zerotrust.dto;

public class MfaSetupResponse {
    private String secret;
    private String otpauthUrl;
    private String message;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public String getOtpauthUrl() { return otpauthUrl; }
    public void setOtpauthUrl(String otpauthUrl) { this.otpauthUrl = otpauthUrl; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
