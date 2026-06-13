export interface LoginRequest {
  username: string;
  password: string;
  deviceId?: string;
  ipAddress?: string;
}

export interface LoginResponse {
  accessToken?: string;
  refreshToken?: string;
  expiresIn?: number;
  message?: string;
  sessionId?: string;
  userRisk?: number;
  deviceRisk?: number;
  contextRisk?: number;
  finalRisk?: number;
  accessAllowed?: boolean;
  stepUpRequired?: boolean;
  stepUpLevel?: string;
  riskReasons?: string[];
}

export interface MfaRequest {
  username: string;
  otp: string;
  deviceId?: string;
  ipAddress?: string;
}

export interface StepUpRequest {
  username: string;
  password: string;
  otp?: string;
  deviceId?: string;
  ipAddress?: string;
}

export interface SecurityEvent {
  type?: string;
  eventType?: string;
  severity?: string;
  username?: string;
  userId?: number;
  ipAddress?: string;
  details?: string;
  timestamp?: string;
  finalRisk?: number;
  stepUpLevel?: string;
}

export interface ComparisonMetrics {
  traditional: ModelMetrics;
  zeroTrust: ModelMetrics;
}

export interface ModelMetrics {
  accessControl?: string;
  deviceVerification?: string;
  riskScoring?: string;
  continuousMonitoring?: string;
  policiesEnforced?: number;
  activeSessions?: number;
  averageRiskScore?: number;
  attacksSimulated?: number;
  attacksDetected?: number;
  detectionRate?: number;
}

export interface Session {
  id?: number;
  sessionId: string;
  userId?: number;
  deviceId?: string;
  ipAddress?: string;
  finalRisk?: number;
  status?: string;
  anomalyDetected?: boolean;
  anomalyReason?: string;
  startedAt?: string;
}

export interface Policy {
  id: number;
  name: string;
  description?: string;
  resource: string;
  action: string;
  requiredRole?: string;
  minDeviceTrust?: number;
  maxRiskThreshold?: number;
  enabled: boolean;
}

export interface PolicyEvaluateRequest {
  userId: number;
  resource: string;
  action: string;
  deviceId?: string;
  ipAddress?: string;
}

export interface PolicyEvaluateResponse {
  allowed: boolean;
  decision: string;
  reason: string;
  matchedPolicy?: string;
  finalRisk?: number;
}

export interface AccessComparisonResponse {
  traditional: AccessDecision;
  zeroTrust: AccessDecision;
  outcomesDiffer: boolean;
}

export interface AccessDecision {
  model: string;
  allowed: boolean;
  decision: string;
  reason: string;
  finalRisk?: number;
  matchedPolicy?: string;
}

export interface RiskScore {
  id?: number;
  userId?: number;
  sessionId?: string;
  userRisk?: number;
  deviceRisk?: number;
  contextRisk?: number;
  finalRisk?: number;
  reasons?: string[];
  calculatedAt?: string;
}

export interface AuditLog {
  id: number;
  eventType: string;
  severity: string;
  username?: string;
  userId?: number;
  ipAddress?: string;
  details?: string;
  createdAt?: string;
  correlationId?: string;
}

export interface IncidentSummary {
  id: number;
  eventType: string;
  severity: string;
  username?: string;
  details?: string;
  createdAt?: string;
  correlationId?: string;
}

export interface IncidentTimeline {
  incident: IncidentSummary;
  timeline: TimelineEvent[];
  session?: Session;
  riskScore?: RiskScore;
}

export interface TimelineEvent {
  timestamp?: string;
  phase?: string;
  eventType?: string;
  severity?: string;
  summary?: string;
  details?: string;
}

export interface AttackReport {
  totalAttacks: number;
  detectedCount: number;
  undetectedCount: number;
  detectionRate: number;
  attacks: AttackResult[];
}

export interface AttackResult {
  id?: number;
  attackType?: string;
  detected?: boolean;
  detectionMethod?: string;
  detectionDetails?: string;
  severity?: string;
  simulatedAt?: string;
  finalRisk?: number;
  message?: string;
}

export interface Device {
  id?: number;
  deviceId: string;
  userId?: string;
  deviceType?: string;
  os?: string;
  ipAddress?: string;
  trustScore?: number;
}
