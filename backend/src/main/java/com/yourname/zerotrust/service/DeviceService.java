package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceResponse;
import com.yourname.zerotrust.dto.DeviceTrustScoreResponse;
import com.yourname.zerotrust.dto.DeviceUpdateRequest;

import java.util.List;

public interface DeviceService {
    DeviceResponse registerDevice(DeviceRegisterRequest request);
    DeviceResponse getDeviceByDeviceId(String deviceId);
    List<DeviceResponse> getDevicesByUserId(String userId);
    int evaluateTrustScore(String deviceId);
    DeviceTrustScoreResponse getTrustScore(String deviceId);
    DeviceResponse updateDevice(String deviceId, DeviceUpdateRequest request);
}
