# Setup Instructions

## Backend API
1. Open terminal in `api`.
2. Run:
   - `& "C:\Program Files\Java\maven-mvnd-1.0.5-windows-amd64\bin\mvnd.cmd" spring-boot:run`
3. API base URL:
   - `http://localhost:8080`

## Frontend
1. Open terminal in `client`.
2. Run:
   - `npm run dev`
3. Frontend URL:
   - `http://localhost:5173`

## Notes
- Vite proxy routes `/api/*` to `http://localhost:8080`.
- API currently wraps the legacy `LMS.Library` singleton and exposes book listing/search.
- Additional endpoints now include borrower lookup and checkout/check-in actions.
