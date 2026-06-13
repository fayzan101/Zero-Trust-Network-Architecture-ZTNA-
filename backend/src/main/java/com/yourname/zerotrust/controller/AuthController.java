package com.yourname.zerotrust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.LoginRequest;
import com.yourname.zerotrust.dto.LoginResponse;
import com.yourname.zerotrust.dto.LogoutRequest;
import com.yourname.zerotrust.dto.MfaDisableRequest;
import com.yourname.zerotrust.dto.MfaEnableRequest;
import com.yourname.zerotrust.dto.MfaRequest;
import com.yourname.zerotrust.dto.MfaResponse;
import com.yourname.zerotrust.dto.MfaSetupResponse;
import com.yourname.zerotrust.dto.ProfileResponse;
import com.yourname.zerotrust.dto.RefreshRequest;
import com.yourname.zerotrust.dto.RegisterRequest;
import com.yourname.zerotrust.dto.RegisterResponse;
import com.yourname.zerotrust.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, MFA, tokens, and profile")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a user with the given role. Public endpoint.")
    @ApiResponse(responseCode = "201", description = "User created")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates user, runs risk/policy checks, and creates a session. Returns 202 if MFA is required.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "202", description = "MFA verification required")
    @ApiResponse(responseCode = "403", description = "Access denied by policy or risk threshold")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if ("MFA_REQUIRED".equals(response.getMessage())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa")
    @Operation(summary = "Verify MFA OTP", description = "Completes login after MFA challenge. Public endpoint.")
    public ResponseEntity<MfaResponse> verifyMfa(@Valid @RequestBody MfaRequest request) {
        return ResponseEntity.ok(authService.verifyMfa(request));
    }

    @PostMapping("/mfa/setup")
    @Operation(summary = "Setup MFA", description = "Generates TOTP secret and QR URL for the authenticated user.")
    public ResponseEntity<MfaSetupResponse> setupMfa() {
        return ResponseEntity.ok(authService.setupMfa(currentUsername()));
    }

    @PostMapping("/mfa/enable")
    @Operation(summary = "Enable MFA", description = "Confirms OTP and enables MFA on the authenticated account.")
    public ResponseEntity<GenericResponse> enableMfa(@Valid @RequestBody MfaEnableRequest request) {
        return ResponseEntity.ok(authService.enableMfa(currentUsername(), request));
    }

    @PostMapping("/mfa/disable")
    @Operation(summary = "Disable MFA", description = "Disables MFA after password and OTP verification.")
    public ResponseEntity<GenericResponse> disableMfa(@Valid @RequestBody MfaDisableRequest request) {
        return ResponseEntity.ok(authService.disableMfa(currentUsername(), request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates refresh token and terminates active sessions.")
    public ResponseEntity<GenericResponse> logout(@Valid @RequestBody LogoutRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token. Public endpoint.")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get profile", description = "Returns the authenticated user's profile including MFA status.")
    public ResponseEntity<ProfileResponse> profile(
            @Parameter(description = "Bearer JWT access token") @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.getProfile(authHeader));
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
