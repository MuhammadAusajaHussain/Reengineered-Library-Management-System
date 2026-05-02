# Setup Instructions

## Backend API
1. Open terminal in `api`.
2. Run:
   - `& "C:\Program Files\Java\maven-mvnd-1.0.5-windows-amd64\bin\mvnd.cmd" spring-boot:run`
3. API base URL:
   - `http://localhost:9000`

## Frontend
1. Open terminal in `client`.
2. Run:
   - `npm run dev`
3. Frontend URL:
   - `http://localhost:8080`

## Default Credentials
- `admin / admin`

## Notes
- Vite proxy routes `/api/*` to `http://localhost:9000`.
- DB is embedded Derby at `Database/LMS_REENGINEERED` (persistent).
- Start only **one** backend instance at a time.

## Common Errors and Fixes

### `Port 9000 is already in use`
- A backend is already running.
- Check process:
  - `Get-NetTCPConnection -LocalPort 9000 | Select-Object OwningProcess,State`
- Stop process:
  - `Stop-Process -Id <PID> -Force`

### `Another instance of Derby may have already booted the database`
- Same cause as above: duplicate JVM trying to use the same Derby DB.
- Stop extra backend Java process and run one backend instance only.

### `JAVA_HOME` warning when using mvnd
- Warning is non-fatal.
- Optional fix:
  - `setx JAVA_HOME "C:\Program Files\Java\jdk-26.0.1"`
  - `setx PATH "$($env:PATH);%JAVA_HOME%\bin"`
