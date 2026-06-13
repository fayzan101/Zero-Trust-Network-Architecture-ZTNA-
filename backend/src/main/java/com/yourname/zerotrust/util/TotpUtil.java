package com.yourname.zerotrust.util;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

@Component
public class TotpUtil {

    private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

    public String generateSecret() {
        GoogleAuthenticatorKey key = authenticator.createCredentials();
        return key.getKey();
    }

    public boolean verifyCode(String secret, int code) {
        if (secret == null || secret.isBlank()) {
            return false;
        }
        return authenticator.authorize(secret, code);
    }

    public boolean verifyCode(String secret, String code) {
        if (code == null || !code.matches("\\d{6}")) {
            return false;
        }
        return verifyCode(secret, Integer.parseInt(code));
    }

    public String buildOtpAuthUrl(String username, String secret) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("ZTNA", username,
                new GoogleAuthenticatorKey.Builder(secret).build());
    }
}
