package com.yourname.zerotrust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.ComparisonMetricsResponse;
import com.yourname.zerotrust.service.MetricsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metrics", description = "Traditional vs zero-trust comparison for thesis/demo dashboards")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/comparison")
    @Operation(summary = "Comparison metrics",
            description = "Side-by-side metrics: static RBAC vs dynamic zero-trust (policies, sessions, risk, attack detection).")
    public ResponseEntity<ComparisonMetricsResponse> comparison() {
        return ResponseEntity.ok(metricsService.getComparison());
    }
}
