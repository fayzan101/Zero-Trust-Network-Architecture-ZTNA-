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
import com.yourname.zerotrust.dto.MfaDisableRequest;
import com.yourname.zerotrust.dto.MfaEnableRequest;
import com.yourname.zerotrust.dto.MfaRequest;
import com.yourname.zerotrust.dto.MfaResponse;
import com.yourname.zerotrust.dto.MfaSetupResponse;
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
import com.yourname.zerotrust.exception.BadRequestException;
import com.yourname.zerotrust.exception.ForbiddenException;
import com.yourname.zerotrust.exception.UnauthorizedException;
import com.yourname.zerotrust.policy.PolicyEvaluator;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.AuditLogService;
import com.yourname.zerotrust.service.AuthService;
import com.yourname.zerotrust.service.RiskService;
import com.yourname.zerotrust.service.SessionService;
import com.yourname.zerotrust.util.JwtUtil;
import com.yourname.zerotrust.util.TotpUtil;

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

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TotpUtil totpUtil;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
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

        auditLogService.logInfo("USER_REGISTERED", savedUser.getId(), savedUser.getUsername(),
                null, "User registered with role " + roleName);

        RegisterResponse response = new RegisterResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRole(roleName);
        response.setMessage("User registered successfully");
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            auditLogService.logWarn("LOGIN_FAILED", null, request.getUsername(),
                    request.getIpAddress(), "Invalid username or password");
            throw new UnauthorizedException("Invalid username or password");
        }

        if (user.isMfaEnabled()) {
            auditLogService.logInfo("MFA_REQUIRED", user.getId(), user.getUsername(),
                    request.getIpAddress(), "MFA verification required");
            LoginResponse response = new LoginResponse();
            response.setMessage("MFA_REQUIRED");
            return response;
        }

        return completeAuthenticatedLogin(user, request.getDeviceId(), request.getIpAddress());
    }

    @Override
    public MfaResponse verifyMfa(MfaRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            throw new BadRequestException("MFA is not enabled for this user");
        }

        if (!totpUtil.verifyCode(user.getMfaSecret(), request.getOtp())) {
            auditLogService.logWarn("MFA_VERIFY_FAILED", user.getId(), user.getUsername(),
                    request.getIpAddress(), "Invalid OTP provided");
            throw new UnauthorizedException("Invalid OTP");
        }

        LoginResponse loginResult = completeAuthenticatedLogin(
                user, request.getDeviceId(), request.getIpAddress());

        auditLogService.logInfo("MFA_VERIFY_SUCCESS", user.getId(), user.getUsername(),
                request.getIpAddress(), "MFA verification successful");

        MfaResponse response = new MfaResponse();
        response.setAccessToken(loginResult.getAccessToken());
        response.setRefreshToken(loginResult.getRefreshToken());
        response.setExpiresIn(loginResult.getExpiresIn());
        response.setMessage("MFA verification successful");
        return response;
    }

    @Override
    public MfaSetupResponse setupMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (user.isMfaEnabled()) {
            throw new BadRequestException("MFA is already enabled");
        }

        String secret = totpUtil.generateSecret();
        user.setMfaSecret(secret);
        userRepository.save(user);

        MfaSetupResponse response = new MfaSetupResponse();
        response.setSecret(secret);
        response.setOtpauthUrl(totpUtil.buildOtpAuthUrl(user.getUsername(), secret));
        response.setMessage("Scan the OTP auth URL with your authenticator app, then confirm with /mfa/enable");

        auditLogService.logInfo("MFA_SETUP", user.getId(), user.getUsername(),
                null, "MFA secret generated");

        return response;
    }

    @Override
    public GenericResponse enableMfa(String username, MfaEnableRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (user.isMfaEnabled()) {
            throw new BadRequestException("MFA is already enabled");
        }

        if (user.getMfaSecret() == null) {
            throw new BadRequestException("Run MFA setup first");
        }

        if (!totpUtil.verifyCode(user.getMfaSecret(), request.getOtp())) {
            throw new BadRequestException("Invalid OTP — could not enable MFA");
        }

        user.setMfaEnabled(true);
        userRepository.save(user);

        auditLogService.logInfo("MFA_ENABLED", user.getId(), user.getUsername(),
                null, "MFA enabled successfully");

        return new GenericResponse("MFA enabled successfully");
    }

    @Override
    public GenericResponse disableMfa(String username, MfaDisableRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.isMfaEnabled()) {
            throw new BadRequestException("MFA is not enabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        if (!totpUtil.verifyCode(user.getMfaSecret(), request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);

        auditLogService.logWarn("MFA_DISABLED", user.getId(), user.getUsername(),
                null, "MFA disabled by user");

        return new GenericResponse("MFA disabled successfully");
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
            auditLogService.logCritical("ACCESS_DENIED", user.getId(), user.getUsername(),
                    ipAddress, "Login denied: " + policyResult.getReason()
                            + " (risk=" + risk.getFinalRisk() + ")");
            throw new ForbiddenException("ACCESS_DENIED: " + policyResult.getReason());
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

        auditLogService.logInfo("LOGIN_SUCCESS", user.getId(), user.getUsername(),
                ipAddress, "Login successful, session=" + session.getSessionId()
                        + ", risk=" + risk.getFinalRisk());

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
            auditLogService.logInfo("LOGOUT", user.getId(), user.getUsername(),
                    null, "User logged out");
        }

        return new GenericResponse("Logged out successfully");
    }

    @Override
    public LoginResponse refreshToken(RefreshRequest request) {
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        User user = userRepository.findByRefreshToken(request.getRefreshToken()).orElse(null);
        if (user == null) {
            throw new UnauthorizedException("Refresh token not found");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        auditLogService.logInfo("TOKEN_REFRESH", user.getId(), user.getUsername(),
                null, "Access token refreshed");

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirySeconds());
        response.setMessage("Token refreshed successfully");
        return response;
    }

    @Override
    public ProfileResponse getProfile(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setMfaEnabled(user.isMfaEnabled());
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
