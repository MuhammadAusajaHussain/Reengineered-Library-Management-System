import type { DashboardStats, Role, ActiveLoan, LoanHistory } from '../types'

type Props = {
  stats: DashboardStats | null
  isAdmin: boolean
  role: Role
  activeLoans: ActiveLoan[]
  loanHistory: LoanHistory[]
}

export default function DashboardPage({ stats, isAdmin, role, activeLoans, loanHistory }: Props) {
  const isBorrower = role === 'BORROWER';

  return (
    <div className="dashboard-container">
      <section className="card">
        <h2>{isBorrower ? 'My Activity' : 'Dashboard'}</h2>
        {stats ? (
          <div className="stats-grid">
            <div className="stats-card">
              <span className="value">{stats.totalBooks}</span>
              <span className="label">Total Books</span>
            </div>
            <div className="stats-card">
              <span className="value">{stats.activeLoans}</span>
              <span className="label">{isBorrower ? 'My Active Loans' : 'Active Loans'}</span>
            </div>
            <div className="stats-card">
              <span className="value">{stats.overdueUnpaidLoans}</span>
              <span className="label">{isBorrower ? 'My Overdue Fines' : 'Overdue Unpaid'}</span>
            </div>
            <div className="stats-card">
              <span className="value">{stats.activeHolds}</span>
              <span className="label">{isBorrower ? 'My Active Holds' : 'Active Holds'}</span>
            </div>

            {!isBorrower ? (
              <>
                <div className="stats-card">
                  <span className="value">{stats.totalLoans}</span>
                  <span className="label">Total Books Borrowed</span>
                </div>
                {isAdmin && (
                  <div className="stats-card">
                    <span className="value">{stats.totalStaff}</span>
                    <span className="label">Total Staff</span>
                  </div>
                )}
              </>
            ) : (
              <>
                <div className="stats-card">
                  <span className="value">{stats.totalLoans}</span>
                  <span className="label">Total Books I've Borrowed</span>
                </div>
              </>
            )}
          </div>
        ) : (
          <p>Loading stats...</p>
        )}
      </section>

      {isBorrower && (
        <div className="borrower-details-grid">
          <section className="card">
            <h3>Books I Have Right Now</h3>
            <div className="simple-list">
              {activeLoans.map(loan => (
                <div key={loan.loanId} className="list-item">
                  <span><strong>{loan.bookTitle}</strong></span>
                  <span className="detail">Due: {new Date(loan.dueDate).toLocaleDateString()}</span>
                </div>
              ))}
              {activeLoans.length === 0 && <p>No books currently borrowed.</p>}
            </div>
          </section>

          <section className="card">
            <h3>Books Borrowed in the Past</h3>
            <div className="simple-list">
              {loanHistory.filter(h => h.returnDate).map(h => (
                <div key={h.loanId} className="list-item">
                  <span><strong>{h.bookTitle}</strong></span>
                  <span className="detail">Returned: {new Date(h.returnDate!).toLocaleDateString()}</span>
                </div>
              ))}
              {loanHistory.filter(h => h.returnDate).length === 0 && <p>No return history.</p>}
            </div>
          </section>
        </div>
      )}
    </div>
  )
}
