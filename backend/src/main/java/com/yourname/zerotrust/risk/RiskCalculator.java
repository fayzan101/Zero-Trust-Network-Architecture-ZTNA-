package com.yourname.zerotrust.risk;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.User;

@Component
public class RiskCalculator {

    public RiskBreakdown assess(User user, Device device, String ipAddress, boolean newDevice) {
        RiskBreakdown breakdown = new RiskBreakdown();

        int userRisk = 10;
        if (!user.isMfaEnabled()) {
            userRisk += 25;
            breakdown.addReason("MFA not enabled (+25 user risk)");
        }
        if (user.getCreatedAt() != null) {
            long accountAgeDays = ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now());
            if (accountAgeDays < 7) {
                userRisk += 15;
                breakdown.addReason("New account (<7 days) (+15 user risk)");
            }
        }
        if (user.getLastLogin() == null) {
            userRisk += 10;
            breakdown.addReason("First login (+10 user risk)");
        } else {
            long daysSinceLogin = ChronoUnit.DAYS.between(user.getLastLogin(), LocalDateTime.now());
            if (daysSinceLogin > 30) {
                userRisk += 15;
                breakdown.addReason("Inactive >30 days (+15 user risk)");
            }
        }
        breakdown.setUserRisk(clamp(userRisk));

        int deviceRisk = calculateDeviceRisk(device);
        breakdown.setDeviceRisk(deviceRisk);
        if (device == null) {
            breakdown.addReason("Unknown device (+50 device risk)");
        } else {
            breakdown.addReason("Device trust score " + device.getTrustScore()
                    + " → device risk " + deviceRisk);
        }

        boolean offHours = isOffHours();
        int contextRisk = 10;
        if (ipAddress == null || ipAddress.isBlank()) {
            contextRisk += 20;
            breakdown.addReason("Missing IP address (+20 context risk)");
        } else if (isPrivateIp(ipAddress)) {
            contextRisk += 5;
            breakdown.addReason("Private IP " + ipAddress + " (+5 context risk)");
        } else {
            contextRisk += 15;
            breakdown.addReason("External IP " + ipAddress + " (+15 context risk)");
        }
        if (newDevice) {
            contextRisk += 25;
            breakdown.addReason("New/unregistered device (+25 context risk)");
        }
        if (offHours) {
            contextRisk += 20;
            breakdown.addReason("Off-hours access (+20 context risk)");
        }
        breakdown.setContextRisk(clamp(contextRisk));

        int finalRisk = calculateFinalRisk(breakdown.getUserRisk(), breakdown.getDeviceRisk(),
                breakdown.getContextRisk());
        breakdown.setFinalRisk(finalRisk);
        breakdown.addReason("Final risk = weighted 30/40/30 → " + finalRisk);

        return breakdown;
    }

    public int calculateUserRisk(User user) {
        return assess(user, null, "127.0.0.1", false).getUserRisk();
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
        if (newDevice) risk += 25;
        if (offHours) risk += 20;
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
