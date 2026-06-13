package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;

public class PolicyRequest {
    @NotBlank
    private String name;
    private String description;
    private String resource;
    private String action;
    private String requiredRole;
    private Integer minDeviceTrust;
    private Integer maxRiskThreshold;
    private Boolean enabled;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getRequiredRole() { return requiredRole; }
    public void setRequiredRole(String requiredRole) { this.requiredRole = requiredRole; }
    public Integer getMinDeviceTrust() { return minDeviceTrust; }
    public void setMinDeviceTrust(Integer minDeviceTrust) { this.minDeviceTrust = minDeviceTrust; }
    public Integer getMaxRiskThreshold() { return maxRiskThreshold; }
    public void setMaxRiskThreshold(Integer maxRiskThreshold) { this.maxRiskThreshold = maxRiskThreshold; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
