import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import type { Book } from '../types'

type Props = {
  books: Book[]
  canManageBooks: boolean
  loading: boolean
  searchBy: 'title' | 'author' | 'subject'
  query: string
  onSearchByChange: (value: 'title' | 'author' | 'subject') => void
  onQueryChange: (value: string) => void
  onSearchSubmit: (event: FormEvent<HTMLFormElement>) => void
  onReset: () => void
}

export default function BooksPage(props: Props) {
  const {
    books,
    canManageBooks,
    loading,
    searchBy,
    query,
    onSearchByChange,
    onQueryChange,
    onSearchSubmit,
    onReset,
  } = props

  return (
    <>
      <section className="card">
        <form onSubmit={onSearchSubmit} className="search-bar">
          <select value={searchBy} onChange={(event) => onSearchByChange(event.target.value as 'title' | 'author' | 'subject')}>
            <option value="title">Title</option>
            <option value="author">Author</option>
            <option value="subject">Subject</option>
          </select>
          <input value={query} onChange={(event) => onQueryChange(event.target.value)} placeholder={`Search by ${searchBy}`} />
          <button type="submit">Search</button>
          <button type="button" onClick={onReset}>Reset</button>
        </form>
        {canManageBooks && (
          <div className="page-actions">
            <Link className="btn-link" to="/books/new">Add New Book</Link>
          </div>
        )}
      </section>

      {!loading && (
        <section className="cards-grid">
          {books.map((book) => (
            <article key={book.id} className="entity-card">
              <h3>{book.title}</h3>
              <p><strong>ISBN:</strong> {book.isbn || '-'}</p>
              <p><strong>Author:</strong> {book.author}</p>
              <p><strong>Subject:</strong> {book.subject}</p>
              <p><strong>Copies:</strong> {book.availableCopies}/{book.totalCopies}</p>
              <p><strong>Status:</strong> {book.issued ? 'Issued' : 'Available'}</p>
              {canManageBooks && (
                <div className="card-actions">
                  <Link className="btn-link" to={`/books/${book.id}/edit`}>Edit Book</Link>
                </div>
              )}
            </article>
          ))}
          {books.length === 0 && <p>No books found.</p>}
        </section>
      )}
    </>
  )
}
