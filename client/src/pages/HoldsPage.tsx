import type { HoldItem } from '../types'

type Props = {
  holds: HoldItem[]
  canManageLoans: boolean
  checkoutReadyHold: (holdId: number) => Promise<void>
}

export default function HoldsPage({ holds, canManageLoans, checkoutReadyHold }: Props) {
  return (
    <section className="card">
      <h2>Hold Requests</h2>
      <div className="cards-grid">
        {holds.map((hold) => (
          <article key={hold.id} className="entity-card">
            <h3>Hold #{hold.id}</h3>
            <p><strong>Book ID:</strong> {hold.bookId}</p>
            <p><strong>Book:</strong> {hold.bookTitle}</p>
            <p><strong>Requested:</strong> {hold.requestDate.slice(0, 10)}</p>
            <p><strong>Status:</strong> {hold.status}</p>
            {canManageLoans && hold.status === 'READY' && (
              <div className="card-actions">
                <button type="button" onClick={() => void checkoutReadyHold(hold.id)}>Checkout READY Hold</button>
              </div>
            )}
          </article>
        ))}
        {holds.length === 0 && <p>No hold requests loaded.</p>}
      </div>
    </section>
  )
}
