import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { Activity, LogIn } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import { saveAdminSession } from '../services/session.js'
import ErrorBanner from '../components/ErrorBanner.jsx'

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const [credentials, setCredentials] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const response = await api.post('/admin/login', credentials)
      saveAdminSession(response.data)
      navigate(location.state?.from || '/dashboard', { replace: true })
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="grid min-h-screen place-items-center bg-panel px-4 py-8">
      <section className="w-full max-w-md rounded border border-line bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center gap-3">
          <div className="grid h-10 w-10 place-items-center rounded bg-rose text-white">
            <Activity size={21} />
          </div>
          <div>
            <h1 className="text-xl font-bold text-ink">Incident Login</h1>
            <p className="text-sm text-slate-500">Major incident command center</p>
          </div>
        </div>

        <ErrorBanner message={error} />

        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="label" htmlFor="username">Username</label>
            <input
              id="username"
              className="field mt-1"
              autoComplete="username"
              value={credentials.username}
              onChange={(event) => setCredentials({ ...credentials, username: event.target.value })}
            />
          </div>
          <div>
            <label className="label" htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              className="field mt-1"
              autoComplete="current-password"
              value={credentials.password}
              onChange={(event) => setCredentials({ ...credentials, password: event.target.value })}
            />
          </div>
          <button className="btn btn-primary w-full" type="submit" disabled={submitting}>
            <LogIn size={16} />
            {submitting ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
      </section>
    </main>
  )
}
