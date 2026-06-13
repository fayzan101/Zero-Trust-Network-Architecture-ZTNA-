package com.yourname.zerotrust.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceUpdateRequest;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.repository.DeviceRepository;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.DeviceService;
import com.yourname.zerotrust.support.TestDataFactory;

@SpringBootTest
@ActiveProfiles("test")
class DeviceServiceTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        ownerId = TestDataFactory.createUser(userRepository, roleRepository, "testuser", "USER").getId();
    }

    @Test
    void getTrustScore_returnsScoreForRegisteredDevice() {
        DeviceRegisterRequest request = new DeviceRegisterRequest();
        request.setDeviceId("laptop-01");
        request.setOwnerId(ownerId);
        request.setDeviceType("laptop");
        request.setOs("linux");
        request.setIpAddress("192.168.1.10");
        deviceService.registerDevice(request);

        var response = deviceService.getTrustScore("laptop-01");

        assertEquals("laptop-01", response.getDeviceId());
        assertEquals(85, response.getTrustScore());
    }

    @Test
    void updateDevice_recalculatesTrustScore() {
        DeviceRegisterRequest request = new DeviceRegisterRequest();
        request.setDeviceId("phone-01");
        request.setOwnerId(ownerId);
        request.setDeviceType("phone");
        request.setOs("android");
        request.setIpAddress("10.0.0.5");
        deviceService.registerDevice(request);

        DeviceUpdateRequest update = new DeviceUpdateRequest();
        update.setDeviceType("laptop");
        update.setOs("linux");
        update.setIpAddress("10.0.0.9");

        var updated = deviceService.updateDevice("phone-01", update);

        assertEquals("laptop", updated.getDeviceType());
        assertEquals(85, updated.getTrustScore());
    }

    @Test
    void getTrustScore_throwsWhenDeviceMissing() {
        assertThrows(ResourceNotFoundException.class,
                () -> deviceService.getTrustScore("missing-device"));
    }
}
