package com.example.student.service;

import com.example.student.dto.AuthResponse;
import com.example.student.dto.AuthUser;
import com.example.student.dto.ChangePasswordRequest;
import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthUser currentUser();

    void changePassword(ChangePasswordRequest request);

    void logout();
}
