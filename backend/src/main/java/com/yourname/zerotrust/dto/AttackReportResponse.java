package com.yourname.zerotrust.dto;

import java.util.List;

public class AttackReportResponse {
    private long totalAttacks;
    private long detectedCount;
    private long undetectedCount;
    private double detectionRate;
    private List<AttackSimulationResponse> attacks;

    public long getTotalAttacks() { return totalAttacks; }
    public void setTotalAttacks(long totalAttacks) { this.totalAttacks = totalAttacks; }
    public long getDetectedCount() { return detectedCount; }
    public void setDetectedCount(long detectedCount) { this.detectedCount = detectedCount; }
    public long getUndetectedCount() { return undetectedCount; }
    public void setUndetectedCount(long undetectedCount) { this.undetectedCount = undetectedCount; }
    public double getDetectionRate() { return detectionRate; }
    public void setDetectionRate(double detectionRate) { this.detectionRate = detectionRate; }
    public List<AttackSimulationResponse> getAttacks() { return attacks; }
    public void setAttacks(List<AttackSimulationResponse> attacks) { this.attacks = attacks; }
}
