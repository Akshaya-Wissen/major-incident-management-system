import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { FileCheck2 } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import PageHeader from '../components/PageHeader.jsx'
import LoadingState from '../components/LoadingState.jsx'
import ErrorBanner from '../components/ErrorBanner.jsx'
import SeverityBadge from '../components/SeverityBadge.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import { getAdminSession } from '../services/session.js'
import { visibleToSession } from '../services/incidentVisibility.js'

export default function RcaPage() {
  const session = getAdminSession()
  const [incidents, setIncidents] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/incidents')
      .then((response) => setIncidents(response.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  const rcaCandidates = useMemo(
    () => incidents.filter((incident) => ['RESOLVED', 'RCA', 'CLOSED'].includes(incident.status) && visibleToSession(incident, session)),
    [incidents, session],
  )

  if (loading) return <LoadingState />

  return (
    <>
      <PageHeader
        title="RCA"
        description="Resolved incidents that need root cause analysis, approval, or final closure."
      >
        <Link to="/board?scope=RESOLVED" className="btn btn-primary">Open board view</Link>
      </PageHeader>
      <ErrorBanner message={error} />
      <div className="section overflow-hidden">
        <div className="grid grid-cols-[1fr_110px_120px] border-b border-line bg-panel px-4 py-3 text-xs font-bold uppercase text-slate-500 md:grid-cols-[1fr_110px_120px_170px]">
          <span>Incident</span>
          <span>Severity</span>
          <span>Status</span>
          <span className="hidden md:block">Owner</span>
        </div>
        {rcaCandidates.map((incident) => (
          <Link
            key={incident.id}
            to={`/incidents/${incident.id}`}
            className="grid grid-cols-[1fr_110px_120px] items-center gap-3 border-b border-line px-4 py-4 hover:bg-slate-50 md:grid-cols-[1fr_110px_120px_170px]"
          >
            <div className="min-w-0">
              <div className="flex items-center gap-2">
                <FileCheck2 size={17} className="shrink-0 text-teal" />
                <span className="shrink-0 rounded border border-line bg-panel px-2 py-1 text-[11px] font-bold text-slate-600">INC-{incident.id}</span>
                <p className="truncate text-sm font-bold">{incident.title}</p>
              </div>
              <p className="mt-1 text-xs text-slate-500">{incident.impactedService}</p>
            </div>
            <SeverityBadge severity={incident.severity} />
            <StatusBadge status={incident.status} />
            <span className="hidden text-sm font-semibold text-slate-600 md:block">{incident.incidentCommander}</span>
          </Link>
        ))}
        {rcaCandidates.length === 0 ? (
          <div className="p-6 text-sm font-semibold text-slate-500">No incidents are ready for RCA.</div>
        ) : null}
      </div>
    </>
  )
}
