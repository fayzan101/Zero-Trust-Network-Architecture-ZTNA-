package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleRequest {
    @NotBlank
    @Size(max = 50)
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
