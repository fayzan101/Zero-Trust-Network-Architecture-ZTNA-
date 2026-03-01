# ZTNA Simulator API Reference

---

## Authentication APIs

| Method | Endpoint             | Description           |
| ------ | -------------------- | --------------------- |
| POST   | /api/auth/register   | Register user         |
| POST   | /api/auth/login      | Login request         |
| POST   | /api/auth/mfa        | Verify OTP            |
| POST   | /api/auth/logout     | Logout                |
| POST   | /api/auth/refresh    | Refresh JWT           |
| GET    | /api/auth/profile    | Get user info         |

---

## User & Role Management APIs

| Method | Endpoint          | Description     |
| ------ | ----------------- | -------------- |
| POST   | /api/users        | Create user    |
| GET    | /api/users        | List users     |
| GET    | /api/users        | Get user (by id in body) |
| PUT    | /api/users        | Update user (id in body) |
| DELETE | /api/users        | Delete user (id in body) |
| POST   | /api/roles        | Create role    |
| GET    | /api/roles        | List roles     |
| PUT    | /api/roles        | Update role (id in body) |

---

## Device Trust APIs

| Method | Endpoint                        | Description         |
| ------ | ------------------------------- | ------------------- |
| POST   | /api/devices/register           | Register device     |
| GET    | /api/devices/{id}               | Device info         |
| POST   | /api/devices/evaluate           | Evaluate device     |
| GET    | /api/devices/{id}/trust-score   | Get trust score     |
| PUT    | /api/devices/{id}/update        | Update device       |

---

## Risk Engine APIs

| Method | Endpoint                 | Description           |
| ------ | ------------------------ | --------------------- |
| POST   | /api/risk/calculate      | Calculate risk score  |
| GET    | /api/risk/user/{id}      | User risk history     |
| GET    | /api/risk/session/{id}   | Session risk details  |

**Sample Response:**
```
{
  userRisk: 35,
  deviceRisk: 20,
  contextRisk: 40,
  finalRisk: 32
}
```

---

## Policy Engine APIs

| Method | Endpoint                 | Description               |
| ------ | ------------------------ | ------------------------- |
| POST   | /api/policies            | Create policy             |
| GET    | /api/policies            | List policies             |
| PUT    | /api/policies/{id}       | Update policy             |
| DELETE | /api/policies/{id}       | Delete policy             |
| POST   | /api/policies/evaluate   | Evaluate access decision  |

---

## Monitoring & Logs APIs

| Method | Endpoint                 | Description               |
| ------ | ------------------------ | ------------------------- |
| GET    | /api/logs                | View system logs          |
| GET    | /api/sessions            | Active sessions           |
| POST   | /api/anomaly/check       | Run anomaly detection     |
| POST   | /api/session/terminate   | Kill suspicious session   |

---

## Attack Simulation APIs

| Method | Endpoint                           | Description                |
| ------ | ---------------------------------- | -------------------------- |
| POST   | /api/attack/credential-theft       | Simulate stolen password   |
| POST   | /api/attack/privilege-escalation   | Simulate role abuse        |
| POST   | /api/attack/lateral-movement       | Simulate server hopping    |
| GET    | /api/attack/report                 | Attack detection report    |

---

## Notes
- All endpoints are prefixed with `/api/`
- Use JWT for authentication (except register/login/mfa)
- Admin-only endpoints require elevated role
- See PLANNING.md for module breakdown and implementation phases
