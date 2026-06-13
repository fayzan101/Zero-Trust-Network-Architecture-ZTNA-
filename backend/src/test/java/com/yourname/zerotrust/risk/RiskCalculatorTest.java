package com.yourname.zerotrust.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;

class RiskCalculatorTest {

    private RiskCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new RiskCalculator();
    }

    @Test
    void calculateFinalRisk_appliesWeightedAverage() {
        int result = calculator.calculateFinalRisk(40, 30, 50);
        assertEquals(39, result);
    }

    @Test
    void calculateUserRisk_increasesWhenMfaDisabled() {
        User withMfa = user(false, true);
        User withoutMfa = user(false, false);

        assertTrue(calculator.calculateUserRisk(withoutMfa) > calculator.calculateUserRisk(withMfa));
    }

    @Test
    void calculateDeviceRisk_inverselyCorrelatesWithTrustScore() {
        assertEquals(20, calculator.calculateDeviceRisk(deviceWithTrust(80)));
        assertEquals(70, calculator.calculateDeviceRisk(deviceWithTrust(30)));
    }

    @Test
    void calculateContextRisk_penalizesNewDeviceAndMissingIp() {
        int baseline = calculator.calculateContextRisk("192.168.1.1", false, false);
        int newDevice = calculator.calculateContextRisk("192.168.1.1", true, false);
        int missingIp = calculator.calculateContextRisk(null, false, false);

        assertTrue(newDevice > baseline);
        assertTrue(missingIp > baseline);
    }

    @Test
    void calculateFinalRisk_clampsTo100() {
        assertEquals(100, calculator.calculateFinalRisk(100, 100, 100));
        assertEquals(0, calculator.calculateFinalRisk(0, 0, 0));
    }

    private User user(boolean newAccount, boolean mfaEnabled) {
        User user = new User();
        user.setMfaEnabled(mfaEnabled);
        user.setCreatedAt(newAccount ? LocalDateTime.now() : LocalDateTime.now().minusDays(30));
        user.setLastLogin(LocalDateTime.now().minusDays(1));
        return user;
    }

    private com.yourname.zerotrust.entity.Device deviceWithTrust(int trustScore) {
        com.yourname.zerotrust.entity.Device device = new com.yourname.zerotrust.entity.Device();
        device.setTrustScore(trustScore);
        return device;
    }
}
