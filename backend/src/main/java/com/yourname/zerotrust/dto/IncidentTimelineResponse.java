package com.yourname.zerotrust.dto;

import java.util.List;

public class IncidentTimelineResponse {
    private IncidentSummaryResponse incident;
    private List<TimelineEventResponse> timeline;
    private SessionResponse session;
    private RiskScoreResponse riskScore;
    private AttackSimulationResponse attack;

    public IncidentSummaryResponse getIncident() { return incident; }
    public void setIncident(IncidentSummaryResponse incident) { this.incident = incident; }
    public List<TimelineEventResponse> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineEventResponse> timeline) { this.timeline = timeline; }
    public SessionResponse getSession() { return session; }
    public void setSession(SessionResponse session) { this.session = session; }
    public RiskScoreResponse getRiskScore() { return riskScore; }
    public void setRiskScore(RiskScoreResponse riskScore) { this.riskScore = riskScore; }
    public AttackSimulationResponse getAttack() { return attack; }
    public void setAttack(AttackSimulationResponse attack) { this.attack = attack; }
}
