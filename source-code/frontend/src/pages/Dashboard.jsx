import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { AlertTriangle, CheckCircle2, Clock, ListChecks } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import PageHeader from '../components/PageHeader.jsx'
import LoadingState from '../components/LoadingState.jsx'
import ErrorBanner from '../components/ErrorBanner.jsx'
import IncidentCard from '../components/IncidentCard.jsx'
import { getAdminSession } from '../services/session.js'
import { visibleToSession } from '../services/incidentVisibility.js'

const metrics = [
  { key: 'totalIncidents', label: 'Total incidents', icon: ListChecks, to: '/board' },
  { key: 'openIncidents', label: 'Open incidents', icon: AlertTriangle, to: '/board?scope=OPEN' },
  { key: 'resolvedIncidents', label: 'Resolved/RCA', icon: Clock, to: '/board?scope=RESOLVED' },
  { key: 'closedIncidents', label: 'Closed', icon: CheckCircle2, to: '/board?scope=CLOSED' },
]

export default function Dashboard() {
  const session = getAdminSession()
  const [dashboard, setDashboard] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/dashboard')
      .then((response) => setDashboard(response.data))
      .catch((err) => setError(getErrorMessage(err)))
  }, [])

  if (!dashboard && !error) return <LoadingState />

  return (
    <>
      <PageHeader
        title="Dashboard"
        description={session?.role === 'ADMIN' ? 'Live portfolio demo view for all major availability incidents.' : `Assigned incident view for ${session?.displayName}.`}
      >
        <Link to="/board" className="btn btn-primary">
          View Board
        </Link>
      </PageHeader>
      <ErrorBanner message={error} />

      {dashboard ? (
        <div className="space-y-6">
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {metrics.map((metric) => {
              const Icon = metric.icon
              return (
                <Link key={metric.key} to={metric.to} className="section block p-5 transition hover:border-slate-400 hover:shadow-md">
                  <div className="flex items-center justify-between">
                    <p className="label">{metric.label}</p>
                    <Icon size={18} className="text-slate-500" />
                  </div>
                  <p className="mt-3 text-3xl font-bold">{dashboard[metric.key]}</p>
                </Link>
              )
            })}
          </div>

          <section className="section p-5">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-lg font-bold">Workflow Load</h2>
              <span className="text-xs font-semibold text-slate-500">Detect to Close</span>
            </div>
            <div className="grid gap-3 md:grid-cols-7">
              {Object.entries(dashboard.incidentsByStatus).map(([status, count]) => (
                <Link key={status} to={`/board?status=${status}`} className="rounded border border-line bg-panel p-3 transition hover:border-slate-400 hover:bg-white hover:shadow-sm">
                  <p className="text-xs font-bold text-slate-500">{status}</p>
                  <p className="mt-2 text-2xl font-bold">{count}</p>
                </Link>
              ))}
            </div>
          </section>

          <section>
            <div className="mb-3 flex items-center justify-between">
              <h2 className="text-lg font-bold">Recent Incidents</h2>
              <Link className="text-sm font-semibold text-teal" to="/board">Open board</Link>
            </div>
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              {dashboard.recentIncidents
                .filter((incident) => visibleToSession(incident, session))
                .map((incident) => <IncidentCard key={incident.id} incident={incident} />)}
            </div>
          </section>
        </div>
      ) : null}
    </>
  )
}
