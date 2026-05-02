# Library Management System (Reengineered)

This repository contains the reengineering of a legacy console-based Java LMS into a layered backend plus modern web frontend, while preserving core library domain workflows.

## Current Architecture

- `Project/`: original legacy NetBeans OOAD console implementation (kept for reference).
- `api/`: Spring Boot backend (`Java 8 target`, embedded Derby DB).
- `client/`: React + Vite frontend.
- `docs/`: setup, strategy, and refactoring documentation for report/presentation.

## Implemented Features

- Authentication with role-based access (`ADMIN`, `LIBRARIAN`, `CLERK`, `BORROWER`).
- Admin user management (create/update/delete/list users).
- Book catalog (list/search/create/update/delete).
- Borrower management (create/list/get profile).
- Circulation (checkout/check-in/renew).
- Fine tracking and fine payment.
- Hold lifecycle:
  - place hold
  - cancel hold
  - auto mark oldest hold `READY` on return when copy becomes available
  - staff checkout of `READY` hold
- Active loans + loan history endpoints.
- Dashboard statistics for staff roles.

## Runtime Ports

- Backend API: `http://localhost:9000`
- Frontend (Vite): `http://localhost:8080`
- Frontend proxies `/api/*` to backend `9000`.

## How To Run

### 1) Start Backend (one instance only)

```powershell
cd "C:\Users\Hi-Light Computers\OneDrive\Desktop\Library Management System\Library-Management-System\api"
& "C:\Program Files\Java\maven-mvnd-1.0.5-windows-amd64\bin\mvnd.cmd" spring-boot:run
```

### 2) Start Frontend

```powershell
cd "C:\Users\Hi-Light Computers\OneDrive\Desktop\Library Management System\Library-Management-System\client"
npm run dev
```

Open: `http://localhost:8080`

## Default Admin Access

- Username: `admin`
- Password: `admin`

Admin bootstrap is enforced by backend startup properties (`lms.bootstrap.admin.*`) to keep demo access stable.

## Troubleshooting

### Error: `Port 9000 is already in use`
You already have a backend instance running. Do not start another one.

Check:
```powershell
Get-NetTCPConnection -LocalPort 9000 | Select-Object OwningProcess,State
```

Stop process if needed:
```powershell
Stop-Process -Id <PID> -Force
```

### Error: `Another instance of Derby may have already booted the database`
Same root cause: duplicate backend instance locking Derby DB.
Stop duplicate Java process, then run a single backend instance.

### `JAVA_HOME` warning from mvnd
Non-fatal; app still runs. To remove warning, set:
```powershell
setx JAVA_HOME "C:\Program Files\Java\jdk-26.0.1"
setx PATH "$($env:PATH);%JAVA_HOME%\bin"
```

Open a new terminal afterward.

## Report Mapping

See:
- `docs/reengineering-strategy.md`
- `docs/refactoring-log.md`
- `docs/setup.md`

These files map implemented changes to the reengineering/refactoring concepts from the provided course document.
