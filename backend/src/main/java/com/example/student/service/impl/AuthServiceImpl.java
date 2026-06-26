package com.example.student.service.impl;

import com.example.student.dto.AuthResponse;
import com.example.student.dto.AuthUser;
import com.example.student.dto.ChangePasswordRequest;
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
        user.setRole(normalizeRole(request.getRole()));
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
        return new AuthUser(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        AuthenticatedUser current = requireCurrentUser();
        SystemUser user = userMapper.selectById(current.getId());
        if (user == null) {
            throw new UnauthorizedException("请先登录");
        }
        if (!passwordService.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("当前密码错误");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        if (passwordService.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException("新密码不能与当前密码相同");
        }
        userMapper.updatePasswordAndIncreaseTokenVersion(user.getId(), passwordService.hash(request.getNewPassword()));
    }

    @Override
    @Transactional
    public void logout() {
        AuthenticatedUser current = requireCurrentUser();
        userMapper.increaseTokenVersion(current.getId());
    }

    private AuthResponse authResponse(SystemUser user) {
        TokenService.IssuedToken issuedToken = tokenService.issue(user);
        return new AuthResponse(
                issuedToken.getToken(),
                issuedToken.getExpiresAt(),
                new AuthUser(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole())
        );
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "VIEWER";
        }
        return role.trim().toUpperCase(Locale.ROOT);
    }

    private AuthenticatedUser requireCurrentUser() {
        AuthenticatedUser user = AuthContext.get();
        if (user == null) {
            throw new UnauthorizedException("请先登录");
        }
        return user;
    }
}
