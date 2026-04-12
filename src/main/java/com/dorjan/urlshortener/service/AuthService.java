package com.dorjan.urlshortener.service;

import com.dorjan.urlshortener.dto.AuthResponse;
import com.dorjan.urlshortener.dto.LoginRequest;
import com.dorjan.urlshortener.dto.RegisterRequest;
import com.dorjan.urlshortener.exception.BusinessException;
import com.dorjan.urlshortener.model.User;
import com.dorjan.urlshortener.repository.UserRepository;
import com.dorjan.urlshortener.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.userAlreadyExists();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(BusinessException::invalidCredentials);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.invalidCredentials();
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
