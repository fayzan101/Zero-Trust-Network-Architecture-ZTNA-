package com.yourname.zerotrust.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleRequest {
    @NotNull
    private Long id;

    @Valid
    @NotNull
    private RoleRequest role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RoleRequest getRole() { return role; }
    public void setRole(RoleRequest role) { this.role = role; }
}
