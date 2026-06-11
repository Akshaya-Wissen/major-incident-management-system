import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Download, Plus, Route } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import PageHeader from '../components/PageHeader.jsx'
import LoadingState from '../components/LoadingState.jsx'
import ErrorBanner from '../components/ErrorBanner.jsx'
import IncidentCard from '../components/IncidentCard.jsx'
import { people, reporters } from '../data/people.js'
import { getAdminSession } from '../services/session.js'
import { visibleToSession } from '../services/incidentVisibility.js'
import { getSlaState } from '../services/sla.js'

const columns = ['DETECTED', 'COMMUNICATING', 'ASSESSING', 'DELEGATED', 'RESOLVED', 'RCA', 'CLOSED']
const severityRank = { SEV1: 1, SEV2: 2, SEV3: 3 }

const createBlankIncident = () => ({
  title: '',
  description: '',
  severity: 'SEV2',
  impactedService: '',
  etaDueAt: defaultEta(),
  reporter: '',
  teamLead: '',
  escalationManager: '',
  seniorManager: '',
})

function defaultEta() {
  const date = new Date()
  date.setHours(date.getHours() + 4)
  return toDateTimeInputValue(date)
}

function toDateTimeInputValue(date) {
  const offset = date.getTimezoneOffset()
  const local = new Date(date.getTime() - offset * 60000)
  return local.toISOString().slice(0, 16)
}

