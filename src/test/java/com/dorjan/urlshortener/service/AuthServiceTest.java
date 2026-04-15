package com.dorjan.urlshortener.service;

import com.dorjan.urlshortener.dto.AuthResponse;
import com.dorjan.urlshortener.dto.LoginRequest;
import com.dorjan.urlshortener.dto.RegisterRequest;
import com.dorjan.urlshortener.exception.BusinessException;
import com.dorjan.urlshortener.model.User;
import com.dorjan.urlshortener.repository.UserRepository;
import com.dorjan.urlshortener.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("test123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("test123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtUtil.generateToken("john")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("test123");
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("test123");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("test123");

        User user = new User();
        user.setUsername("john");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("john")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("test123");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setUsername("john");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(request));
    }
}
