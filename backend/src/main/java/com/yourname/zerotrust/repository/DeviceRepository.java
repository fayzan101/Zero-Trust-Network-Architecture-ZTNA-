package com.yourname.zerotrust.repository;

import com.yourname.zerotrust.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(String userId);
    Device findByDeviceId(String deviceId);
}
