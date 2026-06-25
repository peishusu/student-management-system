package com.example.student.service.impl;

import com.example.student.dto.AuthResponse;
import com.example.student.dto.AuthUser;
import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;
import com.example.student.entity.SystemUser;
import com.example.student.exception.BusinessException;
import com.example.student.exception.UnauthorizedException;
import com.example.student.mapper.UserMapper;
import com.example.student.security.AuthContext;
import com.example.student.security.AuthenticatedUser;
import com.example.student.security.PasswordService;
import com.example.student.security.TokenService;
import com.example.student.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthServiceImpl(UserMapper userMapper, PasswordService passwordService, TokenService tokenService) {
        this.userMapper = userMapper;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.getUsername());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (userMapper.selectByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }

        SystemUser user = new SystemUser();
        user.setUsername(username);
        user.setDisplayName(request.getDisplayName().trim());
        user.setPasswordHash(passwordService.hash(request.getPassword()));
        userMapper.insert(user);
        return authResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        SystemUser user = userMapper.selectByUsername(normalizeUsername(request.getUsername()));
        if (user == null || !passwordService.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("用户名或密码错误");
        }
        return authResponse(user);
    }

    @Override
    public AuthUser currentUser() {
        AuthenticatedUser user = AuthContext.get();
        if (user == null) {
            throw new UnauthorizedException("请先登录");
        }
        return new AuthUser(user.getId(), user.getUsername(), user.getDisplayName());
    }

    private AuthResponse authResponse(SystemUser user) {
        TokenService.IssuedToken issuedToken = tokenService.issue(user);
        return new AuthResponse(
                issuedToken.getToken(),
                issuedToken.getExpiresAt(),
                new AuthUser(user.getId(), user.getUsername(), user.getDisplayName())
        );
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
