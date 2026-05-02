# Reengineering Strategy (Modernization Track)

## Scope Decision
- Preserve legacy domain intent and core LMS workflows.
- Move from console-driven interaction to layered web architecture.
- Keep Java + Derby stack to reduce migration risk and ensure continuity.

## Strategy Mapping (from SRE Mid 2)
- **Partial Approach**: preserve core business rules while replacing interaction/data-access boundaries.
- **Incremental Approach**: deliver modules in slices (auth, books, borrowers, circulation, holds, fines, admin).
- **Iterative Approach**: implement-refactor-validate cycles with running software after each iteration.
- **Evolutionary Approach (applied selectively)**: group and modernize features by capability (circulation, user/role management, catalog).

## Why this hybrid strategy
- Avoids Big Bang risk and allows continuous demos.
- Supports side-by-side behavior verification.
- Reduces production/debug complexity by limiting concurrent unknowns.

## Current Layered Architecture
- **API**: controllers, validation, exception mapping.
- **Application/Service**: use-case orchestration, role checks, workflow rules.
- **Infrastructure**: repositories over Derby JDBC, session store.
- **Client**: React UI consuming `/api/*`.

## Completed Capability Slices
1. Auth + sessions + role checks.
2. Catalog search/list + create.
3. Borrower profile + registration.
4. Circulation: checkout/check-in/renew.
5. Fines: calculation + pay fine.
6. Holds: place/cancel + READY reservation + READY checkout.
7. Admin: user CRUD.
8. Observability: request-id logging + access logs.

## Next Strategy-Aligned Steps
1. Add regression tests for circulation and hold workflows.
2. Harden transactional boundaries around multi-step DB updates.
3. Add migration/versioning tool for schema evolution.
4. Produce before/after evidence tables for report (class responsibilities, dependency direction, cyclomatic complexity).
