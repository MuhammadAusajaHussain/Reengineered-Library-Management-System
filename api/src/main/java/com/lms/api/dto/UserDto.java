package com.lms.api.dto;

public class UserDto {
    private final int id;
    private final String username;
    private final String fullName;
    private final String role;
    private final boolean active;

    public UserDto(int id, String username, String fullName, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
}

