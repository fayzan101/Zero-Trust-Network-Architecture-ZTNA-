package com.yourname.zerotrust.dto;

public class PolicyResponse {
    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
    private String requiredRole;
    private Integer minDeviceTrust;
    private Integer maxRiskThreshold;
    private boolean enabled;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
