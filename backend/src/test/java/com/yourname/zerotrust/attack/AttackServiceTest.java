package com.yourname.zerotrust.attack;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.dto.PrivilegeEscalationRequest;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.AttackRepository;
import com.yourname.zerotrust.repository.PolicyRepository;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AttackService;
import com.yourname.zerotrust.support.TestDataFactory;

@SpringBootTest
@ActiveProfiles("test")
class AttackServiceTest {

    @Autowired
    private AttackService attackService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private AttackRepository attackRepository;

    private User user;

    @BeforeEach
    void setUp() {
        attackRepository.deleteAll();
        policyRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        user = TestDataFactory.createUser(userRepository, roleRepository, "attackuser", "USER");
        TestDataFactory.createPolicy(policyRepository, "Admin Policy", "admin", "access", "ADMIN", 60);
    }

    @Test
    void privilegeEscalation_detectsUnauthorizedAdminAccess() {
        PrivilegeEscalationRequest request = new PrivilegeEscalationRequest();
        request.setUserId(user.getId());
        request.setResource("admin");
        request.setAction("access");
        request.setTargetRole("ADMIN");

        var response = attackService.simulatePrivilegeEscalation(request);

        assertTrue(response.isDetected());
        assertTrue(attackRepository.count() == 1);
    }
}
