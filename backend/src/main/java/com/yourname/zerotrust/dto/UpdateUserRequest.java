package com.yourname.zerotrust.dto;

import com.yourname.zerotrust.entity.User;

public class UpdateUserRequest {
    private Long id;
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
