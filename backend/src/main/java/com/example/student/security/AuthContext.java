package com.example.student.security;

public final class AuthContext {
    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<AuthenticatedUser>();

    private AuthContext() {
    }

    public static void set(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static AuthenticatedUser get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
