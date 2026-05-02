# Demo Script (Presentation Flow)

## 1) Problem Statement (1 min)
- Legacy LMS was console-bound and hard to evolve.
- Goal: modernize interaction layer while preserving core library operations.

## 2) Strategy (1 min)
- Explain hybrid strategy:
  - Partial + Incremental + Iterative (+ selective Evolutionary grouping).
- Why not Big Bang: risk/time/visibility constraints.

## 3) Architecture Walkthrough (2 min)
- Show modules:
  - `Project/` legacy baseline
  - `api/` layered backend
  - `client/` React UI
  - `docs/` report evidence
- Mention request-id logging and exception handling.

## 4) Live Feature Demo (5-7 min)
1. Login as `admin/admin`.
2. Show dashboard stats.
3. Admin user CRUD (create librarian/clerk/borrower).
4. Catalog flow (add/search/update/delete book).
5. Circulation flow (checkout/check-in/renew).
6. Hold flow (place hold -> return -> READY -> checkout READY hold).
7. Fine flow (pending fine + mark paid).
8. Loan history view.

## 5) Before vs After Evidence (2 min)
- Use `docs/before-after.md` and `docs/feature-matrix.md`.
- Highlight improved modularity, testability, and observability.

## 6) Risks & Mitigations (1 min)
- Runtime conflicts (port/Derby lock) mitigated by single-instance runbook.
- Role/security enforcement centralized server-side.

## 7) Closing (30 sec)
- Reengineering delivered modern frontend, layered backend, and retained domain behavior.
- Next steps: integration tests + schema versioning + deployment hardening.

## Backup Troubleshooting Slide (optional)
- If login fails during demo:
  1. Verify backend health: `http://localhost:9000/api/health`
  2. Ensure frontend is running on `http://localhost:8080`
  3. Ensure only one backend process is running.
