import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { Borrower } from '../types'

type Props = {
  borrowers: Borrower[]
  onCreate: (payload: { username: string; password: string; fullName: string; address: string; phoneNo: string }) => Promise<void>
  onUpdate: (id: number, payload: { fullName: string; address: string; phoneNo: string }) => Promise<void>
}

export default function BorrowerFormPage({ borrowers, onCreate, onUpdate }: Props) {
  const { id } = useParams()
  const navigate = useNavigate()
  const editing = Boolean(id)
  const current = useMemo(() => borrowers.find((b) => b.id === Number(id)), [borrowers, id])

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [address, setAddress] = useState('')
  const [phoneNo, setPhoneNo] = useState('')

  useEffect(() => {
    if (!editing || !current) return
    setFullName(current.name)
    setAddress(current.address)
    setPhoneNo(current.phoneNumber)
  }, [editing, current])

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (editing && id) {
      await onUpdate(Number(id), { fullName, address, phoneNo })
    } else {
      await onCreate({ username, password, fullName, address, phoneNo })
    }
    navigate('/borrowers')
  }

  return (
    <section className="card">
      <h2>{editing ? 'Edit Borrower' : 'Register Borrower'}</h2>
      {editing && !current && <p className="error">Borrower not found in loaded list.</p>}
      <form onSubmit={handleSubmit} className="form-grid">
        {!editing && <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" required />}
        {!editing && <input value={password} type="password" onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />}
        <input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Full name" required />
        <input value={address} onChange={(e) => setAddress(e.target.value)} placeholder="Address" required />
        <input value={phoneNo} onChange={(e) => setPhoneNo(e.target.value)} placeholder="Phone" required />
        <div className="card-actions">
          <button type="submit">{editing ? 'Save Changes' : 'Create Borrower'}</button>
          <button type="button" onClick={() => navigate('/borrowers')}>Cancel</button>
        </div>
      </form>
    </section>
  )
}
