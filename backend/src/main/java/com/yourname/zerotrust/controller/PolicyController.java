package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.PolicyEvaluateRequest;
import com.yourname.zerotrust.dto.PolicyEvaluateResponse;
import com.yourname.zerotrust.dto.PolicyRequest;
import com.yourname.zerotrust.dto.PolicyResponse;
import com.yourname.zerotrust.service.PolicyService;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> createPolicy(@RequestBody PolicyRequest request) {
        return ResponseEntity.ok(policyService.createPolicy(request));
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> listPolicies() {
        return ResponseEntity.ok(policyService.listPolicies());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePolicy(@PathVariable Long id, @RequestBody PolicyRequest request) {
        PolicyResponse response = policyService.updatePolicy(id, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok(new GenericResponse("Policy deleted successfully"));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<PolicyEvaluateResponse> evaluate(@RequestBody PolicyEvaluateRequest request) {
        return ResponseEntity.ok(policyService.evaluate(request));
    }
}
