package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotNull;

public class PrivilegeEscalationRequest {
    @NotNull
    private Long userId;
    private String targetRole;
    private String resource;
    private String action;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
