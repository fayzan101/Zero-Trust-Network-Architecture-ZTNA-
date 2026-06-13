package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.AnomalyCheckRequest;
import com.yourname.zerotrust.dto.AnomalyCheckResponse;
import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.SessionResponse;
import com.yourname.zerotrust.dto.SessionTerminateRequest;
import com.yourname.zerotrust.service.SessionService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class MonitoringController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/api/sessions")
    public ResponseEntity<List<SessionResponse>> getActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }

    @PostMapping("/api/anomaly/check")
    public ResponseEntity<AnomalyCheckResponse> checkAnomaly(@RequestBody AnomalyCheckRequest request) {
        AnomalyCheckResponse response = sessionService.checkAnomaly(request.getSessionId());
        if (response.getMessage().equals("Session not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/session/terminate")
    public ResponseEntity<?> terminateSession(@RequestBody SessionTerminateRequest request) {
        SessionResponse response = sessionService.terminateSession(request);
        if (response == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Error: Session not found"));
        }
        return ResponseEntity.ok(response);
    }
}
