package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotNull;

public class LateralMovementRequest {
    @NotNull
    private Long userId;
    private String sourceSessionId;
    private String targetResource;
    private String targetIp;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSourceSessionId() { return sourceSessionId; }
    public void setSourceSessionId(String sourceSessionId) { this.sourceSessionId = sourceSessionId; }
    public String getTargetResource() { return targetResource; }
    public void setTargetResource(String targetResource) { this.targetResource = targetResource; }
    public String getTargetIp() { return targetIp; }
    public void setTargetIp(String targetIp) { this.targetIp = targetIp; }
}
