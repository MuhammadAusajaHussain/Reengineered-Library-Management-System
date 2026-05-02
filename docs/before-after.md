# Before vs After (Reengineering Comparison)

## Legacy System (Before)
- Console-based interaction only (`Project/` NetBeans structure).
- Mixed responsibilities (UI input, business logic, and DB calls tightly coupled).
- Hard to expose externally (no REST API boundary).
- Role workflows existed but were tied to console navigation.
- Limited observability for web-era debugging and demos.

## Reengineered System (After)
- Web-based architecture with React frontend + Spring Boot API.
- Layered design:
  - Controllers (API boundary)
  - Services (use-case orchestration)
  - Repositories (Derby persistence)
- Persistent embedded Derby DB retained (no destructive stack migration).
- Role-based operations exposed via explicit endpoints.
- Request-id logging + access logs + centralized exception handling.

## Architectural Shift
- **From** monolithic console orchestration
- **To** capability-oriented services with explicit API contracts.

## Capability Delta
- Added:
  - User auth/session endpoints
  - Admin user CRUD
  - Dashboard stats
  - Loan history endpoint
  - Hold lifecycle (`ACTIVE -> READY -> FULFILLED/CANCELLED`)
  - Fine payment endpoint
- Modernized:
  - Catalog operations (create/update/delete/search/list)
  - Borrower registration/profile flows

## Reengineering Approaches Applied
- **Partial**: preserved core domain intent, replaced interaction/persistence boundaries.
- **Incremental**: delivered feature slices one-by-one.
- **Iterative**: repeated implement/validate cycles with runnable outputs.
- **Evolutionary (selective)**: grouped changes by feature capability.

## Risks Reduced
- Lower migration risk than Big Bang.
- Continuous demo readiness after each iteration.
- Easier fault isolation through explicit endpoint boundaries and logs.
