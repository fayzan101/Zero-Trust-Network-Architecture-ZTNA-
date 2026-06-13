package com.yourname.zerotrust.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.attack.CredentialTheftSimulator;
import com.yourname.zerotrust.attack.LateralMovementSimulator;
import com.yourname.zerotrust.attack.PrivilegeEscalationSimulator;
import com.yourname.zerotrust.attack.SimulationResult;
import com.yourname.zerotrust.dto.AttackReportResponse;
import com.yourname.zerotrust.dto.AttackSimulationResponse;
import com.yourname.zerotrust.dto.CredentialTheftRequest;
import com.yourname.zerotrust.dto.LateralMovementRequest;
import com.yourname.zerotrust.dto.PrivilegeEscalationRequest;
import com.yourname.zerotrust.entity.Attack;
import com.yourname.zerotrust.repository.AttackRepository;
import com.yourname.zerotrust.service.AttackService;
import com.yourname.zerotrust.service.AuditLogService;

@Service
public class AttackServiceImpl implements AttackService {

    @Autowired
    private CredentialTheftSimulator credentialTheftSimulator;

    @Autowired
    private PrivilegeEscalationSimulator privilegeEscalationSimulator;

    @Autowired
    private LateralMovementSimulator lateralMovementSimulator;

    @Autowired
    private AttackRepository attackRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public AttackSimulationResponse simulateCredentialTheft(CredentialTheftRequest request) {
        SimulationResult result = credentialTheftSimulator.simulate(request);
        return saveAndRespond("CREDENTIAL_THEFT", result);
    }

    @Override
    public AttackSimulationResponse simulatePrivilegeEscalation(PrivilegeEscalationRequest request) {
        SimulationResult result = privilegeEscalationSimulator.simulate(request);
        return saveAndRespond("PRIVILEGE_ESCALATION", result);
    }

    @Override
    public AttackSimulationResponse simulateLateralMovement(LateralMovementRequest request) {
        SimulationResult result = lateralMovementSimulator.simulate(request);
        return saveAndRespond("LATERAL_MOVEMENT", result);
    }

    @Override
    public AttackReportResponse getReport() {
        List<Attack> attacks = attackRepository.findAllByOrderBySimulatedAtDesc();
        long detected = attackRepository.countByDetectedTrue();
        long undetected = attackRepository.countByDetectedFalse();
        long total = attacks.size();

        AttackReportResponse report = new AttackReportResponse();
        report.setTotalAttacks(total);
        report.setDetectedCount(detected);
        report.setUndetectedCount(undetected);
        report.setDetectionRate(total > 0 ? (double) detected / total * 100.0 : 0.0);
        report.setAttacks(attacks.stream().map(this::toResponse).collect(Collectors.toList()));
        return report;
    }

    private AttackSimulationResponse saveAndRespond(String attackType, SimulationResult result) {
        Attack attack = new Attack();
        attack.setAttackType(attackType);
        attack.setTargetUserId(result.getTargetUser().getId());
        attack.setTargetUsername(result.getTargetUser().getUsername());
        attack.setSourceIp(result.getSourceIp());
        attack.setDetails(result.getDetails());
        attack.setDetected(result.isDetected());
        attack.setDetectionMethod(result.getDetectionMethod());
        attack.setDetectionDetails(result.getDetectionDetails());
        attack.setSeverity(result.getSeverity());
        attack = attackRepository.save(attack);

        auditLogService.logCritical("ATTACK_SIMULATED", result.getTargetUser().getId(),
                result.getTargetUser().getUsername(), result.getSourceIp(),
                attackType + " detected=" + result.isDetected() + ": " + result.getDetectionDetails());

        AttackSimulationResponse response = toResponse(attack);
        response.setFinalRisk(result.getFinalRisk());
        response.setMessage(result.isDetected()
                ? "Attack detected and blocked by zero-trust controls"
                : "Attack not detected — review policies and monitoring");
        return response;
    }

    private AttackSimulationResponse toResponse(Attack attack) {
        AttackSimulationResponse response = new AttackSimulationResponse();
        response.setId(attack.getId());
        response.setAttackType(attack.getAttackType());
        response.setDetected(attack.isDetected());
        response.setDetectionMethod(attack.getDetectionMethod());
        response.setDetectionDetails(attack.getDetectionDetails());
        response.setSeverity(attack.getSeverity());
        if (attack.getSimulatedAt() != null) {
            response.setSimulatedAt(attack.getSimulatedAt().toString());
        }
        return response;
    }
}
