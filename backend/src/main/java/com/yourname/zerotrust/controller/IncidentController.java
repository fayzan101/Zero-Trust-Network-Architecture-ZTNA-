package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.IncidentSummaryResponse;
import com.yourname.zerotrust.dto.IncidentTimelineResponse;
import com.yourname.zerotrust.service.IncidentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/incidents")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Incidents", description = "Security incident timeline and forensic replay (admin only)")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping
    @Operation(summary = "List security incidents", description = "Returns WARN and CRITICAL audit events as incidents.")
    public ResponseEntity<List<IncidentSummaryResponse>> listIncidents() {
        return ResponseEntity.ok(incidentService.listIncidents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Incident timeline", description = "Reconstructs the full event timeline for an incident by audit log ID.")
    public ResponseEntity<IncidentTimelineResponse> getTimeline(
            @Parameter(description = "Audit log ID anchoring the incident") @PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getTimeline(id));
    }
}
