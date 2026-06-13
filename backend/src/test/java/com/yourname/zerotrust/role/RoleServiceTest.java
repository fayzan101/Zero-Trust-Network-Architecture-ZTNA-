package com.yourname.zerotrust.role;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.dto.RoleRequest;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.RoleService;
import com.yourname.zerotrust.support.TestDataFactory;

@SpringBootTest
@ActiveProfiles("test")
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        TestDataFactory.ensureRole(roleRepository, "USER");
        TestDataFactory.ensureRole(roleRepository, "ADMIN");
    }

    @Test
    void deleteRole_blocksBuiltInRoles() {
        Role userRole = roleRepository.findByName("USER").orElseThrow();
        var response = roleService.deleteRole(userRole.getId());
        assertTrue(response.getMessage().contains("Cannot delete built-in role"));
    }

    @Test
    void deleteRole_removesUnusedCustomRole() {
        RoleRequest request = new RoleRequest();
        request.setName("AUDITOR");
        roleService.createRole(request);

        Role auditor = roleRepository.findByName("AUDITOR").orElseThrow();
        var response = roleService.deleteRole(auditor.getId());

        assertTrue(response.getMessage().contains("deleted successfully"));
    }
}
