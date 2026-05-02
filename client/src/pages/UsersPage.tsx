import { Link } from 'react-router-dom'
import type { ManagedUser } from '../types'

type Props = {
  users: ManagedUser[]
}

export default function UsersPage({ users }: Props) {
  return (
    <>
      <section className="card">
        <h2>User Management</h2>
        <div className="page-actions">
          <Link className="btn-link" to="/users/new">Add New User</Link>
        </div>
      </section>
      <section className="cards-grid">
        {users.map((u) => (
          <article key={u.id} className="entity-card">
            <h3>{u.fullName}</h3>
            <p><strong>User ID:</strong> {u.id}</p>
            <p><strong>Username:</strong> {u.username}</p>
            <p><strong>Role:</strong> {u.role}</p>
            <p><strong>Status:</strong> {u.active ? 'Active' : 'Inactive'}</p>
            <div className="card-actions">
              <Link className="btn-link" to={`/users/${u.id}/edit`}>Edit User</Link>
            </div>
          </article>
        ))}
        {users.length === 0 && <p>No users loaded.</p>}
      </section>
    </>
  )
}
