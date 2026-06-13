# Zero Trust Project

A full-stack Zero Trust demo with Spring Boot backend, Angular dashboard, and PostgreSQL database.

## Structure

- **backend/**: Spring Boot API, JWT, risk, policy, monitoring, attack simulation, etc.
- **frontend/**: Angular 18 security console
- **docker-compose.yml**: Orchestrates backend, frontend, and Postgres

## Quick Start

### Docker
1. `docker-compose up --build`
2. Frontend: http://localhost:5173
3. Backend: http://localhost:8082 (mapped from 8080 in compose)

### Local development
```powershell
# Terminal 1 — backend
cd backend
mvn spring-boot:run

# Terminal 2 — Angular dashboard
cd frontend
npm install
npm start
```
- Dashboard: http://localhost:5173
- API / Swagger: http://localhost:8080

### Demo credentials
- `demo` / `Demo123!` — standard user (no MFA)
- `admin` / `Admin123!` — admin (MFA enabled; use authenticator or MFA setup)

## Backend
- Java 17, Spring Boot 3, JPA, JWT, Postgres
- See backend/README.md for API details

## Frontend
- Angular 18, dark-themed security console
- Live WebSocket feed, ZT comparison, risk engine, incidents, audit logs
