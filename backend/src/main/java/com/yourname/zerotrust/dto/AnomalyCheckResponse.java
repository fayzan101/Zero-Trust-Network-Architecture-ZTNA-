package com.yourname.zerotrust.dto;

import java.util.List;

public class AnomalyCheckResponse {
    private String sessionId;
    private boolean anomalyDetected;
    private List<String> anomalies;
    private int finalRisk;
    private String message;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public boolean isAnomalyDetected() { return anomalyDetected; }
    public void setAnomalyDetected(boolean anomalyDetected) { this.anomalyDetected = anomalyDetected; }
    public List<String> getAnomalies() { return anomalies; }
    public void setAnomalies(List<String> anomalies) { this.anomalies = anomalies; }
    public int getFinalRisk() { return finalRisk; }
    public void setFinalRisk(int finalRisk) { this.finalRisk = finalRisk; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
