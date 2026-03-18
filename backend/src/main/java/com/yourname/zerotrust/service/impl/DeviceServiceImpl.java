package com.yourname.zerotrust.service.impl;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceResponse;
import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.repository.DeviceRepository;
import com.yourname.zerotrust.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public DeviceResponse registerDevice(DeviceRegisterRequest request) {
        Device device = new Device();
        device.setDeviceId(request.getDeviceId());
        device.setUserId(request.getUserId());
        device.setDeviceType(request.getDeviceType());
        device.setOs(request.getOs());
        device.setIpAddress(request.getIpAddress());
        device.setRegisteredAt(LocalDateTime.now());
        device.setTrustScore(evaluateTrustScoreInternal(device));
        device = deviceRepository.save(device);
        return toResponse(device);
    }

    @Override
    public DeviceResponse getDeviceByDeviceId(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        return device != null ? toResponse(device) : null;
    }

    @Override
    public List<DeviceResponse> getDevicesByUserId(String userId) {
        return deviceRepository.findByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public int evaluateTrustScore(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) return 0;
        int score = evaluateTrustScoreInternal(device);
        device.setTrustScore(score);
        deviceRepository.save(device);
        return score;
    }

    private int evaluateTrustScoreInternal(Device device) {
        // Simple example: score based on device type and OS
        int score = 50;
        if (device.getDeviceType().equalsIgnoreCase("laptop")) score += 20;
        if (device.getOs().toLowerCase().contains("windows")) score += 10;
        if (device.getOs().toLowerCase().contains("linux")) score += 15;
        // Add more logic as needed
        return Math.min(score, 100);
    }

    private DeviceResponse toResponse(Device device) {
        DeviceResponse resp = new DeviceResponse();
        resp.setId(device.getId());
        resp.setDeviceId(device.getDeviceId());
        resp.setUserId(device.getUserId());
        resp.setDeviceType(device.getDeviceType());
        resp.setOs(device.getOs());
        resp.setIpAddress(device.getIpAddress());
        resp.setTrustScore(device.getTrustScore());
        resp.setRegisteredAt(device.getRegisteredAt().toString());
        return resp;
    }
}
