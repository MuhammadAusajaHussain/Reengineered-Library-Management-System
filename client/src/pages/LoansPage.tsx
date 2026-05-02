import type { ActiveLoan, LoanHistory } from '../types'

type Props = {
  canManageLoans: boolean
  activeLoans: ActiveLoan[]
  loanHistory: LoanHistory[]
  fineLoanId: string
  setFineLoanId: (value: string) => void
  payFine: () => Promise<void>
}

export default function LoansPage({ canManageLoans, activeLoans, loanHistory, fineLoanId, setFineLoanId, payFine }: Props) {
  return (
    <>
      <section className="card">
        <h2>Active Loans</h2>
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
          {activeLoans.map((loan) => (
            <article key={loan.loanId} className="entity-card">
              <h3>{loan.bookTitle}</h3>
              <p><strong>Due:</strong> {loan.dueDate.slice(0, 10)}</p>
              <p><strong>Pending Fine:</strong> Rs {loan.pendingFine}</p>
              <p><strong>Fine Paid:</strong> {loan.finePaid ? 'Yes' : 'No'}</p>
            </article>
          ))}
          {activeLoans.length === 0 && <p>No active loans.</p>}
        </div>
      </section>
      <section className="card">
        <h2>Loan History</h2>
        <div className="cards-grid">
          {loanHistory.map((loan) => (
            <article key={loan.loanId} className="entity-card">
              <h3>{loan.bookTitle}</h3>
              <p><strong>Issued:</strong> {loan.issueDate.slice(0, 10)}</p>
              <p><strong>Due:</strong> {loan.dueDate.slice(0, 10)}</p>
              <p><strong>Returned:</strong> {loan.returnDate ? loan.returnDate.slice(0, 10) : '-'}</p>
              <p><strong>Fine Paid:</strong> {loan.finePaid ? 'Yes' : 'No'}</p>
            </article>
          ))}
          {loanHistory.length === 0 && <p>No loan history.</p>}
        </div>
      </section>
    </>
  )
}
