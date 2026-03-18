package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceResponse;
import java.util.List;

public interface DeviceService {
    DeviceResponse registerDevice(DeviceRegisterRequest request);
    DeviceResponse getDeviceByDeviceId(String deviceId);
    List<DeviceResponse> getDevicesByUserId(String userId);
    int evaluateTrustScore(String deviceId);
}
