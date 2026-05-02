import type { DashboardStats } from '../types'

type Props = {
  stats: DashboardStats | null
  isAdmin: boolean
}

export default function DashboardPage({ stats, isAdmin }: Props) {
  return (
    <section className="card">
      <h2>Dashboard</h2>
      {stats ? (
        <div className="stats-grid">
          <div className="stats-card">
            <span className="value">{stats.totalBooks}</span>
            <span className="label">Total Books</span>
          </div>
          <div className="stats-card">
            <span className="value">{stats.activeLoans}</span>
            <span className="label">Active Loans</span>
          </div>
          <div className="stats-card">
            <span className="value">{stats.overdueUnpaidLoans}</span>
            <span className="label">Overdue Unpaid</span>
          </div>
          <div className="stats-card">
            <span className="value">{stats.activeHolds}</span>
            <span className="label">Active Holds</span>
          </div>
          <div className="stats-card">
            <span className="value">{stats.totalBorrowers}</span>
            <span className="label">Total Borrowers</span>
          </div>
          {isAdmin && (
            <div className="stats-card">
              <span className="value">{stats.totalStaff}</span>
              <span className="label">Total Staff</span>
            </div>
          )}
        </div>
      ) : (
        <p>Loading dashboard stats...</p>
      )}
    </section>
  )
}
