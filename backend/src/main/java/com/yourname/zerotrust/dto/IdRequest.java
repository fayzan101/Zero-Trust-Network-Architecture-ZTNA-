package com.yourname.zerotrust.dto;

import jakarta.validation.constraints.NotNull;

public class IdRequest {
    @NotNull
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
