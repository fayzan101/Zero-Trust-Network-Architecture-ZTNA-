package com.yourname.zerotrust.config;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.DeviceRepository;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;

@Component
@Profile("!test")
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository, RoleRepository roleRepository,
            DeviceRepository deviceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.deviceRepository = deviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role userRole = roleRepository.findByName("USER").orElse(null);
        if (adminRole == null || userRole == null) {
            log.warn("Skipping demo seed — roles not found");
            return;
        }

        User admin = ensureUser("admin", "admin@ztna.local", "Admin123!", adminRole, true);
        User demo = ensureUser("demo", "demo@ztna.local", "Demo123!", userRole, false);
        ensureDevice(demo, "demo-laptop-01", "laptop", "linux", "192.168.1.50", 85);

        log.info("Demo data ready — admin/Admin123! demo/Demo123! device=demo-laptop-01");
    }

    private User ensureUser(String username, String email, String password, Role role, boolean mfaEnabled) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setMfaEnabled(mfaEnabled);
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            user.setLastLogin(LocalDateTime.now().minusDays(1));
            return userRepository.save(user);
        });
    }

    private void ensureDevice(User owner, String deviceId, String type, String os, String ip, int trustScore) {
        if (deviceRepository.findByDeviceId(deviceId) != null) {
            return;
        }
        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setOwner(owner);
        device.setDeviceType(type);
        device.setOs(os);
        device.setIpAddress(ip);
        device.setTrustScore(trustScore);
        device.setRegisteredAt(LocalDateTime.now());
        deviceRepository.save(device);
    }
}
