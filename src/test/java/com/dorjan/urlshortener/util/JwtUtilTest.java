package com.dorjan.urlshortener.util;

import com.dorjan.urlshortener.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        JwtProperties properties = new JwtProperties(
                "testSecretKeyForJWTTokenGenerationThatShouldBeLongEnough123456",
                3600000
        );
        jwtUtil = new JwtUtil(properties);
    }

    @Test
    void generateToken_shouldReturnToken() {
        String token = jwtUtil.generateToken("john");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("john");

        String username = jwtUtil.extractUsername(token);

        assertEquals("john", username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("john");

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        JwtProperties expiredProperties = new JwtProperties(
                "testSecretKeyForJWTTokenGenerationThatShouldBeLongEnough123456",
                0
        );

        JwtUtil expiredJwtUtil = new JwtUtil(expiredProperties);

        String token = expiredJwtUtil.generateToken("john");

        assertFalse(expiredJwtUtil.isTokenValid(token));
    }
}
