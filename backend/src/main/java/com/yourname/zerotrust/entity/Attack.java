package com.yourname.zerotrust.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "attacks")
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attackType;

    private Long targetUserId;

    private String targetUsername;

    private String sourceIp;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private boolean detected;

    private String detectionMethod;

    @Column(length = 1000)
    private String detectionDetails;

    @Column(nullable = false)
    private String severity;

    private LocalDateTime simulatedAt;

    @PrePersist
    protected void onCreate() {
        simulatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAttackType() { return attackType; }
    public void setAttackType(String attackType) { this.attackType = attackType; }
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }
    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public boolean isDetected() { return detected; }
    public void setDetected(boolean detected) { this.detected = detected; }
    public String getDetectionMethod() { return detectionMethod; }
    public void setDetectionMethod(String detectionMethod) { this.detectionMethod = detectionMethod; }
    public String getDetectionDetails() { return detectionDetails; }
    public void setDetectionDetails(String detectionDetails) { this.detectionDetails = detectionDetails; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public LocalDateTime getSimulatedAt() { return simulatedAt; }
    public void setSimulatedAt(LocalDateTime simulatedAt) { this.simulatedAt = simulatedAt; }
}
