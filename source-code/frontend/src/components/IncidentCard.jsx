import { Link } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import SeverityBadge from './SeverityBadge.jsx'
import StatusBadge from './StatusBadge.jsx'
import SlaBadge from './SlaBadge.jsx'
import { getSlaState } from '../services/sla.js'

export default function IncidentCard({ incident }) {
  const sla = getSlaState(incident)

  return (
    <Link to={`/incidents/${incident.id}`} className="block rounded border border-line bg-white p-4 shadow-sm transition hover:border-slate-400 hover:shadow-md">
      <div className="mb-3 flex items-start justify-between gap-3">
        <div className="flex flex-wrap items-center gap-2">
          <span className="rounded border border-line bg-panel px-2 py-1 text-[11px] font-bold text-slate-600">INC-{incident.id}</span>
          <SeverityBadge severity={incident.severity} />
        </div>
        <ArrowRight size={17} className="shrink-0 text-slate-400" />
      </div>
      <h3 className="break-words text-sm font-bold leading-5 text-ink">{incident.title}</h3>
      <p className="mt-2 break-words text-xs text-slate-500">{incident.impactedService}</p>
      <p className="mt-2 break-words text-xs font-semibold text-slate-600">{incident.currentOwnerRole}: {incident.currentOwner}</p>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        <SlaBadge state={sla} />
        <span className="min-w-0 break-words text-xs text-slate-500">{sla.detail}</span>
      </div>
      <div className="mt-4 flex flex-wrap items-center gap-2">
        <StatusBadge status={incident.status} />
      </div>
    </Link>
  )
}
