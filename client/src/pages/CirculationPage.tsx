import type { Borrower } from '../types'

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
  } = props

  return (
    <section className="card">
      <h2>Borrower & Circulation</h2>
      <div className="actions-grid">
        <input value={borrowerId} onChange={(event) => setBorrowerId(event.target.value)} placeholder="Borrower ID" />
        <input value={bookId} onChange={(event) => setBookId(event.target.value)} placeholder="Book ID" />
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
    </section>
  )
}
