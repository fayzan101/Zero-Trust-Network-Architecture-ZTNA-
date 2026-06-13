package com.yourname.zerotrust.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceResponse;
import com.yourname.zerotrust.dto.DeviceTrustScoreResponse;
import com.yourname.zerotrust.dto.DeviceUpdateRequest;
import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.exception.ResourceNotFoundException;
import com.yourname.zerotrust.repository.DeviceRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.DeviceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DeviceResponse registerDevice(DeviceRegisterRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Device device = new Device();
        device.setDeviceId(request.getDeviceId());
        device.setOwner(owner);
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
        Long ownerId = Long.parseLong(userId);
        return deviceRepository.findByOwner_Id(ownerId)
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

    @Override
    public DeviceTrustScoreResponse getTrustScore(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            throw new ResourceNotFoundException("Device not found: " + deviceId);
        }
        return new DeviceTrustScoreResponse(device.getDeviceId(), device.getTrustScore());
    }

    @Override
    public DeviceResponse updateDevice(String deviceId, DeviceUpdateRequest request) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            throw new ResourceNotFoundException("Device not found: " + deviceId);
        }

        if (request.getDeviceType() != null) {
            device.setDeviceType(request.getDeviceType());
        }
        if (request.getOs() != null) {
            device.setOs(request.getOs());
        }
        device.setIpAddress(request.getIpAddress());
        device.setTrustScore(evaluateTrustScoreInternal(device));
        device = deviceRepository.save(device);
        return toResponse(device);
    }

    private int evaluateTrustScoreInternal(Device device) {
        int score = 50;
        if (device.getDeviceType().equalsIgnoreCase("laptop")) score += 20;
        if (device.getOs().toLowerCase().contains("windows")) score += 10;
        if (device.getOs().toLowerCase().contains("linux")) score += 15;
        return Math.min(score, 100);
    }

    private DeviceResponse toResponse(Device device) {
        DeviceResponse resp = new DeviceResponse();
        resp.setId(device.getId());
        resp.setDeviceId(device.getDeviceId());
        resp.setUserId(device.getOwner().getId().toString());
        resp.setDeviceType(device.getDeviceType());
        resp.setOs(device.getOs());
        resp.setIpAddress(device.getIpAddress());
        resp.setTrustScore(device.getTrustScore());
        resp.setRegisteredAt(device.getRegisteredAt().toString());
        return resp;
    }
}
