package com.yourname.zerotrust.controller;

import com.yourname.zerotrust.dto.*;
import com.yourname.zerotrust.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa")
    public ResponseEntity<MfaResponse> verifyMfa(@Valid @RequestBody MfaRequest request) {
        MfaResponse response = authService.verifyMfa(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse> logout(@Valid @RequestBody LogoutRequest request) {
        GenericResponse response = authService.logout(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(@RequestHeader("Authorization") String authHeader) {
        ProfileResponse response = authService.getProfile(authHeader);
        return ResponseEntity.ok(response);
    }
}
