package com.bbs.bbsadmin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "unit-test-secret-key-must-be-at-least-32-bytes-long");
        ReflectionTestUtils.setField(jwtUtil, "expireMillis", 60_000L);
        jwtUtil.init();
    }

    @Test
    void generateAndParse_shouldRoundTripClaims() {
        String token = jwtUtil.generate("user-1", Map.of("userName", "tester"));
        Claims claims = jwtUtil.parse(token);
        assertEquals("user-1", claims.getSubject());
        assertEquals("tester", claims.get("userName", String.class));
    }

    @Test
    void parse_tamperedToken_shouldThrow() {
        String token = jwtUtil.generate("user-1", null);
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1) + (last == 'a' ? 'b' : 'a');
        assertThrows(JwtException.class, () -> jwtUtil.parse(tampered));
    }

    @Test
    void parse_expiredToken_shouldThrow() {
        ReflectionTestUtils.setField(jwtUtil, "expireMillis", -1_000L);
        String token = jwtUtil.generate("user-1", null);
        assertThrows(JwtException.class, () -> jwtUtil.parse(token));
    }
}
