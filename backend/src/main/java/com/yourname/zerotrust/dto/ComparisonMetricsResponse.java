package com.yourname.zerotrust.dto;

public class ComparisonMetricsResponse {
    private ModelMetrics traditional;
    private ModelMetrics zeroTrust;

    public ModelMetrics getTraditional() { return traditional; }
    public void setTraditional(ModelMetrics traditional) { this.traditional = traditional; }
    public ModelMetrics getZeroTrust() { return zeroTrust; }
    public void setZeroTrust(ModelMetrics zeroTrust) { this.zeroTrust = zeroTrust; }

    public static class ModelMetrics {
        private String accessControl;
        private String deviceVerification;
        private String riskScoring;
        private String continuousMonitoring;
        private long policiesEnforced;
        private long activeSessions;
        private double averageRiskScore;
        private long attacksSimulated;
        private long attacksDetected;
        private double detectionRate;

        public String getAccessControl() { return accessControl; }
        public void setAccessControl(String accessControl) { this.accessControl = accessControl; }
        public String getDeviceVerification() { return deviceVerification; }
        public void setDeviceVerification(String deviceVerification) { this.deviceVerification = deviceVerification; }
        public String getRiskScoring() { return riskScoring; }
        public void setRiskScoring(String riskScoring) { this.riskScoring = riskScoring; }
        public String getContinuousMonitoring() { return continuousMonitoring; }
        public void setContinuousMonitoring(String continuousMonitoring) { this.continuousMonitoring = continuousMonitoring; }
        public long getPoliciesEnforced() { return policiesEnforced; }
        public void setPoliciesEnforced(long policiesEnforced) { this.policiesEnforced = policiesEnforced; }
        public long getActiveSessions() { return activeSessions; }
        public void setActiveSessions(long activeSessions) { this.activeSessions = activeSessions; }
        public double getAverageRiskScore() { return averageRiskScore; }
        public void setAverageRiskScore(double averageRiskScore) { this.averageRiskScore = averageRiskScore; }
        public long getAttacksSimulated() { return attacksSimulated; }
        public void setAttacksSimulated(long attacksSimulated) { this.attacksSimulated = attacksSimulated; }
        public long getAttacksDetected() { return attacksDetected; }
        public void setAttacksDetected(long attacksDetected) { this.attacksDetected = attacksDetected; }
        public double getDetectionRate() { return detectionRate; }
        public void setDetectionRate(double detectionRate) { this.detectionRate = detectionRate; }
    }
}
