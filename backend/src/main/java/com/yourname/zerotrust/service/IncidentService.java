package com.yourname.zerotrust.service;

import java.util.List;

import com.yourname.zerotrust.dto.IncidentSummaryResponse;
import com.yourname.zerotrust.dto.IncidentTimelineResponse;

public interface IncidentService {
    List<IncidentSummaryResponse> listIncidents();
    IncidentTimelineResponse getTimeline(Long incidentId);
}
