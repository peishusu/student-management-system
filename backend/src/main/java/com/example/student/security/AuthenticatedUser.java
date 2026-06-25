package com.example.student.security;

public class AuthenticatedUser {
    private final Long id;
    private final String username;
    private final String displayName;

    public AuthenticatedUser(Long id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
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
}
