import { Link } from 'react-router-dom'
import type { Borrower } from '../types'

type Props = {
  borrowers: Borrower[]
}

export default function BorrowersPage({ borrowers }: Props) {
  return (
    <>
      <section className="card">
        <h2>Borrowers</h2>
        <div className="page-actions">
          <Link className="btn-link" to="/borrowers/new">Register Borrower</Link>
        </div>
      </section>
      <section className="cards-grid">
        {borrowers.map((item) => (
          <article key={item.id} className="entity-card">
            <h3>{item.name}</h3>
            <p><strong>Phone:</strong> {item.phoneNumber}</p>
            <p><strong>Address:</strong> {item.address}</p>
            <p><strong>Borrowed:</strong> {item.borrowedBooksCount}</p>
            <p><strong>Holds:</strong> {item.onHoldBooksCount}</p>
            <div className="card-actions">
              <Link className="btn-link" to={`/borrowers/${item.id}/edit`}>Edit Borrower</Link>
            </div>
          </article>
        ))}
        {borrowers.length === 0 && <p>No borrowers loaded.</p>}
      </section>
    </>
  )
}
