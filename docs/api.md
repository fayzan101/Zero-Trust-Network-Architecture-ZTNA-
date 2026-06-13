# ZTNA Simulator API Reference

All endpoints are prefixed with `/api/`. Use JWT Bearer auth except where noted.

**Swagger UI:** `/swagger-ui.html` | **OpenAPI JSON:** `/v3/api-docs`

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
| POST   | /api/users        | Bearer| Create user              |
| GET    | /api/users        | Bearer| List users               |
| POST   | /api/users/get    | Bearer| Get user by id in body   |
| PUT    | /api/users        | Bearer| Update user              |
| DELETE | /api/users        | Bearer| Delete user              |
| POST   | /api/roles        | Bearer| Create role              |
| GET    | /api/roles        | Bearer| List roles               |
| PUT    | /api/roles        | Bearer| Update role              |

---

## Device Trust APIs

| Method | Endpoint                              | Auth   | Description           |
| ------ | ------------------------------------- | ------ | --------------------- |
| POST   | /api/devices/register                 | Bearer | Register device       |
| GET    | /api/devices/{deviceId}               | Bearer | Device info           |
| GET    | /api/devices/user/{userId}            | Bearer | List devices by user  |
| POST   | /api/devices/evaluate?deviceId=       | Bearer | Re-evaluate trust     |

---

## Risk Engine APIs

| Method | Endpoint                 | Auth   | Description           |
| ------ | ------------------------ | ------ | --------------------- |
| POST   | /api/risk/calculate      | Bearer | Calculate risk score  |
| GET    | /api/risk/user/{id}      | Bearer | User risk history     |
| GET    | /api/risk/session/{id}   | Bearer | Session risk details  |

**Sample response:**
```json
{ "userRisk": 35, "deviceRisk": 20, "contextRisk": 40, "finalRisk": 32 }
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

## Monitoring & Logs APIs

| Method | Endpoint                 | Auth  | Description               |
| ------ | ------------------------ | ----- | ------------------------- |
| GET    | /api/logs                | ADMIN | Audit logs (?eventType, ?username) |
| GET    | /api/sessions            | ADMIN | Active sessions           |
| POST   | /api/anomaly/check       | ADMIN | Run anomaly detection     |
| POST   | /api/session/terminate   | ADMIN | Kill suspicious session   |

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
{ "username": "john", "stolenFromIp": "203.0.113.50", "attackerDeviceId": "unknown-device" }
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

## Error format

All errors return:
```json
{ "status": 401, "error": "Unauthorized", "message": "...", "timestamp": 1234567890 }
```

---

See `docs/PLANNING.md` for module breakdown and `backend/README.md` for runbook.
