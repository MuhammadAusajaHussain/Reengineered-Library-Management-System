package com.lms.api.dto;

import javax.validation.constraints.Pattern;

public class UpdateUserRequest {
    private String fullName;

    @Pattern(regexp = "ADMIN|LIBRARIAN|CLERK|BORROWER", message = "role must be ADMIN|LIBRARIAN|CLERK|BORROWER")
    private String role;

    private Boolean active;

    private String password;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

