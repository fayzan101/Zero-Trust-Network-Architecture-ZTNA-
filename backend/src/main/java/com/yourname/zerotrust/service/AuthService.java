package com.yourname.zerotrust.service;

import com.yourname.zerotrust.dto.*;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    MfaResponse verifyMfa(MfaRequest request);
    MfaSetupResponse setupMfa(String username);
    GenericResponse enableMfa(String username, MfaEnableRequest request);
    GenericResponse disableMfa(String username, MfaDisableRequest request);
    GenericResponse logout(LogoutRequest request);
    LoginResponse refreshToken(RefreshRequest request);
    ProfileResponse getProfile(String authHeader);
}
