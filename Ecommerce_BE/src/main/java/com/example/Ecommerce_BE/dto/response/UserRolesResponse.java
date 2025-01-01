package com.example.Ecommerce_BE.dto.response;

import java.util.List;

public class UserRolesResponse {
    private String message;
    private List<String> roles;

    public UserRolesResponse(String message, List<String> roles) {
        this.message = message;
        this.roles = roles;
    }

    // Getters và Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

