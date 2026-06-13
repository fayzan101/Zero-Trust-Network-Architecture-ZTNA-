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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Zero-trust policy management and access evaluation")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create policy", description = "Creates a new access policy with risk thresholds and role requirements. Admin only.")
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.ok(policyService.createPolicy(request));
    }

    @GetMapping
    @Operation(summary = "List policies", description = "Returns all policies (enabled and disabled).")
    public ResponseEntity<List<PolicyResponse>> listPolicies() {
        return ResponseEntity.ok(policyService.listPolicies());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update policy", description = "Updates an existing policy by ID. Admin only.")
    public ResponseEntity<?> updatePolicy(
            @Parameter(description = "Policy ID") @PathVariable Long id,
            @Valid @RequestBody PolicyRequest request) {
        PolicyResponse response = policyService.updatePolicy(id, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete policy", description = "Permanently deletes a policy. Admin only.")
    public ResponseEntity<GenericResponse> deletePolicy(
            @Parameter(description = "Policy ID") @PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok(new GenericResponse("Policy deleted successfully"));
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate access",
            description = "Checks whether a user may access a resource/action under active policies and current risk.")
    public ResponseEntity<PolicyEvaluateResponse> evaluate(@RequestBody PolicyEvaluateRequest request) {
        return ResponseEntity.ok(policyService.evaluate(request));
    }
}
