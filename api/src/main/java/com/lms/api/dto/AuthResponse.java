package com.lms.api.dto;

public class AuthResponse {
    private String token;
    private int userId;
    private String username;
    private String fullName;
    private String role;

    public AuthResponse(String token, int userId, String username, String fullName, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
