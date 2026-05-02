# Refactoring Log

This log maps concrete project changes to refactoring categories from the provided SRE document.

| Date | Area | Technique Category | Before | After | Behavior Change |
|---|---|---|---|---|---|
| 2026-05-01 | Service decomposition | Composing Methods | Large mixed responsibilities | Focused services (`AuthService`, `BookService`, `BorrowerService`, `LoanService`) | None |
| 2026-05-01 | Persistence boundary | Moving Features Between Objects | SQL mixed with orchestration | Dedicated repositories (`UserRepository`, `BookRepository`, `LoanRepository`) | None |
| 2026-05-01 | Request/response contracts | Organizing Data | Primitive-heavy and implicit transport | DTO-based explicit contracts (`LoginRequest`, `AuthResponse`, `LoanResultDto`, etc.) | None |
| 2026-05-01 | Role checks | Simplifying Conditional Expressions | Repeated ad-hoc authorization checks | Centralized `AuthService.requireAnyRole(...)` | None |
| 2026-05-01 | API operations | Simplifying Method Calls | Console-only operation invocation | Explicit REST endpoints for each use case | None |
| 2026-05-02 | User hierarchy handling | Dealing with Generalization | Role handling spread in multiple flows | Unified role enum + shared session user model | None |
| 2026-05-02 | Holds flow | Composing Methods + Simplifying Method Calls | Single hold state and limited transitions | Hold lifecycle (`ACTIVE`, `READY`, `FULFILLED`, `CANCELLED`) + dedicated endpoints | Minor enhancement |
| 2026-05-02 | Catalog maintenance | Moving Features Between Objects | No web-level book mutation safety checks | `BookService` update/delete with invariant checks | Minor enhancement |
| 2026-05-02 | Operational visibility | Organizing Data | Unstructured runtime logging | Request-id filter + structured logback pattern + exception logging | None |
| 2026-05-02 | UI modernizing | Glassmorphism & Vanilla CSS | Standard browser-default styling | High-contrast Dark Mode with Backdrop Blur and Responsive Form Grids | Visual only |
| 2026-05-02 | Data Integrity | Self-repairing inventory | Occasional leaked book availability | Startup reconciliation of total copies vs active loans | Bug fix |
| 2026-05-02 | Catalog logic | Moving Features Between Objects | Multiple cards for same book | Automatic "Merge-on-Create" for duplicate ISBNs/Titles | UX/Bug fix |
| 2026-05-03 | Unified User Mgmt | Moving Features Between Objects | Fragmented creation paths for borrowers | Merged createUser logic handling borrower profiles with conditional UI fields | Better UX |
| 2026-05-03 | Auth & Persistence | Organizing Data | Session loss on refresh | `localStorage` persistence for session user and auth tokens | UX Fix |
| 2026-05-03 | Dashboard UX | Composing Methods | Generic dashboard for all users | Role-specific views with personal history for Borrowers and total loan metrics for Staff | Major UX |

## Strategy Context Used
- **Partial**: core intent preserved, boundaries modernized.
- **Incremental**: capability-by-capability rollout.
- **Iterative**: repeated implement/validate cycles.

## Open Refactoring Opportunities
- Add transaction demarcation for multi-step operations (check-in + hold reservation).
- Introduce domain value objects (phone, username, role policy).
- Add integration tests for role/permission matrix.
