package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.AttackReportResponse;
import com.yourname.zerotrust.dto.AttackSimulationResponse;
import com.yourname.zerotrust.dto.CredentialTheftRequest;
import com.yourname.zerotrust.dto.LateralMovementRequest;
import com.yourname.zerotrust.dto.PrivilegeEscalationRequest;

public interface AttackService {
    AttackSimulationResponse simulateCredentialTheft(CredentialTheftRequest request);
    AttackSimulationResponse simulatePrivilegeEscalation(PrivilegeEscalationRequest request);
    AttackSimulationResponse simulateLateralMovement(LateralMovementRequest request);
    AttackReportResponse getReport();
}
