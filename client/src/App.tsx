import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'

type Book = {
  id: number
  title: string
  author: string
  subject: string
  issued: boolean
}

type Borrower = {
  id: number
  name: string
  address: string
  phoneNumber: number
  borrowedBooksCount: number
  onHoldBooksCount: number
}

function App() {
  const [books, setBooks] = useState<Book[]>([])
  const [borrower, setBorrower] = useState<Borrower | null>(null)
  const [searchBy, setSearchBy] = useState<'title' | 'author' | 'subject'>('title')
  const [query, setQuery] = useState('')
  const [borrowerId, setBorrowerId] = useState('')
  const [bookId, setBookId] = useState('')
  const [staffId, setStaffId] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [message, setMessage] = useState<string | null>(null)

  useEffect(() => {
    void loadAllBooks()
  }, [])

  async function loadAllBooks() {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch('/api/books')
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
        headers: { 'Content-Type': 'application/json' },
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
      const response = await fetch(`/api/borrowers/${borrowerId}`)
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

  async function processLoan(action: 'checkout' | 'checkin') {
    setLoading(true)
    setError(null)
    setMessage(null)
    try {
      const response = await fetch(`/api/loans/${action}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          borrowerId: Number(borrowerId),
          bookId: Number(bookId),
          staffId: Number(staffId),
        }),
      })

      if (!response.ok) {
        const payload = await response.json().catch(() => null)
        throw new Error(payload?.error ?? `Failed to ${action} book`)
      }

      setMessage(`Book ${action === 'checkout' ? 'checked out' : 'checked in'} successfully.`)
      await loadAllBooks()
      await loadBorrower()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="page">
      <header className="page-header">
        <h1>Library Management System</h1>
        <p>Legacy Java core with a modern web frontend</p>
      </header>

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
                <th>Title</th>
                <th>Author</th>
                <th>Subject</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {books.map((book) => (
                <tr key={book.id}>
                  <td>{book.id}</td>
                  <td>{book.title}</td>
                  <td>{book.author}</td>
                  <td>{book.subject}</td>
                  <td>{book.issued ? 'Issued' : 'Available'}</td>
                </tr>
              ))}
              {books.length === 0 && (
                <tr>
                  <td colSpan={5}>No books found.</td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </section>

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
          <input
            value={staffId}
            onChange={(event) => setStaffId(event.target.value)}
            placeholder="Staff ID (Clerk/Librarian)"
          />
          <button type="button" onClick={() => void loadBorrower()}>Load Borrower</button>
          <button type="button" onClick={() => void processLoan('checkout')}>Checkout</button>
          <button type="button" onClick={() => void processLoan('checkin')}>Check-In</button>
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
    </main>
  )
}

export default App
