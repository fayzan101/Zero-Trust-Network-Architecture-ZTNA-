# ZTNA Backend

Spring Boot 3 API for the Zero Trust Network Access simulator.

## Stack

- Java 17, Spring Boot 3.2, Spring Security, JPA
- PostgreSQL (production), H2 (tests)
- JWT auth, TOTP MFA, risk engine, policy engine, session monitoring, attack simulation

## Run locally

```bash
# With Docker (recommended)
docker-compose up --build

# Or manually (requires Postgres on localhost:5433)
cd backend
mvn spring-boot:run
```

API: `http://localhost:8082` (Docker) or `http://localhost:8080` (local Maven)

Swagger UI: `http://localhost:8082/swagger-ui.html`

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | (dev default in yml) | JWT signing key (min 32 chars) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,...` | Comma-separated frontend origins |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5433/zerotrustdb` | Database URL |

## API modules

| Module | Base path | Auth |
|--------|-----------|------|
| Auth | `/api/auth` | Public: register, login, mfa, refresh |
| Users | `/api/users` | Authenticated |
| Roles | `/api/roles` | Authenticated |
| Devices | `/api/devices` | Authenticated |
| Risk | `/api/risk` | Authenticated |
| Policies | `/api/policies` | Authenticated; CRUD requires ADMIN |
| Monitoring | `/api/sessions`, `/api/anomaly/check`, `/api/session/terminate` | ADMIN |
| Audit logs | `/api/logs` | ADMIN |
| Attacks | `/api/attack` | ADMIN |

## MFA enrollment

1. `POST /api/auth/login` — get JWT
2. `POST /api/auth/mfa/setup` — get `otpauthUrl`
3. Scan in Google Authenticator
4. `POST /api/auth/mfa/enable` with `{ "otp": "123456" }`
5. Future logins return `202 MFA_REQUIRED` → `POST /api/auth/mfa`

## Tests

```bash
cd backend
mvn test
```

Tests use the `test` profile with in-memory H2.

## Attack simulation

Admin-only endpoints for safe attack simulation:

- `POST /api/attack/credential-theft` — simulate stolen credentials from foreign IP
- `POST /api/attack/privilege-escalation` — simulate role abuse
- `POST /api/attack/lateral-movement` — simulate cross-resource access
- `GET /api/attack/report` — detection summary and history

See `docs/api.md` for full request/response reference.
