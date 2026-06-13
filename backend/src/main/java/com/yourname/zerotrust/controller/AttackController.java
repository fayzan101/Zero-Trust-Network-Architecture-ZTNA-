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

@RestController
@RequestMapping("/api/attack")
@PreAuthorize("hasRole('ADMIN')")
public class AttackController {

    @Autowired
    private AttackService attackService;

    @PostMapping("/credential-theft")
    public ResponseEntity<AttackSimulationResponse> credentialTheft(
            @RequestBody CredentialTheftRequest request) {
        return ResponseEntity.ok(attackService.simulateCredentialTheft(request));
    }

    @PostMapping("/privilege-escalation")
    public ResponseEntity<AttackSimulationResponse> privilegeEscalation(
            @RequestBody PrivilegeEscalationRequest request) {
        return ResponseEntity.ok(attackService.simulatePrivilegeEscalation(request));
    }

    @PostMapping("/lateral-movement")
    public ResponseEntity<AttackSimulationResponse> lateralMovement(
            @RequestBody LateralMovementRequest request) {
        return ResponseEntity.ok(attackService.simulateLateralMovement(request));
    }

    @GetMapping("/report")
    public ResponseEntity<AttackReportResponse> report() {
        return ResponseEntity.ok(attackService.getReport());
    }
}
