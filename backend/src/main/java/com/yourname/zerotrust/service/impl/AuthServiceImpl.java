package com.yourname.zerotrust.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.LoginRequest;
import com.yourname.zerotrust.dto.LoginResponse;
import com.yourname.zerotrust.dto.LogoutRequest;
import com.yourname.zerotrust.dto.MfaRequest;
import com.yourname.zerotrust.dto.MfaResponse;
import com.yourname.zerotrust.dto.ProfileResponse;
import com.yourname.zerotrust.dto.RefreshRequest;
import com.yourname.zerotrust.dto.RegisterRequest;
import com.yourname.zerotrust.dto.RegisterResponse;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AuthService;
import com.yourname.zerotrust.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            response.setMessage("Username already exists");
            return response;
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            response.setMessage("Email already exists");
            return response;
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMfaEnabled(false);

        // Assign role (default to USER if not specified)
        String roleName = request.getRole() != null ? request.getRole() : "USER";
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        // Save user
        User savedUser = userRepository.save(user);

        // Build response
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRole(roleName);
        response.setMessage("User registered successfully");

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        // Find user by username
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("Invalid username or password");
            return response;
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setMessage("Invalid username or password");
            return response;
        }

        // Check if MFA is enabled
        if (user.isMfaEnabled()) {
            response.setMessage("MFA_REQUIRED");
            return response;
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Build response
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirySeconds());
        response.setMessage("Login successful");

        return response;
    }

    @Override
    public MfaResponse verifyMfa(MfaRequest request) {
        MfaResponse response = new MfaResponse();

        // Find user by username
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("User not found");
            return response;
        }

        // Simple OTP validation (in production, use TOTP library like GoogleAuthenticator)
        // For demo purposes, accept "123456" as valid OTP
        if (!"123456".equals(request.getOtp())) {
            response.setMessage("Invalid OTP");
            return response;
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Build response
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirySeconds());
        response.setMessage("MFA verification successful");

        return response;
    }

    @Override
    public GenericResponse logout(LogoutRequest request) {
        // Find user by refresh token and invalidate it
        User user = userRepository.findByRefreshToken(request.getRefreshToken()).orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
        }

        return new GenericResponse("Logged out successfully");
    }

    @Override
    public LoginResponse refreshToken(RefreshRequest request) {
        LoginResponse response = new LoginResponse();

        // Validate refresh token
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            response.setMessage("Invalid or expired refresh token");
            return response;
        }

        // Find user by refresh token
        User user = userRepository.findByRefreshToken(request.getRefreshToken()).orElse(null);
        if (user == null) {
            response.setMessage("Refresh token not found");
            return response;
        }

        // Generate new access token
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Update refresh token in database
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        // Build response
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirySeconds());
        response.setMessage("Token refreshed successfully");

        return response;
    }

    @Override
    public ProfileResponse getProfile(String authHeader) {
        ProfileResponse response = new ProfileResponse();

        // Extract token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return response;
        }

        String token = authHeader.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return response;
        }

        // Extract username from token
        String username = jwtUtil.extractUsername(token);

        // Find user
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return response;
        }

        // Build response
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            response.setRole(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));
        }
        if (user.getCreatedAt() != null) {
            response.setCreatedAt(user.getCreatedAt().toString());
        }
        if (user.getLastLogin() != null) {
            response.setLastLogin(user.getLastLogin().toString());
        }

        return response;
    }
}
