package com.yourname.zerotrust.attack;

import com.yourname.zerotrust.entity.User;

public class SimulationResult {
    private final User targetUser;
    private final String sourceIp;
    private final String details;
    private final boolean detected;
    private final String detectionMethod;
    private final String detectionDetails;
    private final String severity;
    private final int finalRisk;

    public SimulationResult(User targetUser, String sourceIp, String details, boolean detected,
            String detectionMethod, String detectionDetails, String severity, int finalRisk) {
        this.targetUser = targetUser;
        this.sourceIp = sourceIp;
        this.details = details;
        this.detected = detected;
        this.detectionMethod = detectionMethod;
        this.detectionDetails = detectionDetails;
        this.severity = severity;
        this.finalRisk = finalRisk;
    }

    public User getTargetUser() { return targetUser; }
    public String getSourceIp() { return sourceIp; }
    public String getDetails() { return details; }
    public boolean isDetected() { return detected; }
    public String getDetectionMethod() { return detectionMethod; }
    public String getDetectionDetails() { return detectionDetails; }
    public String getSeverity() { return severity; }
    public int getFinalRisk() { return finalRisk; }
}
