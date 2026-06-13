# ZTNA Angular Console

Dark-themed security dashboard for the Zero Trust simulator.

## Run locally

```powershell
npm install
npm start
```

Open http://localhost:5173 (proxies `/api` to `http://localhost:8080`).

## Build

```powershell
npm run build
```

Output: `dist/frontend/browser`

## Docker

Built via root `docker-compose.yml` — served on port **5173** with nginx proxying API and WebSocket to the backend.

## Pages

| Route | Description |
|-------|-------------|
| `/login` | Auth with MFA & step-up support |
| `/dashboard` | Metrics, live WebSocket feed, quick actions |
| `/sessions` | Active sessions (admin) |
| `/risk` | Risk calculator with reasons |
| `/policies` | Policy list + evaluate |
| `/compare` | Traditional vs zero-trust comparison |
| `/attacks` | Attack simulation (admin) |
| `/incidents` | Incident timeline (admin) |
| `/audit` | Audit logs with severity filter (admin) |
| `/devices` | Device trust scores |

## Credentials

- `demo` / `Demo123!`
- `admin` / `Admin123!` (admin features + MFA)
