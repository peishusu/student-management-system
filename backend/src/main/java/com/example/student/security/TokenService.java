package com.example.student.security;

import com.example.student.entity.SystemUser;
import com.example.student.exception.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final ObjectMapper objectMapper;

    @Value("${auth.token.secret}")
    private String secret;

    @Value("${auth.token.expire-minutes:120}")
    private long expireMinutes;

    public TokenService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public IssuedToken issue(SystemUser user) {
        long now = System.currentTimeMillis();
        long expiresAt = now + expireMinutes * 60L * 1000L;
        Map<String, Object> header = new LinkedHashMap<String, Object>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("sub", user.getId());
        payload.put("username", user.getUsername());
        payload.put("displayName", user.getDisplayName());
        payload.put("iat", now);
        payload.put("exp", expiresAt);

        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String signingInput = encodedHeader + "." + encodedPayload;
        String signature = encodeUrl(sign(signingInput));
        return new IssuedToken(signingInput + "." + signature, expiresAt);
    }

    public AuthenticatedUser parse(String token) {
        String[] parts = token == null ? new String[0] : token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }
        String signingInput = parts[0] + "." + parts[1];
        byte[] expectedSignature = sign(signingInput);
        byte[] actualSignature;
        try {
            actualSignature = decodeUrl(parts[2]);
        } catch (RuntimeException exception) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }
        if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }

        Map<String, Object> payload = decodeJson(parts[1]);
        long expiresAt = number(payload.get("exp"));
        if (expiresAt < System.currentTimeMillis()) {
            throw new UnauthorizedException("登录已过期，请重新登录");
        }
        return new AuthenticatedUser(
                number(payload.get("sub")),
                string(payload.get("username")),
                string(payload.get("displayName"))
        );
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return encodeUrl(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Token 生成失败", exception);
        }
    }

    private Map<String, Object> decodeJson(String value) {
        try {
            return objectMapper.readValue(decodeUrl(value), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception exception) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }
    }

    private byte[] sign(String value) {
        validateSecret();
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Token 签名失败", exception);
        }
    }

    private void validateSecret() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("auth.token.secret 长度至少需要 32 个字符");
        }
    }

    private String encodeUrl(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] decodeUrl(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private String string(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private long number(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    public static class IssuedToken {
        private final String token;
        private final long expiresAt;

        public IssuedToken(String token, long expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        public String getToken() {
            return token;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }
}
