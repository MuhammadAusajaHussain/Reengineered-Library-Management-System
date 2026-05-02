# Test Scenarios (Execution + Expected Results)

## Environment
- Backend: `http://localhost:9000`
- Frontend: `http://localhost:8080`
- Default admin: `admin / admin`

## TS-01 Login (Admin)
1. Open frontend.
2. Enter `admin` / `admin`.
3. Click Login.

**Expected**
- Login succeeds.
- Role shown as `ADMIN`.
- Admin user management section is visible.

## TS-02 Create User (Librarian)
1. Login as admin.
2. Create user:
   - username: `librarian2`
   - password: `librarian2`
   - role: `LIBRARIAN`
3. Logout/login with `librarian2`.

**Expected**
- User appears in user list.
- Librarian login succeeds.

## TS-03 Add and Edit Book
1. Login as admin or librarian.
2. Add a book.
3. Edit same book (title/author/subject/total copies).

**Expected**
- Book row reflects updates.
- Validation prevents invalid copy reductions below currently issued count.

## TS-04 Borrower Registration + Checkout
1. Login as staff role.
2. Register borrower.
3. Checkout a book to borrower.

**Expected**
- Borrower profile shows incremented borrowed count.
- Active loan appears in active loans list.

## TS-05 Check-in + Fine
1. Check-in an overdue loan (or manipulate due date during testing).
2. Observe pending fine.
3. Pay fine using loan id.

**Expected**
- Check-in response includes fine amount.
- Fine payment endpoint marks fine paid.

## TS-06 Hold Lifecycle
1. Ensure a book has no available copies.
2. Place hold for borrower.
3. Return a copy of the same book.
4. Observe hold status transitions to `READY`.
5. Staff performs READY hold checkout.

**Expected**
- Hold moves `ACTIVE -> READY -> FULFILLED`.
- Reserved copy behavior maintained.

## TS-07 Loan History
1. Perform checkout/check-in actions.
2. Open loan history view.

**Expected**
- History rows include issue date, due date, return date, and fine paid state.

## TS-08 Unauthorized Access (Negative)
1. Login as borrower.
2. Attempt admin-only or staff-only endpoint action (via UI/API client).

**Expected**
- API returns `403 Forbidden` or role-specific restrictions.

## TS-09 Runtime Conflict Recovery (Operational)
1. Attempt starting backend twice.

**Expected**
- Second instance fails with port/Derby lock.
- After stopping old process, single instance starts successfully.
