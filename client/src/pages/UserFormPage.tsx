import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { ManagedUser, Role } from '../types'

type Props = {
  users: ManagedUser[]
  onCreate: (payload: { username: string; password: string; fullName: string; role: Role; active: boolean }) => Promise<void>
  onUpdate: (id: number, payload: { fullName?: string; role: Role; active: boolean; password?: string }) => Promise<void>
  onDelete: (id: number) => Promise<void>
}

export default function UserFormPage({ users, onCreate, onUpdate, onDelete }: Props) {
  const { id } = useParams()
  const navigate = useNavigate()
  const editing = Boolean(id)
  const current = useMemo(() => users.find((u) => u.id === Number(id)), [users, id])

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [role, setRole] = useState<Role>('CLERK')
  const [active, setActive] = useState(true)

  useEffect(() => {
    if (!editing || !current) return
    setUsername(current.username)
    setFullName(current.fullName)
    setRole(current.role)
    setActive(current.active)
  }, [editing, current])

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (editing && id) {
      await onUpdate(Number(id), { fullName, role, active, password: password || undefined })
    } else {
      await onCreate({ username, password, fullName, role, active })
    }
    navigate('/users')
  }

  async function handleDelete() {
    if (!id) return
    await onDelete(Number(id))
    navigate('/users')
  }

  return (
    <section className="card">
      <h2>{editing ? 'Edit User' : 'Add User'}</h2>
      {editing && !current && <p className="error">User not found in loaded list.</p>}
      <form onSubmit={handleSubmit} className="form-grid">
        {!editing && <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" required />}
        <input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Full name" required />
        <input value={password} type="password" onChange={(e) => setPassword(e.target.value)} placeholder={editing ? 'New password (optional)' : 'Password'} required={!editing} />
        <select value={role} onChange={(e) => setRole(e.target.value as Role)}>
          <option value="ADMIN">ADMIN</option>
          <option value="LIBRARIAN">LIBRARIAN</option>
          <option value="CLERK">CLERK</option>
          <option value="BORROWER">BORROWER</option>
        </select>
        <label className="checkbox">
          <input type="checkbox" checked={active} onChange={(e) => setActive(e.target.checked)} />
          Active
        </label>
        <div className="card-actions">
          <button type="submit">{editing ? 'Save Changes' : 'Create User'}</button>
          <button type="button" onClick={() => navigate('/users')}>Cancel</button>
          {editing && <button type="button" onClick={() => void handleDelete()}>Delete User</button>}
        </div>
      </form>
    </section>
  )
}
