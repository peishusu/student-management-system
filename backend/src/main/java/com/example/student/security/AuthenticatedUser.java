package com.example.student.security;

public class AuthenticatedUser {
    private final Long id;
    private final String username;
    private final String displayName;
    private final String role;
    private final int tokenVersion;

    public AuthenticatedUser(Long id, String username, String displayName, String role, int tokenVersion) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.tokenVersion = tokenVersion;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }

    public int getTokenVersion() {
        return tokenVersion;
    }
}
