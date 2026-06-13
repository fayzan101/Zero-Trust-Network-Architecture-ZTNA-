package com.yourname.zerotrust.policy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.support.TestDataFactory;

@SpringBootTest
@ActiveProfiles("test")
class PolicyEvaluatorTest {

    @Autowired
    private PolicyEvaluator policyEvaluator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    private User user;

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        user = TestDataFactory.createUser(userRepository, roleRepository, "policyuser", "USER");
        TestDataFactory.createPolicy(policyRepository, "Login Policy", "login", "access", null, 90);
        TestDataFactory.createPolicy(policyRepository, "Admin Policy", "admin", "access", "ADMIN", 60);
    }

    @Test
    void evaluate_allowsLoginWithinRiskThreshold() {
        PolicyEvaluateRequest request = new PolicyEvaluateRequest();
        request.setUserId(user.getId());
        request.setResource("login");
        request.setAction("access");
        request.setIpAddress("192.168.1.10");

        PolicyEvaluateResponse response = policyEvaluator.evaluate(request);

        assertTrue(response.isAllowed());
        assertEquals("ALLOW", response.getDecision());
    }

    @Test
    void evaluate_deniesAdminAccessForUserRole() {
        PolicyEvaluateRequest request = new PolicyEvaluateRequest();
        request.setUserId(user.getId());
        request.setResource("admin");
        request.setAction("access");

        PolicyEvaluateResponse response = policyEvaluator.evaluate(request);

        assertFalse(response.isAllowed());
        assertEquals("DENY", response.getDecision());
    }

    private void assertEquals(String expected, String actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
