package com.yourname.zerotrust.access;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AccessComparisonService;
import com.yourname.zerotrust.support.TestDataFactory;

@SpringBootTest
@ActiveProfiles("test")
class AccessComparisonServiceTest {

    @Autowired
    private AccessComparisonService accessComparisonService;

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

        user = TestDataFactory.createUser(userRepository, roleRepository, "compareuser", "USER");
        TestDataFactory.createPolicy(policyRepository, "Strict Login", "login", "access", null, 30);
    }

    @Test
    void compare_traditionalAllowsButZeroTrustDeniesOnHighRisk() {
        PolicyEvaluateRequest request = new PolicyEvaluateRequest();
        request.setUserId(user.getId());
        request.setResource("login");
        request.setAction("access");
        request.setIpAddress("203.0.113.99");
        request.setDeviceId("unknown-device");

        var result = accessComparisonService.compare(request);

        assertTrue(result.getTraditional().isAllowed());
        assertFalse(result.getZeroTrust().isAllowed());
        assertTrue(result.isOutcomesDiffer());
    }
}
