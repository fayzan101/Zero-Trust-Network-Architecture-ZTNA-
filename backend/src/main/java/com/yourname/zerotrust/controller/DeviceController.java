package com.yourname.zerotrust.controller;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceResponse;
import com.yourname.zerotrust.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/register")
    public ResponseEntity<DeviceResponse> registerDevice(@RequestBody DeviceRegisterRequest request) {
        DeviceResponse resp = deviceService.registerDevice(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceResponse> getDevice(@PathVariable String deviceId) {
        DeviceResponse resp = deviceService.getDeviceByDeviceId(deviceId);
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceResponse>> getDevicesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(deviceService.getDevicesByUserId(userId));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Integer> evaluateTrust(@RequestParam String deviceId) {
        int score = deviceService.evaluateTrustScore(deviceId);
        return ResponseEntity.ok(score);
    }
}
