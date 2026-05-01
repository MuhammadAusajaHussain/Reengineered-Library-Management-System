# Refactoring Log (Phase 1)

This log explicitly maps implementation changes to the six refactoring categories from the SRE Mid 2 document.

## 1) Composing Methods
- Broke legacy-style monolithic flow into focused services:
  - `AuthService`
  - `BookService`
  - `BorrowerService`
  - `LoanService`
- Each service has single-purpose methods for login, search, checkout, check-in, and renew.

## 2) Moving Features Between Objects
- Moved persistence logic out of service/controller logic into dedicated repositories:
  - `UserRepository`
  - `BookRepository`
  - `LoanRepository`
- Moved password hashing to `PasswordHasher` utility.

## 3) Organizing Data
- Introduced stronger role representation with `Role` enum.
- Introduced dedicated DTOs for each use-case:
  - `LoginRequest`, `AuthResponse`
  - `CreateBookRequest`, `CreateBorrowerRequest`
  - `LoanResultDto`

## 4) Simplifying Conditional Expressions
- Replaced ad-hoc permission checks with central role checks in `AuthService.requireAnyRole(...)`.
- Reduced controller branching by pushing validation to services and bean validation annotations.

## 5) Simplifying Method Calls
- Replaced implicit legacy-style operations with explicit endpoint contracts:
  - `/api/auth/login`
  - `/api/books`, `/api/books/search`
  - `/api/borrowers`, `/api/borrowers/{id}`
  - `/api/loans/checkout`, `/api/loans/checkin`, `/api/loans/renew`

## 6) Dealing with Generalization
- Centralized authorization concerns in `AuthService` and session model (`SessionUser`) so controllers use shared behavior.

## Reengineering Strategy Used in This Phase
- **Partial + Incremental + Iterative (hybrid)**:
  - Partial: introduced a new layered backend path without requiring full immediate rewrite.
  - Incremental: delivered authentication, catalog, borrower, and circulation in one runnable iteration.
  - Iterative: established a stable base to continue evolving role features (holds/fines history/admin workflows).
# Refactoring Log

Use this log to connect each change with refactoring concepts from the provided PDF.

| Date | Area | Technique Category | Before | After | Behavior Change |
|---|---|---|---|---|---|
| 2026-05-01 | UI access path | Composing Methods | Console interactions and orchestration mixed in one flow | Introduced API entry points for discrete use cases | None |
| 2026-05-01 | API boundary | Simplifying Method Calls | Implicit multi-purpose console paths | Explicit HTTP endpoints (`GET /api/books`, `POST /api/books/search`) | None |
| 2026-05-01 | Search filtering | Simplifying Conditional Expressions | Legacy branching style in console flow | Isolated search-mode switch in service function | None |
| 2026-05-01 | Runtime compatibility | Partial + Iterative modernization | Java 8 environment incompatible with Spring Boot 3 | Moved API baseline to Spring Boot 2.7 (Java 8 compatible) | None |
| 2026-05-01 | Circulation flow | Simplifying Method Calls | Checkout/check-in only reachable through console orchestration | Added dedicated endpoints (`POST /api/loans/checkout`, `POST /api/loans/checkin`) | None |
| 2026-05-01 | Borrower access | Moving Features between Objects | Borrower state only printed in console methods | Added service mapping to borrower DTO + web endpoint (`GET /api/borrowers/{id}`) | None |

## Next Entries to Capture
- Move DB calls behind repositories (**Moving Features Between Objects**).
- Introduce value objects for phone/password policy (**Organizing Data**).
- Clean role checks and generalization boundaries (**Dealing with Generalization**).
