package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.RiskCalculateRequest;
import com.yourname.zerotrust.dto.RiskScoreResponse;
import com.yourname.zerotrust.service.RiskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/risk")
@Tag(name = "Risk", description = "Risk scoring with weighted user/device/context breakdown")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate risk score",
            description = "Computes weighted risk and returns explanatory reasons for each factor.")
    public ResponseEntity<RiskScoreResponse> calculateRisk(@RequestBody RiskCalculateRequest request) {
        return ResponseEntity.ok(riskService.calculateRisk(request));
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "User risk history", description = "Returns all risk scores for a user, newest first.")
    public ResponseEntity<List<RiskScoreResponse>> getUserRiskHistory(
            @Parameter(description = "User ID") @PathVariable Long id) {
        return ResponseEntity.ok(riskService.getUserRiskHistory(id));
    }

    @GetMapping("/session/{id}")
    @Operation(summary = "Session risk", description = "Returns the latest risk score for a session.")
    public ResponseEntity<RiskScoreResponse> getSessionRisk(
            @Parameter(description = "Session UUID") @PathVariable String id) {
        RiskScoreResponse response = riskService.getSessionRisk(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
