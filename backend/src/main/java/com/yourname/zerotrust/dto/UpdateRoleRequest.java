package com.yourname.zerotrust.dto;

import com.yourname.zerotrust.entity.Role;

public class UpdateRoleRequest {
    private Long id;
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}