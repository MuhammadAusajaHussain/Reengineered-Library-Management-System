import type { HoldItem } from '../types'

type Props = {
  holds: HoldItem[]
  canManageLoans: boolean
  isAdmin: boolean
  checkoutReadyHold: (holdId: number) => Promise<void>
  onDeleteHold: (holdId: number) => Promise<void>
}

export default function HoldsPage({ holds, canManageLoans, isAdmin, checkoutReadyHold, onDeleteHold }: Props) {
  return (
    <section className="card">
      <h2>Hold Requests</h2>
      <div className="cards-grid">
        {holds.map((hold) => (
          <article key={hold.id} className="entity-card">
            <h3>{hold.bookTitle}</h3>
            <p><strong>Borrower:</strong> {hold.borrowerName}</p>
            <p><strong>Status:</strong> {hold.status}</p>
            <p><strong>Requested:</strong> {hold.requestDate.slice(0, 10)}</p>
            <div className="card-actions">
              {canManageLoans && hold.status === 'READY' && (
                <button type="button" onClick={() => void checkoutReadyHold(hold.id)}>Checkout READY Hold</button>
              )}
              {isAdmin && (
                <button type="button" className="btn-danger" onClick={() => void onDeleteHold(hold.id)}>Delete Hold</button>
              )}
            </div>
          </article>
        ))}
        {holds.length === 0 && <p>No hold requests found.</p>}
      </div>
    </section>
  )
}
