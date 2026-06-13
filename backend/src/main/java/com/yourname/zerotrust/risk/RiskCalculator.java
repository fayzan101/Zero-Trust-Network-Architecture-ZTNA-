package com.yourname.zerotrust.risk;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.User;

@Component
public class RiskCalculator {

    public int calculateUserRisk(User user) {
        int risk = 10;

        if (!user.isMfaEnabled()) {
            risk += 25;
        }

        if (user.getCreatedAt() != null) {
            long accountAgeDays = ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now());
            if (accountAgeDays < 7) {
                risk += 15;
            }
        }

        if (user.getLastLogin() == null) {
            risk += 10;
        } else {
            long daysSinceLogin = ChronoUnit.DAYS.between(user.getLastLogin(), LocalDateTime.now());
            if (daysSinceLogin > 30) {
                risk += 15;
            }
        }

        return clamp(risk);
    }

    public int calculateDeviceRisk(Device device) {
        if (device == null) {
            return 50;
        }
        return clamp(100 - device.getTrustScore());
    }

    public int calculateContextRisk(String ipAddress, boolean newDevice, boolean offHours) {
        int risk = 10;

        if (ipAddress == null || ipAddress.isBlank()) {
            risk += 20;
        } else if (isPrivateIp(ipAddress)) {
            risk += 5;
        } else {
            risk += 15;
        }

        if (newDevice) {
            risk += 25;
        }

        if (offHours) {
            risk += 20;
        }

        return clamp(risk);
    }

    public int calculateFinalRisk(int userRisk, int deviceRisk, int contextRisk) {
        double weighted = (userRisk * 0.30) + (deviceRisk * 0.40) + (contextRisk * 0.30);
        return clamp((int) Math.round(weighted));
    }

    public boolean isOffHours() {
        int hour = LocalDateTime.now().getHour();
        return hour < 6 || hour >= 22;
    }

    private boolean isPrivateIp(String ip) {
        return ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("127.");
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
