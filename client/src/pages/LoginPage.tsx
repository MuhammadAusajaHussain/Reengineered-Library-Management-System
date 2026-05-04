import type { FormEvent } from 'react'

type Props = {
  username: string
  password: string
  loading: boolean
  error: string | null
  setUsername: (value: string) => void
  setPassword: (value: string) => void
  login: (event: FormEvent<HTMLFormElement>) => Promise<void>
}

export default function LoginPage({ username, password, loading, error, setUsername, setPassword, login }: Props) {
  return (
    <main className="page">
      <header className="page-header">
        <h1>Library Management System</h1>
        <p>Reengineered web frontend with role-based login</p>
      </header>
      <section className="card">
        <h2>Login</h2>
        <br />
        <form onSubmit={login} className="search-form">
          <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" />
          <input value={password} type="password" onChange={(e) => setPassword(e.target.value)} placeholder="Password" />
          <button type="submit">Login</button>
        </form>
        {loading && <p>Loading...</p>}
        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}
