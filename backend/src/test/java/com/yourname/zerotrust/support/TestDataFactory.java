package com.yourname.zerotrust.support;

import java.util.HashSet;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.yourname.zerotrust.entity.Policy;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static Role ensureRole(RoleRepository roleRepository, String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }

    public static User createUser(UserRepository userRepository, RoleRepository roleRepository,
            String username, String roleName) {
        Role role = ensureRole(roleRepository, roleName);
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password123"));
        user.setMfaEnabled(false);
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public static Policy createPolicy(PolicyRepository policyRepository, String name,
            String resource, String action, String requiredRole, Integer maxRisk) {
        Policy policy = new Policy();
        policy.setName(name);
        policy.setResource(resource);
        policy.setAction(action);
        policy.setRequiredRole(requiredRole);
        policy.setMaxRiskThreshold(maxRisk);
        policy.setEnabled(true);
        return policyRepository.save(policy);
    }
}
