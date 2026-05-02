# Feature Matrix (Actor vs Capability)

## Actors
- `ADMIN`
- `LIBRARIAN`
- `CLERK`
- `BORROWER`

## Capabilities

| Capability | ADMIN | LIBRARIAN | CLERK | BORROWER |
|---|---|---|---|---|
| Login | Yes | Yes | Yes | Yes |
| View/search books | Yes | Yes | Yes | Yes |
| Add/update/delete books | Yes | Yes | No | No |
| Register borrower | Yes | Yes | Yes | No |
| Checkout / check-in | Yes | Yes | Yes | No |
| Renew loan | Yes | Yes | Yes | Yes (own) |
| View active loans | Yes | Yes | Yes | Yes (own) |
| View loan history | Yes | Yes | Yes | Yes (own) |
| Pay fine | Yes | Yes | Yes | No |
| Place hold | Yes | Yes | Yes | Yes (own) |
| Cancel hold | Yes | Yes | Yes | Yes (own) |
| Checkout READY hold | Yes | Yes | Yes | No |
| View dashboard stats | Yes | Yes | Yes | No |
| Manage users (create/update/delete/list) | Yes | No | No | No |

## Notes
- Role enforcement is implemented server-side.
- Borrower self-access is constrained to own data for sensitive endpoints.
- Some UI actions are hidden by role, but API remains source of truth for permissions.
