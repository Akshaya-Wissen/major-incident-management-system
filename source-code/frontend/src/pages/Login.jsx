import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { Activity, LogIn } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import { saveAdminSession } from '../services/session.js'
import ErrorBanner from '../components/ErrorBanner.jsx'

const ROLE_OPTIONS = [
  { value: 'TEAM_LEAD', label: 'Team Lead' },
  { value: 'ESCALATION_MANAGER', label: 'Escalation Manager' },
  { value: 'SENIOR_MANAGER', label: 'Senior Manager' },
  { value: 'TECHNICAL_LEAD', label: 'Technical Lead' },
  { value: 'COMMUNICATION_LEAD', label: 'Communication Lead' },
]

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const [mode, setMode] = useState('login')
  const [credentials, setCredentials] = useState({ username: '', password: '' })
  const [registration, setRegistration] = useState({
    username: '',
    displayName: '',
    password: '',
    role: 'TEAM_LEAD',
  })
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

  const register = async (event) => {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const response = await api.post('/admin/register', registration)
      saveAdminSession(response.data)
      navigate(location.state?.from || '/dashboard', { replace: true })
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  const isLogin = mode === 'login'

  return (
    <main className="grid min-h-screen place-items-center bg-panel px-4 py-8">
      <section className="w-full max-w-lg rounded border border-line bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center gap-3">
          <div className="grid h-10 w-10 place-items-center rounded bg-rose text-white">
            <Activity size={21} />
          </div>
          <div>
            <h1 className="text-xl font-bold text-ink">Incident Login</h1>
            <p className="text-sm text-slate-500">Major incident command center</p>
          </div>
        </div>

        <div className="mb-5 grid grid-cols-2 rounded border border-line bg-panel p-1">
          <button
            type="button"
            className={`rounded px-3 py-2 text-sm font-bold ${isLogin ? 'bg-white text-ink shadow-sm' : 'text-slate-500'}`}
            onClick={() => {
              setMode('login')
              setError('')
            }}
          >
            Sign In
          </button>
          <button
            type="button"
            className={`rounded px-3 py-2 text-sm font-bold ${!isLogin ? 'bg-white text-ink shadow-sm' : 'text-slate-500'}`}
            onClick={() => {
              setMode('register')
              setError('')
            }}
          >
            Register
          </button>
        </div>

        <ErrorBanner message={error} />

        {isLogin ? (
          <form onSubmit={submit} className="space-y-4">
            <div>
              <label className="label" htmlFor="username">Username</label>
              <input
                id="username"
                className="field mt-1"
                autoComplete="username"
                required
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
                required
                value={credentials.password}
                onChange={(event) => setCredentials({ ...credentials, password: event.target.value })}
              />
            </div>
            <button className="btn btn-primary w-full" type="submit" disabled={submitting}>
              <LogIn size={16} />
              {submitting ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
        ) : (
          <form onSubmit={register} className="space-y-4">
            <div>
              <label className="label" htmlFor="register-name">Full name</label>
              <input
                id="register-name"
                className="field mt-1"
                autoComplete="name"
                required
                value={registration.displayName}
                onChange={(event) => setRegistration({ ...registration, displayName: event.target.value })}
              />
            </div>
            <div>
              <label className="label" htmlFor="register-username">Username</label>
              <input
                id="register-username"
                className="field mt-1"
                autoComplete="username"
                required
                value={registration.username}
                onChange={(event) => setRegistration({ ...registration, username: event.target.value })}
              />
            </div>
            <div>
              <label className="label" htmlFor="register-role">Role</label>
              <select
                id="register-role"
                className="field mt-1"
                required
                value={registration.role}
                onChange={(event) => setRegistration({ ...registration, role: event.target.value })}
              >
                {ROLE_OPTIONS.map((role) => (
                  <option key={role.value} value={role.value}>{role.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label" htmlFor="register-password">Password</label>
              <input
                id="register-password"
                type="password"
                className="field mt-1"
                autoComplete="new-password"
                required
                minLength={6}
                value={registration.password}
                onChange={(event) => setRegistration({ ...registration, password: event.target.value })}
              />
            </div>
            <button className="btn btn-primary w-full" type="submit" disabled={submitting}>
              <LogIn size={16} />
              {submitting ? 'Creating account...' : 'Create Account'}
            </button>
          </form>
        )}
      </section>
    </main>
  )
}
