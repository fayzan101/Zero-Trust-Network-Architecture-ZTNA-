package com.yourname.zerotrust.dto;

public class GenericResponse {
    private String message;

    public GenericResponse() {}

    public GenericResponse(String message) {
        this.message = message;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
