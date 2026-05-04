import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { NavLink, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import type { ActiveLoan, Book, Borrower, DashboardStats, HoldItem, LoanHistory, ManagedUser, Role, User } from './types'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import BooksPage from './pages/BooksPage'
import BookFormPage from './pages/BookFormPage'
import BorrowersPage from './pages/BorrowersPage'
import BorrowerFormPage from './pages/BorrowerFormPage'
import UsersPage from './pages/UsersPage'
import UserFormPage from './pages/UserFormPage'
import CirculationPage from './pages/CirculationPage'
import LoansPage from './pages/LoansPage'
import HoldsPage from './pages/HoldsPage'

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('lms_token') || '')
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem('lms_user')
    return stored ? JSON.parse(stored) : null
  })
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [books, setBooks] = useState<Book[]>([])
  const [borrowers, setBorrowers] = useState<Borrower[]>([])
  const [borrower, setBorrower] = useState<Borrower | null>(null)
  const [searchBy, setSearchBy] = useState<'title' | 'author' | 'subject'>('title')
  const [query, setQuery] = useState('')
  const [borrowerId, setBorrowerId] = useState(() => {
    const stored = localStorage.getItem('lms_user')
    if (stored) {
      const u = JSON.parse(stored)
      if (u.role === 'BORROWER') return u.userId.toString()
    }
    return ''
  })
  const [bookId, setBookId] = useState('')
  const [activeLoans, setActiveLoans] = useState<ActiveLoan[]>([])
  const [holds, setHolds] = useState<HoldItem[]>([])
  const [dashboardStats, setDashboardStats] = useState<DashboardStats | null>(null)
  const [fineLoanId, setFineLoanId] = useState('')
  const [loanHistory, setLoanHistory] = useState<LoanHistory[]>([])
  const [users, setUsers] = useState<ManagedUser[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [message, setMessage] = useState<string | null>(null)

  const canManageBooks = useMemo(() => user?.role === 'ADMIN' || user?.role === 'LIBRARIAN', [user])
  const canManageLoans = useMemo(() => user?.role === 'ADMIN' || user?.role === 'LIBRARIAN' || user?.role === 'CLERK', [user])
  const isAdmin = useMemo(() => user?.role === 'ADMIN', [user])

  const canSeeDashboard = useMemo(() => !!user, [user])

  const location = useLocation()

  useEffect(() => {
    if (!token) return
    void loadAllBooks()
    void loadBorrowers()
    void loadActiveLoans()
    void loadLoanHistory()
    void loadDashboardStats()
    void loadUsers()
    void loadHolds()
  }, [token, user?.role, borrowerId])

  useEffect(() => {
    if (token && location.pathname === '/dashboard') {
      void loadDashboardStats()
    }
  }, [location.pathname, token])

  async function login(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const normalizedUsername = username.trim()
      const normalizedPassword = password.trim()
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: normalizedUsername, password: normalizedPassword }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        const fallbackText = await response.text().catch(() => '')
        const errorMessage =
          payload?.error ??
          payload?.message ??
          (fallbackText ? fallbackText : `Login failed (HTTP ${response.status})`)
        throw new Error(errorMessage)
      }
      const data = (await response.json()) as { token: string; userId: number; username: string; fullName: string; role: User['role'] }
      const sessionUser = {
        userId: data.userId,
        username: data.username,
        fullName: data.fullName,
        role: data.role,
      }
      setToken(data.token)
      setUser(sessionUser)
      localStorage.setItem('lms_token', data.token)
      localStorage.setItem('lms_user', JSON.stringify(sessionUser))
      if (data.role === 'BORROWER') {
        setBorrowerId(data.userId.toString())
      }
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
      if (response.status === 401) return logout()
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
      if (response.status === 401) return logout()
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
      if (response.status === 401) return logout()
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
    if (!token) return
    try {
      const response = await fetch('/api/dashboard/stats', {
        headers: authHeaders(),
      })
      if (response.status === 401) return logout()
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
      const data = (await response.json()) as ManagedUser[]
      setUsers(data)
    } catch {
      // no-op
    }
  }

  async function createUser(payload: {
    username: string;
    password: string;
    fullName: string;
    role: Role;
    active: boolean;
    address?: string;
    phoneNo?: string;
  }) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          username: payload.username,
          password: payload.password,
          fullName: payload.fullName,
          role: payload.role,
          active: payload.active,
          address: payload.address,
          phoneNo: payload.phoneNo,
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to create user')
      }
      setMessage('User created successfully')
      await loadUsers()
      await loadBorrowers()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function updateUser(userId: number, payload: { fullName?: string; role: Role; active: boolean; password?: string }) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/users/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify(payload),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to update user')
      }
      setMessage('User updated successfully')
      await loadUsers()
      await loadBorrowers()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function deleteUser(userId: number) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/users/${userId}`, {
        method: 'DELETE',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to delete user')
      }
      setMessage('User deleted successfully')
      await loadUsers()
      await loadBorrowers()
      await loadDashboardStats()
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

      if (response.status === 401) return logout()
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
      await loadDashboardStats()
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

  async function deleteHold(holdId: number) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/holds/${holdId}/admin`, {
        method: 'DELETE',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to delete hold')
      }
      setMessage('Hold request deleted successfully')
      await loadHolds()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function loadHolds() {
    if (!token) return
    try {
      const isStaff = user?.role === 'ADMIN' || user?.role === 'LIBRARIAN' || user?.role === 'CLERK'
      // For staff, we use the /all endpoint. For borrowers, we use the borrowerId query param.
      const url = isStaff ? '/api/holds/all' : `/api/holds?borrowerId=${Number(borrowerId)}`

      if (!isStaff && !borrowerId) return

      const response = await fetch(url, {
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

  async function updateBook(bookIdToUpdate: number, payload: { isbn: string; title: string; author: string; subject: string; totalCopies: number }) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/books/${bookIdToUpdate}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify(payload),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to update book')
      }
      setMessage('Book updated successfully')
      await loadAllBooks()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function deleteBook(bookIdToDelete: number) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/books/${bookIdToDelete}`, {
        method: 'DELETE',
        headers: authHeaders(),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to delete book')
      }
      setMessage('Book deleted successfully')
      await loadAllBooks()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function createBorrower(payload: { username: string; password: string; fullName: string; address: string; phoneNo: string }) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/borrowers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          username: payload.username,
          password: payload.password,
          fullName: payload.fullName,
          address: payload.address,
          phoneNo: payload.phoneNo,
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to create borrower')
      }
      const created: Borrower = await response.json()
      setMessage(`Borrower created with ID ${created.id}`)
      await loadBorrowers()
      await loadUsers()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function createBook(payload: { isbn: string; title: string; author: string; subject: string; copies: number }) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify({
          isbn: payload.isbn,
          title: payload.title,
          author: payload.author,
          subject: payload.subject,
          copies: payload.copies,
        }),
      })
      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? 'Failed to create book')
      }
      const created: Book = await response.json()
      setMessage(`Book created with ID ${created.id}`)
      await loadAllBooks()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function updateBorrower(borrowerIdToUpdate: number, payload: { fullName: string; address: string; phoneNo: string }) {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/borrowers/${borrowerIdToUpdate}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...authHeaders() },
        body: JSON.stringify(payload),
      })
      if (!response.ok) {
        const body = await response.json().catch(() => null)
        throw new Error(body?.error ?? 'Failed to update borrower')
      }
      setMessage('Borrower updated successfully')
      await loadBorrowers()
      await loadUsers()
      await loadDashboardStats()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  async function deleteBorrower(id: number) {
    if (!window.confirm('Are you sure you want to delete this borrower? This will also delete their user account and all related data.')) return
    await deleteUser(id)
  }

  function logout() {
    setToken('')
    setUser(null)
    setBorrowerId('')
    setBooks([])
    setBorrowers([])
    setDashboardStats(null)
    setLoanHistory([])
    setUsers([])
    setHolds([])
    setActiveLoans([])
    setPassword('')
    setError(null)
    setMessage(null)
    localStorage.removeItem('lms_token')
    localStorage.removeItem('lms_user')
  }

  if (!token || !user) {
    return <LoginPage username={username} password={password} loading={loading} error={error} setUsername={setUsername} setPassword={setPassword} login={login} />
  }

  function routeClassName({ isActive }: { isActive: boolean }) {
    return isActive ? 'sidebar-link active' : 'sidebar-link'
  }

  return (
    <main className="layout">
      <aside className="sidebar">
        <h2>LMS</h2>
        <div className="user-info">
          <p>{user.fullName}</p>
          <p className="role">{user.role}</p>
        </div>
        <nav className="sidebar-nav">
          {canSeeDashboard && <NavLink to="/dashboard" className={routeClassName}>Dashboard</NavLink>}
          <NavLink to="/books" className={routeClassName}>Books</NavLink>
          {canManageLoans && <NavLink to="/circulation" className={routeClassName}>Circulation</NavLink>}
          <NavLink to="/loans" className={routeClassName}>Loans</NavLink>
          <NavLink to="/holds" className={routeClassName}>Holds</NavLink>
          {canManageLoans && <NavLink to="/borrowers" className={routeClassName}>Borrowers</NavLink>}
          {isAdmin && <NavLink to="/users" className={routeClassName}>User Management</NavLink>}
          <button type="button" onClick={logout}>Logout</button>
        </nav>
      </aside>

      <section className="content">
        <header className="page-header">
          <h1>Library Management System</h1>
          {error && <p className="error">{error}</p>}
          {message && <p className="message">{message}</p>}
          {loading && <p>Loading...</p>}
        </header>

        <Routes>
          {canSeeDashboard && (
            <Route
              path="/dashboard"
              element={
                <DashboardPage
                  stats={dashboardStats}
                  isAdmin={isAdmin}
                  role={user!.role}
                  activeLoans={loanHistory.filter(l => !l.returnDate)}
                  loanHistory={loanHistory}
                />
              }
            />
          )}
          <Route path="/books" element={<BooksPage books={books} canManageBooks={canManageBooks} loading={loading} searchBy={searchBy} query={query} onSearchByChange={setSearchBy} onQueryChange={setQuery} onSearchSubmit={onSearchSubmit} onReset={() => void loadAllBooks()} />} />
          {canManageBooks && <Route path="/books/new" element={<BookFormPage books={books} onCreate={createBook} onUpdate={updateBook} onDelete={deleteBook} />} />}
          {canManageBooks && <Route path="/books/:id/edit" element={<BookFormPage books={books} onCreate={createBook} onUpdate={updateBook} onDelete={deleteBook} />} />}
          {canManageLoans && <Route path="/circulation" element={<CirculationPage borrower={borrower} borrowerId={borrowerId} bookId={bookId} canManageLoans={canManageLoans} setBorrowerId={setBorrowerId} setBookId={setBookId} loadBorrower={loadBorrower} loadHolds={loadHolds} processLoan={processLoan} renewLoan={renewLoan} placeHold={placeHold} borrowers={borrowers} books={books} />} />}
          <Route path="/loans" element={<LoansPage userRole={user.role} canManageLoans={canManageLoans} activeLoans={activeLoans} loanHistory={loanHistory} fineLoanId={fineLoanId} setFineLoanId={setFineLoanId} payFine={payFine} />} />
          <Route path="/holds" element={<HoldsPage holds={holds} canManageLoans={canManageLoans} isAdmin={isAdmin} checkoutReadyHold={checkoutReadyHold} onDeleteHold={deleteHold} />} />
          {canManageLoans && <Route path="/borrowers" element={<BorrowersPage borrowers={borrowers} isAdmin={isAdmin} onDelete={deleteBorrower} />} />}
          {canManageLoans && <Route path="/borrowers/new" element={<BorrowerFormPage borrowers={borrowers} onCreate={createBorrower} onUpdate={updateBorrower} />} />}
          {canManageLoans && <Route path="/borrowers/:id/edit" element={<BorrowerFormPage borrowers={borrowers} onCreate={createBorrower} onUpdate={updateBorrower} />} />}
          {isAdmin && <Route path="/users" element={<UsersPage users={users} />} />}
          {isAdmin && <Route path="/users/new" element={<UserFormPage users={users} onCreate={createUser} onUpdate={updateUser} onDelete={deleteUser} />} />}
          {isAdmin && <Route path="/users/:id/edit" element={<UserFormPage users={users} onCreate={createUser} onUpdate={updateUser} onDelete={deleteUser} />} />}
          <Route path="/" element={<Navigate to={canSeeDashboard ? '/dashboard' : '/books'} replace />} />
          <Route path="*" element={<Navigate to={canSeeDashboard ? '/dashboard' : '/books'} replace />} />
        </Routes>
      </section>
    </main>
  )
}

export default App
