package com.yourname.zerotrust.dto;

public class AttackSimulationResponse {
    private Long id;
    private String attackType;
    private boolean detected;
    private String detectionMethod;
    private String detectionDetails;
    private String severity;
    private String message;
    private int finalRisk;
    private String simulatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAttackType() { return attackType; }
    public void setAttackType(String attackType) { this.attackType = attackType; }
    public boolean isDetected() { return detected; }
    public void setDetected(boolean detected) { this.detected = detected; }
    public String getDetectionMethod() { return detectionMethod; }
    public void setDetectionMethod(String detectionMethod) { this.detectionMethod = detectionMethod; }
    public String getDetectionDetails() { return detectionDetails; }
    public void setDetectionDetails(String detectionDetails) { this.detectionDetails = detectionDetails; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getFinalRisk() { return finalRisk; }
    public void setFinalRisk(int finalRisk) { this.finalRisk = finalRisk; }
    public String getSimulatedAt() { return simulatedAt; }
    public void setSimulatedAt(String simulatedAt) { this.simulatedAt = simulatedAt; }
}
