import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'

type Book = {
  id: number
  isbn: string
  title: string
  author: string
  subject: string
  issued: boolean
  totalCopies: number
  availableCopies: number
}

type Borrower = {
  id: number
  name: string
  address: string
  phoneNumber: string
  borrowedBooksCount: number
  onHoldBooksCount: number
}

type User = {
  userId: number
  username: string
  fullName: string
  role: 'ADMIN' | 'LIBRARIAN' | 'CLERK' | 'BORROWER'
}

type ActiveLoan = {
  loanId: number
  borrowerId: number
  bookId: number
  bookTitle: string
  dueDate: string
  pendingFine: number
  finePaid: boolean
}

type HoldItem = {
  id: number
  bookId: number
  bookTitle: string
  requestDate: string
  status: string
}

type DashboardStats = {
  totalBooks: number
  activeLoans: number
  overdueUnpaidLoans: number
  activeHolds: number
  totalBorrowers: number
}

type LoanHistory = {
  loanId: number
  borrowerId: number
  bookId: number
  bookTitle: string
  issueDate: string
  dueDate: string
  returnDate: string | null
  finePaid: boolean
}

function App() {
  const [token, setToken] = useState('')
  const [user, setUser] = useState<User | null>(null)
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin')

  const [books, setBooks] = useState<Book[]>([])
  const [borrowers, setBorrowers] = useState<Borrower[]>([])
  const [borrower, setBorrower] = useState<Borrower | null>(null)
  const [searchBy, setSearchBy] = useState<'title' | 'author' | 'subject'>('title')
  const [query, setQuery] = useState('')
  const [borrowerId, setBorrowerId] = useState('')
  const [bookId, setBookId] = useState('')
  const [newBorrowerUsername, setNewBorrowerUsername] = useState('')
  const [newBorrowerPassword, setNewBorrowerPassword] = useState('')
  const [newBorrowerName, setNewBorrowerName] = useState('')
  const [newBorrowerAddress, setNewBorrowerAddress] = useState('')
  const [newBorrowerPhone, setNewBorrowerPhone] = useState('')
  const [newBookTitle, setNewBookTitle] = useState('')
  const [newBookAuthor, setNewBookAuthor] = useState('')
  const [newBookSubject, setNewBookSubject] = useState('')
  const [newBookIsbn, setNewBookIsbn] = useState('')
  const [newBookCopies, setNewBookCopies] = useState('1')
  const [activeLoans, setActiveLoans] = useState<ActiveLoan[]>([])
  const [holds, setHolds] = useState<HoldItem[]>([])
  const [dashboardStats, setDashboardStats] = useState<DashboardStats | null>(null)
  const [fineLoanId, setFineLoanId] = useState('')
  const [loanHistory, setLoanHistory] = useState<LoanHistory[]>([])
  const [editBookId, setEditBookId] = useState('')
  const [editBookIsbn, setEditBookIsbn] = useState('')
  const [editBookTitle, setEditBookTitle] = useState('')
  const [editBookAuthor, setEditBookAuthor] = useState('')
  const [editBookSubject, setEditBookSubject] = useState('')
  const [editBookTotalCopies, setEditBookTotalCopies] = useState('1')
  const [deleteBookId, setDeleteBookId] = useState('')
  const [users, setUsers] = useState<Array<{ id: number; username: string; fullName: string; role: string; active: boolean }>>([])
  const [newUserUsername, setNewUserUsername] = useState('')
  const [newUserPassword, setNewUserPassword] = useState('')
  const [newUserFullName, setNewUserFullName] = useState('')
  const [newUserRole, setNewUserRole] = useState<'ADMIN' | 'LIBRARIAN' | 'CLERK' | 'BORROWER'>('CLERK')
  const [newUserActive, setNewUserActive] = useState(true)
  const [editUserId, setEditUserId] = useState('')
  const [editUserFullName, setEditUserFullName] = useState('')
  const [editUserRole, setEditUserRole] = useState<'ADMIN' | 'LIBRARIAN' | 'CLERK' | 'BORROWER'>('CLERK')
  const [editUserActive, setEditUserActive] = useState(true)
  const [editUserPassword, setEditUserPassword] = useState('')
  const [deleteUserId, setDeleteUserId] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [message, setMessage] = useState<string | null>(null)

  const canManageBooks = useMemo(() => user?.role === 'ADMIN' || user?.role === 'LIBRARIAN', [user])
  const canManageLoans = useMemo(() => user?.role === 'ADMIN' || user?.role === 'LIBRARIAN' || user?.role === 'CLERK', [user])
  const isAdmin = useMemo(() => user?.role === 'ADMIN', [user])

  useEffect(() => {
    if (!token) return
    void loadAllBooks()
    void loadBorrowers()
    void loadActiveLoans()
    void loadLoanHistory()
    void loadDashboardStats()
    void loadUsers()
  }, [token])

  async function login(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Login failed')
      }
      const data = (await response.json()) as { token: string; userId: number; username: string; fullName: string; role: User['role'] }
      setToken(data.token)
      setUser({
        userId: data.userId,
        username: data.username,
        fullName: data.fullName,
        role: data.role,
      })
      setMessage(`Logged in as ${data.fullName} (${data.role})`)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  function authHeaders(): Record<string, string> {
    if (!token) {
      return {}
    }
    return { Authorization: `Bearer ${token}` }
  }

  async function loadAllBooks() {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/books', {
        headers: authHeaders(),
      })
      if (!response.ok) throw new Error('Failed to load books')
      const data: Book[] = await response.json()
      setBooks(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function onSearchSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!query.trim()) return

    setLoading(true)
    setError(null)
    setMessage(null)

    try {
      const response = await fetch('/api/books/search', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({ searchBy, query }),
      })
      if (!response.ok) throw new Error('Search request failed')
      const data: Book[] = await response.json()
      setBooks(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function loadBorrower() {
    if (!borrowerId.trim()) return
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/borrowers/${borrowerId}`, {
        headers: authHeaders(),
      })
      if (!response.ok) throw new Error('Borrower not found')
      const data: Borrower = await response.json()
      setBorrower(data)
      setMessage('Borrower loaded successfully.')
    } catch (err) {
      setBorrower(null)
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function loadBorrowers() {
    if (!token) return
    try {
      const response = await fetch('/api/borrowers', { headers: authHeaders() })
      if (!response.ok) return
      const data: Borrower[] = await response.json()
      setBorrowers(data)
    } catch {
      // keep UI resilient if role doesn't allow this endpoint
    }
  }

  async function loadActiveLoans() {
    if (!token) return
    try {
      const response = await fetch('/api/loans/active', {
        headers: authHeaders(),
      })
      if (!response.ok) return
      const data: ActiveLoan[] = await response.json()
      setActiveLoans(data)
    } catch {
      // no-op
    }
  }

  async function loadDashboardStats() {
    if (!token || !canManageLoans) return
    try {
      const response = await fetch('/api/dashboard/stats', {
        headers: authHeaders(),
      })
      if (!response.ok) return
      const data: DashboardStats = await response.json()
      setDashboardStats(data)
    } catch {
      // no-op
    }
  }

  async function loadLoanHistory() {
    if (!token) return
    try {
      const response = await fetch('/api/loans/history', { headers: authHeaders() })
      if (!response.ok) return
      const data: LoanHistory[] = await response.json()
      setLoanHistory(data)
    } catch {
      // no-op
    }
  }

  async function loadUsers() {
    if (!token || !isAdmin) return
    try {
      const response = await fetch('/api/users', { headers: authHeaders() })
      if (!response.ok) return
      const data = (await response.json()) as Array<{ id: number; username: string; fullName: string; role: string; active: boolean }>
      setUsers(data)
    } catch {
      // no-op
    }
  }

  async function createUser(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          username: newUserUsername,
          password: newUserPassword,
          fullName: newUserFullName,
          role: newUserRole,
          active: newUserActive,
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to create user')
      }
      setMessage('User created successfully')
      setNewUserUsername('')
      setNewUserPassword('')
      setNewUserFullName('')
      setNewUserRole('CLERK')
      setNewUserActive(true)
      await loadUsers()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function updateUser() {
    if (!editUserId.trim()) return
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/users/${Number(editUserId)}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          fullName: editUserFullName || undefined,
          role: editUserRole,
          active: editUserActive,
          password: editUserPassword || undefined,
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to update user')
      }
      setMessage('User updated successfully')
      setEditUserId('')
      setEditUserFullName('')
      setEditUserPassword('')
      await loadUsers()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function deleteUser() {
    if (!deleteUserId.trim()) return
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/users/${Number(deleteUserId)}`, {
        method: 'DELETE',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to delete user')
      }
      setMessage('User deleted successfully')
      setDeleteUserId('')
      await loadUsers()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function processLoan(action: 'checkout' | 'checkin') {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/loans/${action}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          borrowerId: Number(borrowerId),
          bookId: Number(bookId),
        }),
      })

      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? `Failed to ${action} book`)
      }

      const result = (await response.json()) as { message: string; fineAmount: number }
      setMessage(`${result.message}${result.fineAmount > 0 ? ` | Fine: Rs ${result.fineAmount}` : ''}`)
      await loadAllBooks()
      await loadBorrower()
      await loadActiveLoans()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function renewLoan() {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/loans/renew', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          borrowerId: Number(borrowerId),
          bookId: Number(bookId),
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to renew loan')
      }
      const result = (await response.json()) as { message: string }
      setMessage(result.message)
      await loadActiveLoans()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function placeHold() {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/holds?borrowerId=${Number(borrowerId)}&bookId=${Number(bookId)}`, {
        method: 'POST',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to place hold')
      }
      const data = (await response.json()) as HoldItem
      setMessage(`Hold created for ${data.bookTitle}`)
      await loadHolds()
      await loadBorrower()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function loadHolds() {
    if (!borrowerId.trim()) return
    try {
      const response = await fetch(`/api/holds?borrowerId=${Number(borrowerId)}`, {
        headers: authHeaders(),
      })
      if (!response.ok) return
      const data: HoldItem[] = await response.json()
      setHolds(data)
    } catch {
      // no-op
    }
  }

  async function checkoutReadyHold(holdId: number) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/holds/${holdId}/checkout`, {
        method: 'POST',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to checkout READY hold')
      }
      const result = (await response.json()) as { message: string; fineAmount: number }
      setMessage(result.message)
      await loadAllBooks()
      await loadHolds()
      await loadActiveLoans()
      await loadLoanHistory()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function payFine() {
    if (!fineLoanId.trim()) return
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/loans/pay-fine', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({ loanId: Number(fineLoanId) }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to pay fine')
      }
      const result = (await response.json()) as { message: string; fineAmount: number }
      setMessage(`${result.message}${result.fineAmount ? ` | Paid: Rs ${result.fineAmount}` : ''}`)
      await loadActiveLoans()
      await loadLoanHistory()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function updateBook() {
    if (!editBookId.trim()) return
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/books/${Number(editBookId)}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          isbn: editBookIsbn || null,
          title: editBookTitle,
          author: editBookAuthor,
          subject: editBookSubject,
          totalCopies: Number(editBookTotalCopies),
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to update book')
      }
      setMessage('Book updated successfully')
      setEditBookId('')
      setEditBookIsbn('')
      setEditBookTitle('')
      setEditBookAuthor('')
      setEditBookSubject('')
      setEditBookTotalCopies('1')
      await loadAllBooks()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function deleteBook() {
    if (!deleteBookId.trim()) return
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/books/${Number(deleteBookId)}`, {
        method: 'DELETE',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to delete book')
      }
      setMessage('Book deleted successfully')
      setDeleteBookId('')
      await loadAllBooks()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function createBorrower(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/borrowers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          username: newBorrowerUsername,
          password: newBorrowerPassword,
          fullName: newBorrowerName,
          address: newBorrowerAddress,
          phoneNo: newBorrowerPhone,
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to create borrower')
      }
      const created: Borrower = await response.json()
      setMessage(`Borrower created with ID ${created.id}`)
      setNewBorrowerUsername('')
      setNewBorrowerPassword('')
      setNewBorrowerName('')
      setNewBorrowerAddress('')
      setNewBorrowerPhone('')
      await loadBorrowers()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function createBook(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          isbn: newBookIsbn,
          title: newBookTitle,
          author: newBookAuthor,
          subject: newBookSubject,
          copies: Number(newBookCopies),
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to create book')
      }
      const created: Book = await response.json()
      setMessage(`Book created with ID ${created.id}`)
      setNewBookTitle('')
      setNewBookAuthor('')
      setNewBookSubject('')
      setNewBookIsbn('')
      setNewBookCopies('1')
      await loadAllBooks()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  if (!token || !user) {
    return (
      <main className="page">
        <header className="page-header">
          <h1>Library Management System</h1>
          <p>Reengineered web frontend with role-based login</p>
        </header>
        <section className="card">
          <h2>Login</h2>
          <form onSubmit={login} className="search-form">
            <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" />
            <input
              value={password}
              type="password"
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
            />
            <button type="submit">Login</button>
          </form>
          <p className="hint">
            Demo users: <code>admin/admin</code>, <code>librarian1/librarian</code>, <code>clerk1/clerk</code>, <code>borrower1/borrower</code>
          </p>
          {loading && <p>Loading...</p>}
          {error && <p className="error">{error}</p>}
        </section>
      </main>
    )
  }

  return (
    <main className="page">
      <header className="page-header">
        <h1>Library Management System</h1>
        <p>Welcome {user.fullName} ({user.role})</p>
      </header>

      {dashboardStats && (
        <section className="card">
          <h2>Dashboard</h2>
          <div className="stats-grid">
            <p>Total Books: <strong>{dashboardStats.totalBooks}</strong></p>
            <p>Active Loans: <strong>{dashboardStats.activeLoans}</strong></p>
            <p>Overdue Unpaid Loans: <strong>{dashboardStats.overdueUnpaidLoans}</strong></p>
            <p>Active Holds: <strong>{dashboardStats.activeHolds}</strong></p>
            <p>Total Borrowers: <strong>{dashboardStats.totalBorrowers}</strong></p>
          </div>
        </section>
      )}

      {isAdmin && (
        <section className="card">
          <h2>Admin: User Management</h2>

          <h3>Create user</h3>
          <form onSubmit={createUser} className="actions-grid">
            <input value={newUserUsername} onChange={(e) => setNewUserUsername(e.target.value)} placeholder="Username" />
            <input value={newUserPassword} type="password" onChange={(e) => setNewUserPassword(e.target.value)} placeholder="Password" />
            <input value={newUserFullName} onChange={(e) => setNewUserFullName(e.target.value)} placeholder="Full name" />
            <select value={newUserRole} onChange={(e) => setNewUserRole(e.target.value as any)}>
              <option value="ADMIN">ADMIN</option>
              <option value="LIBRARIAN">LIBRARIAN</option>
              <option value="CLERK">CLERK</option>
              <option value="BORROWER">BORROWER</option>
            </select>
            <label className="checkbox">
              <input type="checkbox" checked={newUserActive} onChange={(e) => setNewUserActive(e.target.checked)} />
              Active
            </label>
            <button type="submit">Create</button>
          </form>

          <h3>Update user</h3>
          <div className="actions-grid">
            <input value={editUserId} onChange={(e) => setEditUserId(e.target.value)} placeholder="User ID" />
            <input value={editUserFullName} onChange={(e) => setEditUserFullName(e.target.value)} placeholder="New full name (optional)" />
            <input value={editUserPassword} type="password" onChange={(e) => setEditUserPassword(e.target.value)} placeholder="New password (optional)" />
            <select value={editUserRole} onChange={(e) => setEditUserRole(e.target.value as any)}>
              <option value="ADMIN">ADMIN</option>
              <option value="LIBRARIAN">LIBRARIAN</option>
              <option value="CLERK">CLERK</option>
              <option value="BORROWER">BORROWER</option>
            </select>
            <label className="checkbox">
              <input type="checkbox" checked={editUserActive} onChange={(e) => setEditUserActive(e.target.checked)} />
              Active
            </label>
            <button type="button" onClick={() => void updateUser()}>Update</button>
          </div>

          <h3>Delete user</h3>
          <div className="actions-grid">
            <input value={deleteUserId} onChange={(e) => setDeleteUserId(e.target.value)} placeholder="User ID" />
            <button type="button" onClick={() => void deleteUser()}>Delete</button>
          </div>

          <h3>All users</h3>
          <div className="borrower-list">
            {users.map((u) => (
              <p key={u.id}>
                #{u.id} | {u.username} | {u.fullName} | {u.role} | {u.active ? 'Active' : 'Inactive'}
              </p>
            ))}
            {users.length === 0 && <p>No users loaded.</p>}
          </div>
        </section>
      )}

      <section className="card">
        <form onSubmit={onSearchSubmit} className="search-form">
          <select value={searchBy} onChange={(event) => setSearchBy(event.target.value as 'title' | 'author' | 'subject')}>
            <option value="title">Title</option>
            <option value="author">Author</option>
            <option value="subject">Subject</option>
          </select>
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder={`Search by ${searchBy}`}
          />
          <button type="submit">Search</button>
          <button type="button" onClick={() => void loadAllBooks()}>Reset</button>
        </form>

        {loading && <p>Loading...</p>}
        {error && <p className="error">{error}</p>}

        {!loading && !error && (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>ISBN</th>
                <th>Title</th>
                <th>Author</th>
                <th>Subject</th>
                <th>Copies</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {books.map((book) => (
                <tr key={book.id}>
                  <td>{book.id}</td>
                  <td>{book.isbn || '-'}</td>
                  <td>{book.title}</td>
                  <td>{book.author}</td>
                  <td>{book.subject}</td>
                  <td>{book.availableCopies}/{book.totalCopies}</td>
                  <td>{book.issued ? 'Issued' : 'Available'}</td>
                </tr>
              ))}
              {books.length === 0 && (
                <tr>
                  <td colSpan={7}>No books found.</td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </section>

      {canManageBooks && (
        <section className="card">
          <h2>Add Book</h2>
          <form onSubmit={createBook} className="actions-grid">
            <input value={newBookIsbn} onChange={(e) => setNewBookIsbn(e.target.value)} placeholder="ISBN (optional)" />
            <input value={newBookTitle} onChange={(e) => setNewBookTitle(e.target.value)} placeholder="Title" />
            <input value={newBookAuthor} onChange={(e) => setNewBookAuthor(e.target.value)} placeholder="Author" />
            <input value={newBookSubject} onChange={(e) => setNewBookSubject(e.target.value)} placeholder="Subject" />
            <input value={newBookCopies} onChange={(e) => setNewBookCopies(e.target.value)} placeholder="Copies" />
            <button type="submit">Add Book</button>
          </form>
        </section>
      )}

      {canManageBooks && (
        <section className="card">
          <h2>Edit / Delete Book</h2>
          <div className="actions-grid">
            <input value={editBookId} onChange={(e) => setEditBookId(e.target.value)} placeholder="Book ID" />
            <input value={editBookIsbn} onChange={(e) => setEditBookIsbn(e.target.value)} placeholder="ISBN (optional)" />
            <input value={editBookTitle} onChange={(e) => setEditBookTitle(e.target.value)} placeholder="Title" />
            <input value={editBookAuthor} onChange={(e) => setEditBookAuthor(e.target.value)} placeholder="Author" />
            <input value={editBookSubject} onChange={(e) => setEditBookSubject(e.target.value)} placeholder="Subject" />
            <input value={editBookTotalCopies} onChange={(e) => setEditBookTotalCopies(e.target.value)} placeholder="Total copies" />
            <button type="button" onClick={() => void updateBook()}>Update Book</button>
          </div>
          <div className="actions-grid">
            <input value={deleteBookId} onChange={(e) => setDeleteBookId(e.target.value)} placeholder="Book ID to delete" />
            <button type="button" onClick={() => void deleteBook()}>Delete Book</button>
          </div>
        </section>
      )}

      <section className="card">
        <h2>Borrower & Circulation</h2>
        <div className="actions-grid">
          <input
            value={borrowerId}
            onChange={(event) => setBorrowerId(event.target.value)}
            placeholder="Borrower ID"
          />
          <input
            value={bookId}
            onChange={(event) => setBookId(event.target.value)}
            placeholder="Book ID"
          />
          <button type="button" onClick={() => void loadBorrower()}>Load Borrower</button>
          <button type="button" onClick={() => void loadHolds()}>Load Holds</button>
          {canManageLoans && <button type="button" onClick={() => void processLoan('checkout')}>Checkout</button>}
          {canManageLoans && <button type="button" onClick={() => void processLoan('checkin')}>Check-In</button>}
          <button type="button" onClick={() => void renewLoan()}>Renew</button>
          <button type="button" onClick={() => void placeHold()}>Place Hold</button>
        </div>

        {borrower && (
          <div className="borrower-box">
            <strong>{borrower.name}</strong>
            <p>ID: {borrower.id}</p>
            <p>Address: {borrower.address}</p>
            <p>Phone: {borrower.phoneNumber}</p>
            <p>Borrowed Books: {borrower.borrowedBooksCount}</p>
            <p>Hold Requests: {borrower.onHoldBooksCount}</p>
          </div>
        )}

        {message && <p className="message">{message}</p>}
      </section>

      <section className="card">
        <h2>Active Loans & Fine Payment</h2>
        <div className="actions-grid">
          <input value={fineLoanId} onChange={(e) => setFineLoanId(e.target.value)} placeholder="Loan ID for fine payment" />
          {canManageLoans && <button type="button" onClick={() => void payFine()}>Mark Fine Paid</button>}
        </div>
        {activeLoans.length > 0 ? (
          <div className="borrower-list">
            {activeLoans.map((loan) => (
              <p key={loan.loanId}>
                Loan #{loan.loanId} | Borrower #{loan.borrowerId} | {loan.bookTitle} | Due: {loan.dueDate.slice(0, 10)} | Pending fine: Rs {loan.pendingFine} | Fine paid: {loan.finePaid ? 'Yes' : 'No'}
              </p>
            ))}
          </div>
        ) : <p>No active loans.</p>}
      </section>

      <section className="card">
        <h2>Hold Requests</h2>
        {holds.length > 0 ? (
          <div className="borrower-list">
            {holds.map((hold) => (
              <p key={hold.id}>
                Hold #{hold.id} | Book #{hold.bookId} ({hold.bookTitle}) | Requested: {hold.requestDate.slice(0, 10)} | {hold.status}
                {canManageLoans && hold.status === 'READY' && (
                  <>
                    {' '}| <button type="button" onClick={() => void checkoutReadyHold(hold.id)}>Checkout READY Hold</button>
                  </>
                )}
              </p>
            ))}
          </div>
        ) : <p>No hold requests loaded.</p>}
      </section>

      <section className="card">
        <h2>Loan History</h2>
        {loanHistory.length > 0 ? (
          <div className="borrower-list">
            {loanHistory.map((loan) => (
              <p key={loan.loanId}>
                Loan #{loan.loanId} | Borrower #{loan.borrowerId} | {loan.bookTitle} | Issued: {loan.issueDate.slice(0, 10)} | Due: {loan.dueDate.slice(0, 10)} | Returned: {loan.returnDate ? loan.returnDate.slice(0, 10) : '-'} | Fine paid: {loan.finePaid ? 'Yes' : 'No'}
              </p>
            ))}
          </div>
        ) : <p>No loan history.</p>}
      </section>

      {canManageLoans && (
        <section className="card">
          <h2>Register Borrower</h2>
          <form onSubmit={createBorrower} className="actions-grid">
            <input value={newBorrowerUsername} onChange={(e) => setNewBorrowerUsername(e.target.value)} placeholder="Username" />
            <input
              value={newBorrowerPassword}
              type="password"
              onChange={(e) => setNewBorrowerPassword(e.target.value)}
              placeholder="Password"
            />
            <input value={newBorrowerName} onChange={(e) => setNewBorrowerName(e.target.value)} placeholder="Full Name" />
            <input value={newBorrowerAddress} onChange={(e) => setNewBorrowerAddress(e.target.value)} placeholder="Address" />
            <input value={newBorrowerPhone} onChange={(e) => setNewBorrowerPhone(e.target.value)} placeholder="Phone" />
            <button type="submit">Create Borrower</button>
          </form>

          {borrowers.length > 0 && (
            <div className="borrower-list">
              <h3>All Borrowers</h3>
              {borrowers.map((item) => (
                <p key={item.id}>
                  #{item.id} - {item.name} ({item.phoneNumber}) | Borrowed: {item.borrowedBooksCount}
                </p>
              ))}
            </div>
          )}
        </section>
      )}
    </main>
  )
}

export default App
