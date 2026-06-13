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

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @PostMapping("/calculate")
    public ResponseEntity<RiskScoreResponse> calculateRisk(@RequestBody RiskCalculateRequest request) {
        return ResponseEntity.ok(riskService.calculateRisk(request));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<RiskScoreResponse>> getUserRiskHistory(@PathVariable Long id) {
        return ResponseEntity.ok(riskService.getUserRiskHistory(id));
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<RiskScoreResponse> getSessionRisk(@PathVariable String id) {
        RiskScoreResponse response = riskService.getSessionRisk(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
