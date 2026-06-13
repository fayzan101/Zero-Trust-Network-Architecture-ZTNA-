package com.yourname.zerotrust.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class JwtSecretValidator {

    private static final int MIN_SECRET_LENGTH = 32;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @EventListener(ApplicationReadyEvent.class)
    public void validateSecret() {
        if (jwtSecret == null || jwtSecret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least " + MIN_SECRET_LENGTH + " characters");
        }
        if ("your_jwt_secret_here".equals(jwtSecret)) {
            throw new IllegalStateException(
                    "JWT_SECRET is still the default placeholder — set a strong secret in .env");
        }
    }
}
