import type { DashboardStats } from '../types'

type Props = {
  stats: DashboardStats | null
}

export default function DashboardPage({ stats }: Props) {
  return (
    <section className="card">
      <h2>Dashboard</h2>
      {stats ? (
        <div className="stats-grid">
          <p>Total Books: <strong>{stats.totalBooks}</strong></p>
          <p>Active Loans: <strong>{stats.activeLoans}</strong></p>
          <p>Overdue Unpaid Loans: <strong>{stats.overdueUnpaidLoans}</strong></p>
          <p>Active Holds: <strong>{stats.activeHolds}</strong></p>
          <p>Total Borrowers: <strong>{stats.totalBorrowers}</strong></p>
        </div>
      ) : (
        <p>No dashboard stats yet.</p>
      )}
    </section>
  )
}
