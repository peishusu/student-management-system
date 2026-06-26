package com.example.student.security;

import com.example.student.common.ApiResponse;
import com.example.student.entity.SystemUser;
import com.example.student.exception.UnauthorizedException;
import com.example.student.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(TokenService tokenService, UserMapper userMapper, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, "请先登录");
            return false;
        }
        try {
            AuthenticatedUser tokenUser = tokenService.parse(authorization.substring(7).trim());
            SystemUser user = userMapper.selectById(tokenUser.getId());
            if (user == null || !user.getUsername().equals(tokenUser.getUsername())) {
                writeUnauthorized(response, "登录状态无效，请重新登录");
                return false;
            }
            int tokenVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
            if (tokenUser.getTokenVersion() != tokenVersion) {
                writeUnauthorized(response, "登录状态已失效，请重新登录");
                return false;
            }
            AuthContext.set(new AuthenticatedUser(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole(), tokenVersion));
            return true;
        } catch (UnauthorizedException exception) {
            writeUnauthorized(response, exception.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(401, message));
    }
}
