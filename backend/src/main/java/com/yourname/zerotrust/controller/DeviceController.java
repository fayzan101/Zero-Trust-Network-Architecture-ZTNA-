package com.yourname.zerotrust.controller;

import com.yourname.zerotrust.dto.DeviceRegisterRequest;
import com.yourname.zerotrust.dto.DeviceResponse;
import com.yourname.zerotrust.dto.DeviceTrustScoreResponse;
import com.yourname.zerotrust.dto.DeviceUpdateRequest;
import com.yourname.zerotrust.service.DeviceService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Devices", description = "Device registration, trust scoring, and management")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register device", description = "Registers a device for a user. Requires ownerId (FK to User.id). Admin only.")
    public ResponseEntity<DeviceResponse> registerDevice(@Valid @RequestBody DeviceRegisterRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(request));
    }

    @GetMapping("/{deviceId}")
    @Operation(summary = "Get device", description = "Returns device details by deviceId string.")
    public ResponseEntity<DeviceResponse> getDevice(
            @Parameter(description = "Unique device identifier, e.g. demo-laptop-01") @PathVariable String deviceId) {
        DeviceResponse resp = deviceService.getDeviceByDeviceId(deviceId);
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{deviceId}/trust-score")
    @Operation(summary = "Get trust score", description = "Returns the current trust score (0–100) for a device.")
    public ResponseEntity<DeviceTrustScoreResponse> getTrustScore(
            @Parameter(description = "Unique device identifier") @PathVariable String deviceId) {
        return ResponseEntity.ok(deviceService.getTrustScore(deviceId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List devices by user", description = "Returns all devices owned by the given user ID.")
    public ResponseEntity<List<DeviceResponse>> getDevicesByUser(
            @Parameter(description = "Owner user ID") @PathVariable String userId) {
        return ResponseEntity.ok(deviceService.getDevicesByUserId(userId));
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Re-evaluate trust score", description = "Recalculates and persists the trust score for a device.")
    public ResponseEntity<Integer> evaluateTrust(
            @Parameter(description = "Device ID to evaluate", required = true) @RequestParam String deviceId) {
        return ResponseEntity.ok(deviceService.evaluateTrustScore(deviceId));
    }

    @PutMapping("/{deviceId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update device", description = "Updates device type, OS, or IP and recalculates trust score. Admin only.")
    public ResponseEntity<DeviceResponse> updateDevice(
            @Parameter(description = "Unique device identifier") @PathVariable String deviceId,
            @Valid @RequestBody DeviceUpdateRequest request) {
        return ResponseEntity.ok(deviceService.updateDevice(deviceId, request));
    }
}
