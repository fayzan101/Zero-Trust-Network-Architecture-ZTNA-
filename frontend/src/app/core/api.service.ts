import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AccessComparisonResponse, AttackReport, AttackResult, AuditLog,
  ComparisonMetrics, Device, IncidentSummary, IncidentTimeline,
  Policy, PolicyEvaluateRequest, PolicyEvaluateResponse, RiskScore,
  Session
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getMetrics(): Observable<ComparisonMetrics> {
    return this.http.get<ComparisonMetrics>(`${this.base}/api/metrics/comparison`);
  }

  getSessions(): Observable<Session[]> {
    return this.http.get<Session[]>(`${this.base}/api/sessions`);
  }

  terminateSession(sessionId: string, reason: string): Observable<unknown> {
    return this.http.post(`${this.base}/api/session/terminate`, { sessionId, reason });
  }

  getPolicies(): Observable<Policy[]> {
    return this.http.get<Policy[]>(`${this.base}/api/policies`);
  }

  evaluatePolicy(req: PolicyEvaluateRequest): Observable<PolicyEvaluateResponse> {
    return this.http.post<PolicyEvaluateResponse>(`${this.base}/api/policies/evaluate`, req);
  }

  compareAccess(req: PolicyEvaluateRequest): Observable<AccessComparisonResponse> {
    return this.http.post<AccessComparisonResponse>(`${this.base}/api/access/compare`, req);
  }

  calculateRisk(req: { userId: number; deviceId?: string; ipAddress?: string }): Observable<RiskScore> {
    return this.http.post<RiskScore>(`${this.base}/api/risk/calculate`, req);
  }

  getAuditLogs(severity?: string): Observable<AuditLog[]> {
    const q = severity ? `?severity=${severity}` : '';
    return this.http.get<AuditLog[]>(`${this.base}/api/logs${q}`);
  }

  getIncidents(): Observable<IncidentSummary[]> {
    return this.http.get<IncidentSummary[]>(`${this.base}/api/incidents`);
  }

  getIncidentTimeline(id: number): Observable<IncidentTimeline> {
    return this.http.get<IncidentTimeline>(`${this.base}/api/incidents/${id}`);
  }

  getAttackReport(): Observable<AttackReport> {
    return this.http.get<AttackReport>(`${this.base}/api/attack/report`);
  }

  simulateCredentialTheft(body: { username: string; stolenFromIp?: string; attackerDeviceId?: string }): Observable<AttackResult> {
    return this.http.post<AttackResult>(`${this.base}/api/attack/credential-theft`, body);
  }

  simulatePrivilegeEscalation(body: { userId: number; targetRole?: string; resource?: string; action?: string }): Observable<AttackResult> {
    return this.http.post<AttackResult>(`${this.base}/api/attack/privilege-escalation`, body);
  }

  simulateLateralMovement(body: { userId: number; targetResource?: string; targetIp?: string }): Observable<AttackResult> {
    return this.http.post<AttackResult>(`${this.base}/api/attack/lateral-movement`, body);
  }

  getDevice(deviceId: string): Observable<Device> {
    return this.http.get<Device>(`${this.base}/api/devices/${deviceId}`);
  }

  getDevicesByUser(userId: string): Observable<Device[]> {
    return this.http.get<Device[]>(`${this.base}/api/devices/user/${userId}`);
  }
}
