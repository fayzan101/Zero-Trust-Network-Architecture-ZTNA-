package com.yourname.zerotrust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.AccessComparisonResponse;
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.service.AccessComparisonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/access")
@Tag(name = "Access Comparison", description = "Traditional RBAC vs zero-trust per-request comparison")
public class AccessComparisonController {

    @Autowired
    private AccessComparisonService accessComparisonService;

    @PostMapping("/compare")
    @Operation(summary = "Compare access models",
            description = "Evaluates the same request through static RBAC and full zero-trust policy+risk engine.")
    public ResponseEntity<AccessComparisonResponse> compare(@RequestBody PolicyEvaluateRequest request) {
        return ResponseEntity.ok(accessComparisonService.compare(request));
    }
}
