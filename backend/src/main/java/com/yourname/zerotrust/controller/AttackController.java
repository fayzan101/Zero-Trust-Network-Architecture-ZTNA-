package com.yourname.zerotrust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.AttackReportResponse;
import com.yourname.zerotrust.dto.AttackSimulationResponse;
import com.yourname.zerotrust.dto.CredentialTheftRequest;
import com.yourname.zerotrust.dto.LateralMovementRequest;
import com.yourname.zerotrust.dto.PrivilegeEscalationRequest;
import com.yourname.zerotrust.service.AttackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attack")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Attack Simulation", description = "Simulate attacks and measure zero-trust detection (admin only)")
public class AttackController {

    @Autowired
    private AttackService attackService;

    @PostMapping("/credential-theft")
    @Operation(summary = "Simulate credential theft",
            description = "Simulates login from a stolen password and external IP. Tests risk/policy detection.")
    public ResponseEntity<AttackSimulationResponse> credentialTheft(
            @Valid @RequestBody CredentialTheftRequest request) {
        return ResponseEntity.ok(attackService.simulateCredentialTheft(request));
    }

    @PostMapping("/privilege-escalation")
    @Operation(summary = "Simulate privilege escalation",
            description = "Simulates a user attempting to gain a higher role (e.g. ADMIN).")
    public ResponseEntity<AttackSimulationResponse> privilegeEscalation(
            @Valid @RequestBody PrivilegeEscalationRequest request) {
        return ResponseEntity.ok(attackService.simulatePrivilegeEscalation(request));
    }

    @PostMapping("/lateral-movement")
    @Operation(summary = "Simulate lateral movement",
            description = "Simulates a user accessing a restricted resource from an unauthorized context.")
    public ResponseEntity<AttackSimulationResponse> lateralMovement(
            @Valid @RequestBody LateralMovementRequest request) {
        return ResponseEntity.ok(attackService.simulateLateralMovement(request));
    }

    @GetMapping("/report")
    @Operation(summary = "Attack detection report",
            description = "Returns total attacks simulated, detection rate, and per-attack details.")
    public ResponseEntity<AttackReportResponse> report() {
        return ResponseEntity.ok(attackService.getReport());
    }
}