export default function IncidentBoard() {
  const session = getAdminSession()
  const [searchParams, setSearchParams] = useSearchParams()
  const statusFilter = searchParams.get('status') || 'ALL'
  const scopeFilter = searchParams.get('scope') || 'ALL'
  const [incidents, setIncidents] = useState([])
  const [form, setForm] = useState(createBlankIncident)
  const [showForm, setShowForm] = useState(false)
  const [notice, setNotice] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filters, setFilters] = useState({
    query: '',
    severity: 'ALL',
    sla: 'ALL',
    owner: 'ALL',
    sort: 'newest',
  })

  useEffect(() => {
    const nextSla = searchParams.get('sla') || 'ALL'
    setFilters((current) => current.sla === nextSla ? current : { ...current, sla: nextSla })
  }, [searchParams])

  const load = () => {
    setLoading(true)
    api.get('/incidents')
      .then((response) => setIncidents(response.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const visibleIncidents = useMemo(
    () => incidents.filter((incident) => visibleToSession(incident, session)),
    [incidents, session],
  )
  const ownerOptions = useMemo(
    () => Array.from(new Set(visibleIncidents.map((incident) => incident.currentOwner))).sort(),
    [visibleIncidents],
  )
  const filteredIncidents = useMemo(() => {
    const query = filters.query.trim().toLowerCase()
    return visibleIncidents
      .filter((incident) => {
        const sla = getSlaState(incident)
        const matchesQuery = !query || [
          incident.title,
          incident.impactedService,
          incident.currentOwner,
          incident.currentOwnerRole,
          incident.status,
        ].some((value) => String(value || '').toLowerCase().includes(query))
        const matchesSeverity = filters.severity === 'ALL' || incident.severity === filters.severity
        const matchesOwner = filters.owner === 'ALL' || incident.currentOwner === filters.owner
        const matchesStatus = statusFilter === 'ALL' || incident.status === statusFilter
        const matchesScope = scopeFilter === 'ALL'
          || (scopeFilter === 'OPEN' && !['CLOSED'].includes(incident.status))
          || (scopeFilter === 'RESOLVED' && ['RESOLVED', 'RCA'].includes(incident.status))
          || (scopeFilter === 'CLOSED' && incident.status === 'CLOSED')
        const matchesSla = filters.sla === 'ALL'
          || (filters.sla === 'OVERDUE' && sla.overdue && !incident.resolvedAt)
          || (filters.sla === 'ON_TRACK' && !sla.overdue && !incident.resolvedAt)
          || (filters.sla === 'DONE' && Boolean(incident.resolvedAt))
        return matchesQuery && matchesSeverity && matchesOwner && matchesStatus && matchesScope && matchesSla
      })
      .sort((a, b) => {
        if (filters.sort === 'etaAsc') return new Date(a.etaDueAt || 0) - new Date(b.etaDueAt || 0)
        if (filters.sort === 'etaDesc') return new Date(b.etaDueAt || 0) - new Date(a.etaDueAt || 0)
        if (filters.sort === 'severity') return severityRank[a.severity] - severityRank[b.severity]
        return new Date(b.detectedAt || 0) - new Date(a.detectedAt || 0)
      })
  }, [visibleIncidents, filters, statusFilter, scopeFilter])
  const grouped = useMemo(() => columns.reduce((acc, status) => {
    acc[status] = filteredIncidents.filter((incident) => incident.status === status)
    return acc
  }, {}), [filteredIncidents])
  const overdueCount = visibleIncidents.filter((incident) => getSlaState(incident).overdue && !incident.resolvedAt).length

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    const payload = {
      ...form,
      incidentCommander: form.teamLead,
      communicationLead: form.teamLead,
      technicalLead: form.escalationManager,
    }
    try {
      const response = await api.post('/incidents', payload)
      setForm(createBlankIncident())
      setShowForm(false)
      setNotice({
        title: 'Ticket submitted',
        message: `${response.data.title} has been raised by ${response.data.reporter}. It is now assigned to ${response.data.currentOwnerRole} ${response.data.currentOwner}.`,
      })
      load()
    } catch (err) {
      setNotice({ title: 'Submission failed', message: getErrorMessage(err), tone: 'error' })
    }
  }

  if (loading) return <LoadingState />

  return (
    <>
      <PageHeader
        title="Incident Board"
        description={session?.role === 'ADMIN' ? 'All tickets across the incident lifecycle.' : `Tickets currently assigned to ${session?.displayName}.`}
      >
        <button type="button" className="btn btn-primary" onClick={() => setShowForm((value) => !value)}>
          <Plus size={16} />
          New Incident
        </button>
      </PageHeader>
      <ErrorBanner message={error} />
      <NoticeDialog notice={notice} onClose={() => setNotice(null)} />
      <div className="mb-4 grid gap-3 md:grid-cols-3">
        <Metric label="Visible tickets" value={visibleIncidents.length} />
        <Metric label="Overdue SLA" value={overdueCount} tone={overdueCount > 0 ? 'danger' : 'success'} />
        <Metric label="Active owner" value={session?.role === 'ADMIN' ? 'Admin' : session?.displayName} />
      </div>

      {statusFilter !== 'ALL' || scopeFilter !== 'ALL' || filters.sla !== 'ALL' ? (
        <div className="mb-4 flex flex-wrap items-center gap-2 rounded border border-line bg-white p-3 text-sm font-semibold text-slate-600">
          <span>Active view:</span>
          {scopeFilter !== 'ALL' ? <FilterPill label={scopeFilter.toLowerCase()} /> : null}
          {statusFilter !== 'ALL' ? <FilterPill label={statusFilter.toLowerCase()} /> : null}
          {filters.sla !== 'ALL' ? <FilterPill label={filters.sla.toLowerCase().replace('_', ' ')} /> : null}
          <button type="button" className="ml-auto text-sm font-bold text-teal" onClick={() => {
            setSearchParams({})
            setFilters((current) => ({ ...current, sla: 'ALL' }))
          }}>
            Clear view
          </button>
        </div>
      ) : null}

      <section className="section mb-4 grid gap-3 p-4 lg:grid-cols-[1.4fr_repeat(4,1fr)_auto]">
        <input
          className="field"
          placeholder="Search tickets, services, owners"
          value={filters.query}
          onChange={(event) => setFilters({ ...filters, query: event.target.value })}
        />
        <select className="field" value={filters.severity} onChange={(event) => setFilters({ ...filters, severity: event.target.value })}>
          <option value="ALL">All severities</option>
          <option value="SEV1">SEV1</option>
          <option value="SEV2">SEV2</option>
          <option value="SEV3">SEV3</option>
        </select>
        <select className="field" value={filters.sla} onChange={(event) => {
          const next = event.target.value
          setFilters({ ...filters, sla: next })
          const params = new URLSearchParams(searchParams)
          if (next === 'ALL') params.delete('sla')
          else params.set('sla', next)
          setSearchParams(params)
        }}>
          <option value="ALL">All SLA states</option>
          <option value="OVERDUE">Overdue</option>
          <option value="ON_TRACK">Open on track</option>
          <option value="DONE">Resolved</option>
        </select>
        <select className="field" value={filters.owner} onChange={(event) => setFilters({ ...filters, owner: event.target.value })}>
          <option value="ALL">All owners</option>
          {ownerOptions.map((owner) => <option key={owner} value={owner}>{owner}</option>)}
        </select>
        <select className="field" value={filters.sort} onChange={(event) => setFilters({ ...filters, sort: event.target.value })}>
          <option value="newest">Newest first</option>
          <option value="etaAsc">ETA soonest</option>
          <option value="etaDesc">ETA latest</option>
          <option value="severity">Severity first</option>
        </select>
        <button type="button" className="btn" onClick={() => exportCsv(filteredIncidents)}>
          <Download size={16} />
          Export
        </button>
      </section>

      {showForm ? (
        <form onSubmit={submit} className="section mb-6 grid gap-4 p-5 md:grid-cols-2">
          <input className="field md:col-span-2" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <textarea className="field md:col-span-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <select className="field" value={form.severity} onChange={(e) => setForm({ ...form, severity: e.target.value })}>
            <option>SEV1</option>
            <option>SEV2</option>
            <option>SEV3</option>
          </select>
          <input className="field" placeholder="Impacted service" value={form.impactedService} onChange={(e) => setForm({ ...form, impactedService: e.target.value })} />
          <label className="grid gap-1">
            <span className="label">Resolution ETA</span>
            <input className="field" type="datetime-local" value={form.etaDueAt} onChange={(e) => setForm({ ...form, etaDueAt: e.target.value })} required />
          </label>
          <PersonSelect label="Raised by" value={form.reporter} options={reporters} onChange={(value) => setForm({ ...form, reporter: value })} />
          <PersonSelect label="Team lead" value={form.teamLead} onChange={(value) => setForm({ ...form, teamLead: value })} />
          <PersonSelect label="Escalation manager" value={form.escalationManager} onChange={(value) => setForm({ ...form, escalationManager: value })} />
          <PersonSelect label="Senior manager" value={form.seniorManager} onChange={(value) => setForm({ ...form, seniorManager: value })} />
          <div className="md:col-span-2">
            <button className="btn btn-primary" type="submit">Submit</button>
          </div>
        </form>
      ) : null}

      <div className="overflow-x-auto pb-3">
        <div className="grid auto-cols-[260px] grid-flow-col gap-4">
          {columns.map((status) => (
            <section key={status} className="min-h-64 rounded border border-line bg-panel p-3">
              <div className="mb-3 flex items-center justify-between">
                <h2 className="text-sm font-bold">{status}</h2>
                <span className="rounded bg-white px-2 py-1 text-xs font-bold text-slate-600">{grouped[status].length}</span>
              </div>
              <div className="space-y-3">
                {grouped[status].map((incident) => <IncidentCard key={incident.id} incident={incident} />)}
                {grouped[status].length === 0 ? (
                  <div className="rounded border border-dashed border-line bg-white/60 p-4 text-center text-xs font-semibold text-slate-400">
                    No tickets
                  </div>
                ) : null}
              </div>
            </section>
          ))}
        </div>
      </div>
    </>
  )
}

function FilterPill({ label }) {
  return <span className="rounded border border-line bg-panel px-2 py-1 text-xs uppercase text-slate-500">{label}</span>
}

function Metric({ label, value, tone = 'neutral' }) {
  const valueClass = tone === 'danger' ? 'text-rose' : tone === 'success' ? 'text-teal' : 'text-ink'

  return (
    <div className="section p-4">
      <p className="label">{label}</p>
      <p className={`mt-2 text-xl font-bold ${valueClass}`}>{value}</p>
    </div>
  )
}

function exportCsv(incidents) {
  const headers = ['ID', 'Title', 'Severity', 'Status', 'Service', 'Owner Role', 'Owner', 'ETA', 'Detected']
  const rows = incidents.map((incident) => [
    incident.id,
    incident.title,
    incident.severity,
    incident.status,
    incident.impactedService,
    incident.currentOwnerRole,
    incident.currentOwner,
    incident.etaDueAt || '',
    incident.detectedAt || '',
  ])
  const csv = [headers, ...rows]
    .map((row) => row.map((value) => `"${String(value ?? '').replaceAll('"', '""')}"`).join(','))
    .join('\n')
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8;' }))
  const link = document.createElement('a')
  link.href = url
  link.download = 'incident-board.csv'
  link.click()
  URL.revokeObjectURL(url)
}

function PersonSelect({ label, value, onChange, options = people }) {
  return (
    <select className="field" value={value} onChange={(event) => onChange(event.target.value)} required>
      <option value="" disabled>{label}</option>
      {options.map((name) => (
        <option key={name} value={name}>{name}</option>
      ))}
    </select>
  )
}

function NoticeDialog({ notice, onClose }) {
  if (!notice) return null
  const isError = notice.tone === 'error'

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/40 p-4">
      <div className="w-full max-w-md rounded border border-line bg-white p-5 shadow-soft">
        <div className="mb-3 flex items-center gap-2">
          <Route size={18} className={isError ? 'text-rose' : 'text-teal'} />
          <h2 className="text-base font-bold">{notice.title}</h2>
        </div>
        <p className="text-sm leading-6 text-slate-600">{notice.message}</p>
        <button type="button" className="btn btn-primary mt-4 w-full" onClick={onClose}>OK</button>
      </div>
    </div>
  )
}
