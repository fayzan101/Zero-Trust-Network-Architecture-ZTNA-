# Zero Trust Project

A full-stack Zero Trust demo with Spring Boot backend, Vite.js React frontend, and PostgreSQL database.

## Structure

- **backend/**: Spring Boot API, JWT, risk, policy, monitoring, attack simulation, etc.
- **frontend/**: Vite.js + React UI
- **docker-compose.yml**: Orchestrates backend, frontend, and Postgres

## Quick Start

1. `docker-compose up --build`
2. Frontend: http://localhost:5173
3. Backend: http://localhost:8080
4. DB: localhost:5432 (user: postgres, pass: password)

## Backend
- Java 17, Spring Boot 3, JPA, JWT, Postgres
- See backend/README.md for API details

## Frontend
- Vite.js, React 18
- See frontend/README.md for UI details

---
Replace `yourname` in package paths with your actual name/handle.
