import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { Book } from '../types'

type Props = {
  books: Book[]
  onCreate: (payload: { isbn: string; title: string; author: string; subject: string; copies: number }) => Promise<void>
  onUpdate: (id: number, payload: { isbn: string; title: string; author: string; subject: string; totalCopies: number }) => Promise<void>
  onDelete: (id: number) => Promise<void>
}

export default function BookFormPage({ books, onCreate, onUpdate, onDelete }: Props) {
  const { id } = useParams()
  const navigate = useNavigate()
  const editing = Boolean(id)
  const current = useMemo(() => books.find((b) => b.id === Number(id)), [books, id])

  const [isbn, setIsbn] = useState('')
  const [title, setTitle] = useState('')
  const [author, setAuthor] = useState('')
  const [subject, setSubject] = useState('')
  const [copies, setCopies] = useState('1')

  useEffect(() => {
    if (!editing || !current) return
    setIsbn(current.isbn ?? '')
    setTitle(current.title)
    setAuthor(current.author)
    setSubject(current.subject)
    setCopies(String(current.totalCopies))
  }, [editing, current])

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (editing && id) {
      await onUpdate(Number(id), { isbn, title, author, subject, totalCopies: Number(copies) })
    } else {
      await onCreate({ isbn, title, author, subject, copies: Number(copies) })
    }
    navigate('/books')
  }

  async function handleDelete() {
    if (!id) return
    await onDelete(Number(id))
    navigate('/books')
  }

  return (
    <section className="card">
      <h2>{editing ? 'Edit Book' : 'Add Book'}</h2>
      {editing && !current && <p className="error">Book not found in loaded catalog. Go back and refresh books.</p>}
      <br />
      <form onSubmit={handleSubmit} className="form-grid">
        <input value={isbn} onChange={(e) => setIsbn(e.target.value)} placeholder="ISBN (optional)" />
        <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Title" required />
        <input value={author} onChange={(e) => setAuthor(e.target.value)} placeholder="Author" required />
        <input value={subject} onChange={(e) => setSubject(e.target.value)} placeholder="Subject" required />
        <input value={copies} onChange={(e) => setCopies(e.target.value)} placeholder={editing ? 'Total copies' : 'Copies'} required />
        <div className="card-actions">
          <button type="submit">{editing ? 'Save Changes' : 'Create Book'}</button>
          <button type="button" onClick={() => navigate('/books')}>Cancel</button>
          {editing && <button type="button" onClick={() => void handleDelete()}>Delete Book</button>}
        </div>
      </form>
    </section>
  )
}
