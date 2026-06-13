# ZTNA Simulator API Reference

All endpoints are prefixed with `/api/`. Use JWT Bearer auth except where noted.

**Swagger UI:** `/swagger-ui.html` | **OpenAPI JSON:** `/v3/api-docs`

**Demo credentials (seeded on startup):** `admin` / `Admin123!` · `demo` / `Demo123!` · device `demo-laptop-01`

---

## Authentication APIs

| Method | Endpoint               | Auth     | Description                    |
| ------ | ---------------------- | -------- | ------------------------------ |
| POST   | /api/auth/register     | Public   | Register user                  |
| POST   | /api/auth/login        | Public   | Login (returns 202 if MFA req) |
| POST   | /api/auth/mfa          | Public   | Verify TOTP after login        |
| POST   | /api/auth/mfa/setup    | Bearer   | Generate MFA secret + QR URL   |
| POST   | /api/auth/mfa/enable   | Bearer   | Confirm OTP and enable MFA     |
| POST   | /api/auth/mfa/disable  | Bearer   | Disable MFA (password + OTP)   |
| POST   | /api/auth/logout       | Bearer   | Logout and terminate sessions  |
| POST   | /api/auth/refresh      | Public   | Refresh JWT                    |
| GET    | /api/auth/profile      | Bearer   | Get user info (incl. mfaEnabled)|

**Login request (optional context for risk/policy):**
```json
{ "username": "john", "password": "secret", "deviceId": "laptop-01", "ipAddress": "192.168.1.10" }
```

**Login response includes:** `accessToken`, `refreshToken`, `sessionId`, `userRisk`, `deviceRisk`, `contextRisk`, `finalRisk`, `accessAllowed`

**HTTP status codes:** `401` invalid credentials | `403` policy/risk denied | `202` MFA required

---

## User & Role Management APIs

| Method | Endpoint          | Auth  | Description              |
| ------ | ----------------- | ----- | ------------------------ |
| POST   | /api/users        | ADMIN | Create user              |
| GET    | /api/users        | ADMIN | List users               |
| POST   | /api/users/get    | ADMIN | Get user by id in body   |
| PUT    | /api/users        | ADMIN | Update user              |
| DELETE | /api/users        | ADMIN | Delete user              |
| POST   | /api/roles        | ADMIN | Create role              |
| GET    | /api/roles        | ADMIN | List roles               |
| PUT    | /api/roles        | ADMIN | Update role              |
| DELETE | /api/roles/{id}   | ADMIN | Delete role              |

---

## Device Trust APIs

| Method | Endpoint                              | Auth   | Description                    |
| ------ | ------------------------------------- | ------ | ------------------------------ |
| POST   | /api/devices/register                 | ADMIN  | Register device for ownerId    |
| GET    | /api/devices/{deviceId}               | Bearer | Device info                    |
| GET    | /api/devices/{deviceId}/trust-score   | Bearer | Current trust score (0–100)    |
| GET    | /api/devices/user/{userId}            | Bearer | List devices by user           |
| POST   | /api/devices/evaluate?deviceId=       | Bearer | Re-evaluate trust              |
| PUT    | /api/devices/{deviceId}/update        | ADMIN  | Update device and recalc trust |

**Register request:**
```json
{ "deviceId": "laptop-01", "ownerId": 2, "deviceType": "laptop", "os": "linux", "ipAddress": "192.168.1.10" }
```

---

## Risk Engine APIs

| Method | Endpoint                 | Auth   | Description                    |
| ------ | ------------------------ | ------ | ------------------------------ |
| POST   | /api/risk/calculate      | Bearer | Calculate risk score + reasons |
| GET    | /api/risk/user/{id}      | Bearer | User risk history              |
| GET    | /api/risk/session/{id}   | Bearer | Session risk details           |

**Sample response:**
```json
{
  "userRisk": 35,
  "deviceRisk": 20,
  "contextRisk": 40,
  "finalRisk": 32,
  "reasons": [
    "MFA not enabled (+25 user risk)",
    "Device trust score 85 → device risk 15",
    "Final risk = weighted 30/40/30 → 32"
  ]
}
```

---

## Policy Engine APIs

| Method | Endpoint                 | Auth          | Description              |
| ------ | ------------------------ | ------------- | ------------------------ |
| POST   | /api/policies            | ADMIN         | Create policy            |
| GET    | /api/policies            | Bearer        | List policies            |
| PUT    | /api/policies/{id}       | ADMIN         | Update policy            |
| DELETE | /api/policies/{id}       | ADMIN         | Delete policy            |
| POST   | /api/policies/evaluate   | Bearer        | Evaluate access decision |

---

## Monitoring APIs

| Method | Endpoint                 | Auth  | Description               |
| ------ | ------------------------ | ----- | ------------------------- |
| GET    | /api/sessions            | ADMIN | Active sessions           |
| POST   | /api/anomaly/check       | ADMIN | Run anomaly detection     |
| POST   | /api/session/terminate   | ADMIN | Kill suspicious session   |

Stale ACTIVE sessions (>24h inactivity) are auto-terminated hourly.

---

## Audit Log APIs

| Method | Endpoint                 | Auth  | Description                              |
| ------ | ------------------------ | ----- | ---------------------------------------- |
| GET    | /api/logs                | ADMIN | All audit logs                           |
| GET    | /api/logs?severity=CRITICAL | ADMIN | Filter by INFO, WARN, or CRITICAL     |
| GET    | /api/logs?eventType=POLICY_DENIED | ADMIN | Filter by event type            |
| GET    | /api/logs?username=admin | ADMIN | Filter by username                       |

**Key event types:** `POLICY_DENIED`, `ACCESS_DENIED`, `SESSION_TERMINATED`, `ATTACK_SIMULATED`, `ANOMALY_DETECTED`

---

## Attack Simulation APIs

| Method | Endpoint                           | Auth  | Description                |
| ------ | ---------------------------------- | ----- | -------------------------- |
| POST   | /api/attack/credential-theft       | ADMIN | Simulate stolen password   |
| POST   | /api/attack/privilege-escalation   | ADMIN | Simulate role abuse        |
| POST   | /api/attack/lateral-movement       | ADMIN | Simulate server hopping    |
| GET    | /api/attack/report                 | ADMIN | Attack detection report    |

**Credential theft request:**
```json
{ "username": "demo", "sourceIp": "203.0.113.50" }
```

**Attack report response:**
```json
{
  "totalAttacks": 5,
  "detectedCount": 4,
  "undetectedCount": 1,
  "detectionRate": 80.0,
  "attacks": [ ... ]
}
```

---

## Metrics APIs

| Method | Endpoint                 | Auth   | Description                              |
| ------ | ------------------------ | ------ | ---------------------------------------- |
| GET    | /api/metrics/comparison  | Bearer | Traditional vs zero-trust side-by-side   |

**Response shape:**
```json
{
  "traditional": { "accessControl": "Static RBAC — binary allow/deny", "detectionRate": 0 },
  "zeroTrust": { "policiesEnforced": 4, "activeSessions": 2, "averageRiskScore": 28.5, "detectionRate": 80.0 }
}
```

---

## Error format

All errors return:
```json
{ "status": 401, "error": "Unauthorized", "message": "...", "timestamp": 1234567890 }
```

---

See `docs/PLANNING.md` for module breakdown and `backend/README.md` for runbook.
