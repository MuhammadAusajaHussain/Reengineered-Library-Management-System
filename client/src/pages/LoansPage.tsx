import { useState } from 'react'
import type { Role, ActiveLoan, LoanHistory } from '../types'

type Props = {
  userRole: Role
  canManageLoans: boolean
  activeLoans: ActiveLoan[]
  loanHistory: LoanHistory[]
  fineLoanId: string
  setFineLoanId: (value: string) => void
  payFine: () => Promise<void>
}

export default function LoansPage({ userRole, canManageLoans, activeLoans, loanHistory, fineLoanId, setFineLoanId, payFine }: Props) {
  const [showOnlyFines, setShowOnlyFines] = useState(false)
  const isStaff = userRole !== 'BORROWER'

  const filteredHistory = showOnlyFines
    ? loanHistory.filter(l => !l.finePaid) // If finePaid is false, it means there's an outstanding issue
    : loanHistory

  // For active loans, we might want to filter those with pending fine > 0 if toggle is on
  const filteredActive = showOnlyFines
    ? activeLoans.filter(l => l.pendingFine > 0)
    : activeLoans

  return (
    <>
      <section className="card">
        <div className="header-with-action">
          <h2>Active Loans</h2>
          <div className="toggle-container">
            <label className="toggle-label">
              <input
                type="checkbox"
                checked={showOnlyFines}
                onChange={(e) => setShowOnlyFines(e.target.checked)}
              />
              Show Only Overdue/Fines
            </label>
          </div>
        </div>

        <div className="inline-row">
          <select value={fineLoanId} onChange={(e) => setFineLoanId(e.target.value)}>
            <option value="">Select Loan to Pay Fine</option>
            {activeLoans.filter(l => l.pendingFine > 0).map(l => (
              <option key={l.loanId} value={l.loanId}>Loan #{l.loanId} - {l.bookTitle} (Rs {l.pendingFine})</option>
            ))}
          </select>
          {canManageLoans && <button type="button" onClick={() => void payFine()}>Mark Fine Paid</button>}
        </div>

        <div className="cards-grid">
          {filteredActive.map((loan) => (
            <article key={loan.loanId} className={`entity-card ${loan.pendingFine > 0 ? 'border-warning' : ''}`}>
              <h3>{loan.bookTitle}</h3>
              {isStaff && <p><strong>Borrower:</strong> {loan.borrowerName} (ID: {loan.borrowerId})</p>}
              <p><strong>Due:</strong> {loan.dueDate.slice(0, 10)}</p>
              <p><strong>Pending Fine:</strong> Rs {loan.pendingFine}</p>
              <p><strong>Fine Paid:</strong> {loan.finePaid ? 'Yes' : 'No'}</p>
            </article>
          ))}
          {filteredActive.length === 0 && <p>No {showOnlyFines ? 'overdue ' : ''}active loans found.</p>}
        </div>
      </section>

      <section className="card">
        <h2>Loan History</h2>
        <br />
        <div className="cards-grid">
          {filteredHistory.map((loan) => (
            <article key={loan.loanId} className={`entity-card ${!loan.finePaid ? 'border-danger' : ''}`}>
              <h3>{loan.bookTitle}</h3>
              {isStaff && <p><strong>Borrower:</strong> {loan.borrowerName} (ID: {loan.borrowerId})</p>}
              <p><strong>Issued:</strong> {loan.issueDate.slice(0, 10)}</p>
              <p><strong>Due:</strong> {loan.dueDate.slice(0, 10)}</p>
              <p><strong>Returned:</strong> {loan.returnDate ? loan.returnDate.slice(0, 10) : '-'}</p>
              <p><strong>Fine Paid:</strong> {loan.finePaid ? 'Yes' : 'No'}</p>
            </article>
          ))}
          {filteredHistory.length === 0 && <p>No {showOnlyFines ? 'overdue ' : ''}loan history found.</p>}
        </div>
      </section>
    </>
  )
}
