# Reengineering Strategy (UI Modernization Track)

## Scope Decision
- Keep existing Java + Derby stack and preserve existing business logic.
- Modernize presentation layer by introducing REST API + React frontend.
- Clean code structure incrementally without behavior changes.

## Strategy Mapping (from SRE Mid 2)
- **Partial Approach**: only reengineer UI/access layer now, core rules stay in place.
- **Incremental Approach**: deliver functionality module-by-module (search, borrower profile, circulation, holds, fines).
- **Iterative Approach**: short cycles of refactor -> expose API -> build UI -> validate against legacy behavior.

## Why This Strategy
- Lower risk than Big Bang.
- Faster visible progress for demos and report milestones.
- Enables side-by-side verification with existing console flow.

## Phase Breakdown
1. Setup API wrapper around legacy domain.
2. Build React UI for read flows first.
3. Add write flows (checkout/check-in/renew/holds).
4. Refactor legacy package structure and isolate data access.
5. Add regression tests for high-risk flows.
