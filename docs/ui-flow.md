# UI Flow by User Role

This document explains what each user role can see in the new routed web UI and what actions they can perform from each page.

## Shared Experience (All Users)

- **Login Screen**
  - Sees username/password inputs.
  - Can submit login.
- **Post-login Layout**
  - Sees a persistent left sidebar for navigation.
  - Sees role-specific menu items only.
  - Sees success/error feedback messages at the top of the content area.

## Admin Flow

- **Sidebar Pages**
  - Dashboard
  - Books
  - Circulation
  - Loans
  - Holds
  - Borrowers
  - User Management
- **Can View**
  - Full dashboard metrics.
  - Full book catalog.
  - All active loans and full loan history.
  - Hold requests (including READY holds).
  - Borrower records.
  - All system users.
- **Can Interact**
  - Add, edit, and delete books.
  - Checkout, check-in, and renew books.
  - Place holds and process READY hold checkout.
  - Mark fines as paid.
  - Register borrowers (from both Borrowers and User Management pages).
  - Create, update, and delete users (all roles).
  - Delete borrowers directly from the Borrowers page.

## Librarian Flow

- **Sidebar Pages**
  - Dashboard
  - Books
  - Circulation
  - Loans
  - Holds
  - Borrowers
- **Can View**
  - Dashboard metrics.
  - Full book catalog.
  - All active loans and full loan history.
  - Hold request list.
  - Borrower list.
- **Can Interact**
  - Add, edit, and delete books.
  - Checkout, check-in, and renew books.
  - Place holds and process READY hold checkout.
  - Mark fines as paid.
  - Register borrowers.

## Clerk Flow

- **Sidebar Pages**
  - Dashboard
  - Books
  - Circulation
  - Loans
  - Holds
  - Borrowers
- **Can View**
  - Dashboard metrics.
  - Full book catalog.
  - All active loans and full loan history.
  - Hold request list.
  - Borrower list.
- **Can Interact**
  - Checkout, check-in, and renew books.
  - Place holds and process READY hold checkout.
  - Mark fines as paid.
  - Register borrowers.
- **Cannot Interact**
  - Cannot add, edit, or delete books.
  - Cannot access User Management.

## Borrower Flow

- **Sidebar Pages**
  - Books
  - Circulation
  - Loans
  - Holds
- **Can View**
  - Book search/list.
  - Own borrower profile (via borrower ID lookup).
  - Own active loans and own loan history.
  - Own hold requests.
- **Can Interact**
  - Search books.
  - Request renewals.
  - Place and cancel own holds.
- **Cannot Interact**
  - Cannot access Dashboard.
  - Cannot access Borrowers management page.
  - Cannot access User Management.
  - Cannot perform staff-only checkout/check-in/fine-payment actions.

## Navigation Behavior

- Menu items are conditionally rendered based on authenticated role.
- Default route after login:
  - Staff roles (Admin/Librarian/Clerk): `Dashboard`
  - Borrower: `Books`
- Unknown routes redirect to the same role-appropriate default route.
