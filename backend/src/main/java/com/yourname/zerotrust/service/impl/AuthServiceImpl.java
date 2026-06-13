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
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.ProfileResponse;
import com.yourname.zerotrust.dto.RefreshRequest;
import com.yourname.zerotrust.dto.RegisterRequest;
import com.yourname.zerotrust.dto.RegisterResponse;
import com.yourname.zerotrust.dto.RiskCalculateRequest;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.dto.SessionResponse;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AuthService;
import com.yourname.zerotrust.service.RiskService;
import com.yourname.zerotrust.service.SessionService;
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

    @Autowired
    private RiskService riskService;

    @Autowired
    private PolicyEvaluator policyEvaluator;

    @Autowired
    private SessionService sessionService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        if (userRepository.existsByUsername(request.getUsername())) {
            response.setMessage("Username already exists");
            return response;
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            response.setMessage("Email already exists");
            return response;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMfaEnabled(false);

        String roleName = request.getRole() != null ? request.getRole() : "USER";
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

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

        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("Invalid username or password");
            response.setAccessAllowed(false);
            return response;
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setMessage("Invalid username or password");
            response.setAccessAllowed(false);
            return response;
        }

        if (user.isMfaEnabled()) {
            response.setMessage("MFA_REQUIRED");
            return response;
        }

        return completeAuthenticatedLogin(user, request.getDeviceId(), request.getIpAddress());
    }

    @Override
    public MfaResponse verifyMfa(MfaRequest request) {
        MfaResponse response = new MfaResponse();

        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("User not found");
            return response;
        }

        if (!"123456".equals(request.getOtp())) {
            response.setMessage("Invalid OTP");
            return response;
        }

        LoginResponse loginResult = completeAuthenticatedLogin(
                user, request.getDeviceId(), request.getIpAddress());

        if (!loginResult.isAccessAllowed()) {
            response.setMessage(loginResult.getMessage());
            return response;
        }

        response.setAccessToken(loginResult.getAccessToken());
        response.setRefreshToken(loginResult.getRefreshToken());
        response.setExpiresIn(loginResult.getExpiresIn());
        response.setMessage("MFA verification successful");

        return response;
    }

    private LoginResponse completeAuthenticatedLogin(User user, String deviceId, String ipAddress) {
        LoginResponse response = new LoginResponse();

        RiskCalculateRequest riskRequest = new RiskCalculateRequest();
        riskRequest.setUserId(user.getId());
        riskRequest.setDeviceId(deviceId);
        riskRequest.setIpAddress(ipAddress);
        RiskScoreResponse risk = riskService.calculateRisk(riskRequest);

        PolicyEvaluateRequest policyRequest = new PolicyEvaluateRequest();
        policyRequest.setUserId(user.getId());
        policyRequest.setResource("login");
        policyRequest.setAction("access");
        policyRequest.setDeviceId(deviceId);
        policyRequest.setIpAddress(ipAddress);
        PolicyEvaluateResponse policyResult = policyEvaluator.evaluate(policyRequest);

        response.setUserRisk(risk.getUserRisk());
        response.setDeviceRisk(risk.getDeviceRisk());
        response.setContextRisk(risk.getContextRisk());
        response.setFinalRisk(risk.getFinalRisk());

        if (!policyResult.isAllowed()) {
            response.setMessage("ACCESS_DENIED: " + policyResult.getReason());
            response.setAccessAllowed(false);
            return response;
        }

        SessionResponse session = sessionService.createSession(user, deviceId, ipAddress, risk);

        RiskCalculateRequest sessionRiskRequest = new RiskCalculateRequest();
        sessionRiskRequest.setUserId(user.getId());
        sessionRiskRequest.setDeviceId(deviceId);
        sessionRiskRequest.setIpAddress(ipAddress);
        sessionRiskRequest.setSessionId(session.getSessionId());
        riskService.calculateRisk(sessionRiskRequest);

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirySeconds());
        response.setSessionId(session.getSessionId());
        response.setMessage("Login successful");
        response.setAccessAllowed(true);

        return response;
    }

    @Override
    public GenericResponse logout(LogoutRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken()).orElse(null);
        if (user != null) {
            sessionService.terminateSessionsForUser(user.getId(), "User logout");
            user.setRefreshToken(null);
            userRepository.save(user);
        }

        return new GenericResponse("Logged out successfully");
    }

    @Override
    public LoginResponse refreshToken(RefreshRequest request) {
        LoginResponse response = new LoginResponse();

        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            response.setMessage("Invalid or expired refresh token");
            return response;
        }

        User user = userRepository.findByRefreshToken(request.getRefreshToken()).orElse(null);
        if (user == null) {
            response.setMessage("Refresh token not found");
            return response;
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirySeconds());
        response.setMessage("Token refreshed successfully");

        return response;
    }

    @Override
    public ProfileResponse getProfile(String authHeader) {
        ProfileResponse response = new ProfileResponse();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return response;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return response;
        }

        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return response;
        }

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
