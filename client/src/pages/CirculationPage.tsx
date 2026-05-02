import type { Book, Borrower } from '../types'

type Props = {
  borrower: Borrower | null
  borrowerId: string
  bookId: string
  canManageLoans: boolean
  setBorrowerId: (value: string) => void
  setBookId: (value: string) => void
  loadBorrower: () => Promise<void>
  loadHolds: () => Promise<void>
  processLoan: (action: 'checkout' | 'checkin') => Promise<void>
  renewLoan: () => Promise<void>
  placeHold: () => Promise<void>
  borrowers: Borrower[]
  books: Book[]
}

export default function CirculationPage(props: Props) {
  const {
    borrower,
    borrowerId,
    bookId,
    canManageLoans,
    setBorrowerId,
    setBookId,
    loadBorrower,
    loadHolds,
    processLoan,
    renewLoan,
    placeHold,
    borrowers,
    books,
  } = props

  return (
    <section className="card">
      <h2>Borrower & Circulation</h2>
      <div className="actions-grid two-columns">
        <select value={borrowerId} onChange={(event) => setBorrowerId(event.target.value)}>
          <option value="">Select Borrower</option>
          {borrowers.map(b => (
            <option key={b.id} value={b.id}>{b.name}</option>
          ))}
        </select>
        <select value={bookId} onChange={(event) => setBookId(event.target.value)}>
          <option value="">Select Book</option>
          {books.map(b => (
            <option key={b.id} value={b.id}>{b.title} {b.author ? `(${b.author})` : ''}</option>
          ))}
        </select>
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
          <p>Address: {borrower.address}</p>
          <p>Phone: {borrower.phoneNumber}</p>
          <p>Borrowed Books: {borrower.borrowedBooksCount}</p>
          <p>Hold Requests: {borrower.onHoldBooksCount}</p>
        </div>
      )}
    </section>
  )
}
