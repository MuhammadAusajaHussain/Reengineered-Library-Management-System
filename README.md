# Library Management System (Reengineered)

A reengineering of a legacy console-based Java LMS into a layered Spring Boot backend and a modern React frontend, while preserving the core library domain workflows.

---

## Repository Structure

```
.
├── api/          # Spring Boot backend (Java 8 target, embedded Apache Derby)
├── client/       # React + Vite frontend
├── Database/     # Auto-created Derby database (generated on first run)
├── Project/      # Original legacy NetBeans OOAD console implementation (reference only)
└── docs/         # Setup, strategy, and refactoring documentation
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 8 or 17 recommended | Java 21+ may cause warnings with Spring Boot 2.7.x |
| Maven / mvnd | Any recent | mvnd 1.0.5 used in development |
| Node.js | 18+ | Required for the React frontend |
| npm | 9+ | Comes with Node.js |

> **No database installation required.** The backend uses an embedded Apache Derby database that is created automatically on first run inside the `Database/` folder.

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/MuhammadAusajaHussain/Reengineered-Library-Management-System.git
cd Reengineered-Library-Management-System
```

### 2. Start the Backend

Using **mvnd**:
```bash
cd api
mvnd spring-boot:run
```

Using standard **Maven**:
```bash
cd api
mvn spring-boot:run
```

The backend starts at: `http://localhost:9000`

> On first startup, the system automatically creates the Derby database and bootstraps the default admin account.

### 3. Start the Frontend

```bash
cd client
npm install      # only needed on first run
npm run dev
```

The frontend starts at: `http://localhost:8080`

---

## Default Admin Credentials

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin` |

The admin account is automatically created/reset on every backend startup via bootstrap properties in `api/src/main/resources/application.properties`. This keeps demo access stable regardless of database state.

---

## Implemented Features

- **Authentication** with role-based access (`ADMIN`, `LIBRARIAN`, `CLERK`, `BORROWER`)
- **Admin user management** — create, update, delete, and list users
- **Book catalog** — list, search, create, update, delete
- **Borrower management** — create, list, get profile
- **Circulation** — checkout, check-in, renew
- **Fine tracking** and fine payment
- **Hold lifecycle**
  - Place hold
  - Cancel hold
  - Auto-mark oldest hold `READY` when a copy is returned
  - Staff checkout of a `READY` hold
- **Active loans** and loan history endpoints
- **Dashboard statistics** for staff roles

---

## Runtime Ports

| Service | URL |
|---------|-----|
| Backend API | `http://localhost:9000` |
| Frontend (Vite) | `http://localhost:8080` |

The frontend proxies all `/api/*` requests to the backend on port `9000`.

---

## Troubleshooting

### Port already in use

If you see `Port 9000 is already in use`, a backend instance is already running. Find and stop it:

**On Windows (PowerShell):**
```powershell
Get-NetTCPConnection -LocalPort 9000 | Select-Object OwningProcess, State
Stop-Process -Id <PID> -Force
```

**On macOS/Linux:**
```bash
lsof -i :9000
kill -9 <PID>
```

Apply the same approach for port `8080` if the frontend fails to start.

---

### Derby database locked

If you see `Another instance of Derby may have already booted the database`, the database file is locked by a previous crashed process.

1. Kill all Java processes:

   **Windows:** `taskkill /F /IM java.exe`  
   **macOS/Linux:** `pkill -f java`

2. Delete the lock file inside the `Database/LMS_REENGINEERED/` folder:
   - `db.lck`
   - `dbex.lck` (if present)

3. Restart the backend.

---

### JAVA_HOME warning (mvnd)

This is non-fatal — the app still runs. To silence it, set `JAVA_HOME` to your JDK installation:

**Windows (permanent):**
```powershell
setx JAVA_HOME "C:\path\to\your\jdk"
```

**macOS/Linux:**
```bash
export JAVA_HOME=/path/to/your/jdk
# Add the above line to ~/.bashrc or ~/.zshrc to make it permanent
```

Open a new terminal after setting this.

---

## Documentation

See the `docs/` folder for:

- `reengineering-strategy.md` — overall reengineering approach and rationale
- `refactoring-log.md` — detailed log of refactoring decisions
- `setup.md` — environment setup and configuration notes

These documents map the implemented changes to reengineering and refactoring concepts from the course material.