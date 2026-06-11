import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Send, Users, Wrench, FileCheck, LockKeyhole, Route } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import PageHeader from '../components/PageHeader.jsx'
import LoadingState from '../components/LoadingState.jsx'
import ErrorBanner from '../components/ErrorBanner.jsx'
import SeverityBadge from '../components/SeverityBadge.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import SlaBadge from '../components/SlaBadge.jsx'
import { people } from '../data/people.js'
import { getAdminSession } from '../services/session.js'
import { formatDateTime, getSlaState } from '../services/sla.js'

const todayPlus = (days) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return date.toISOString().slice(0, 10)
}

export default function IncidentDetails() {
  const { id } = useParams()
  const session = getAdminSession()
  const [incident, setIncident] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState(null)
  const [forms, setForms] = useState({
    communicate: { eventType: 'COMMUNICATION', actor: '', message: '' },
    assess: { customerImpact: '', businessImpact: '', affectedUsers: 0, currentHypothesis: '' },
    delegate: { incidentCommander: '', communicationLead: '', technicalLead: '', escalationManager: '', seniorManager: '', delegationNote: '' },
    resolve: { resolutionSummary: '', mitigationSteps: '', resolvedBy: '' },
    rca: { rootCause: '', contributingFactors: '', correctiveActions: '', preventiveActions: '', owner: '', dueDate: todayPlus(7), approved: true },
    close: { closedBy: '', closureSummary: '' },
  })

  const load = () => {
    api.get(`/incidents/${id}`)
      .then((response) => {
        setIncident(response.data)
        setForms((current) => ({
          ...current,
          communicate: { ...current.communicate, actor: response.data.communicationLead || '' },
          delegate: {
            ...current.delegate,
            incidentCommander: response.data.incidentCommander || '',
            communicationLead: response.data.communicationLead || '',
            technicalLead: response.data.technicalLead || '',
            escalationManager: response.data.escalationManager || '',
            seniorManager: response.data.seniorManager || '',
          },
          resolve: { ...current.resolve, resolvedBy: response.data.seniorManager || '' },
          rca: { ...current.rca, owner: response.data.incidentCommander || '' },
          close: { ...current.close, closedBy: response.data.incidentCommander || '' },
        }))
      })
      .catch((err) => setError(getErrorMessage(err)))
  }

  useEffect(load, [id])

  const patch = async (path, body) => {
    setError('')
    try {
      const response = await api.patch(`/incidents/${id}/${path}`, body)
      setIncident(response.data)
      setNotice(buildNotice(path, response.data))
    } catch (err) {
      setNotice({ title: 'Submission failed', message: getErrorMessage(err), tone: 'error' })
    }
  }

  if (!incident && !error) return <LoadingState />

  const isAdmin = session?.role === 'ADMIN'
  const canActAs = (name) => isAdmin || session?.displayName === name
  const canTeamLeadAct = incident && incident.currentOwnerRole === 'Team Lead' && canActAs(incident.currentOwner)
  const canEscalationManagerAct = incident && incident.currentOwnerRole === 'Escalation Manager' && canActAs(incident.currentOwner)
  const canSeniorManagerAct = incident && incident.currentOwnerRole === 'Senior Manager' && canActAs(incident.currentOwner)
  const sla = incident ? getSlaState(incident) : null
  const canEscalateByEta = sla?.overdue && !incident?.resolvedAt

  return (
    <>
      <PageHeader title={incident?.title || 'Incident Details'} description={incident?.description || 'Incident detail view'}>
        {incident ? (
          <>
            <span className="rounded border border-line bg-white px-3 py-2 text-sm font-bold text-slate-600">INC-{incident.id}</span>
            <SeverityBadge severity={incident.severity} />
            <StatusBadge status={incident.status} />
            <SlaBadge state={sla} />
          </>
        ) : null}
      </PageHeader>
      <ErrorBanner message={error} />
      <NoticeDialog notice={notice} onClose={() => setNotice(null)} />

      {incident ? (
        <div className="grid gap-6 xl:grid-cols-[1fr_380px]">
          <div className="space-y-6">
            <section className="section p-5">
              <h2 className="mb-4 text-lg font-bold">Approval Chain</h2>
              <div className="grid gap-4 md:grid-cols-3">
                <Info label="Incident ID" value={`INC-${incident.id}`} />
                <Info label="Raised By" value={incident.reporter} />
                <Info label="Team Lead" value={incident.teamLead} />
                <Info label="Escalation Manager" value={incident.escalationManager} />
                <Info label="Senior Manager" value={incident.seniorManager} />
                <Info label="Current Owner" value={`${incident.currentOwnerRole}: ${incident.currentOwner}`} />
                <Info label="ETA" value={formatDateTime(incident.etaDueAt)} />
              </div>
            </section>

            <section className="section p-5">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-bold">SLA Tracking</h2>
                  <p className="mt-1 text-sm text-slate-600">{sla.detail}</p>
                </div>
                <SlaBadge state={sla} />
              </div>
              <div className="mt-4 grid gap-3 sm:grid-cols-3">
                <SlaInfo label="Detected" value={formatDateTime(incident.detectedAt)} />
                <SlaInfo label="Resolution ETA" value={formatDateTime(incident.etaDueAt)} />
                <SlaInfo label="Resolved" value={formatDateTime(incident.resolvedAt)} />
              </div>
            </section>

            <section className="section p-5">
              <h2 className="mb-4 text-lg font-bold">Lifecycle Actions</h2>
              <div className="grid gap-4 lg:grid-cols-2">
                {canTeamLeadAct ? (
                  <>
                    <Action title="Communicate" icon={Send} onSubmit={() => patch('communicate', forms.communicate)}>
                      <PersonSelect label="Actor" value={forms.communicate.actor} onChange={(value) => setForms({ ...forms, communicate: { ...forms.communicate, actor: value } })} />
                      <textarea className="field" placeholder="Message" value={forms.communicate.message} onChange={(e) => setForms({ ...forms, communicate: { ...forms.communicate, message: e.target.value } })} />
                    </Action>
                    <Action title="Assess" icon={Users} onSubmit={() => patch('assess', forms.assess)}>
                      <input className="field" placeholder="Affected users" type="number" value={forms.assess.affectedUsers} onChange={(e) => setForms({ ...forms, assess: { ...forms.assess, affectedUsers: Number(e.target.value) } })} />
                      <textarea className="field" placeholder="Customer impact" value={forms.assess.customerImpact} onChange={(e) => setForms({ ...forms, assess: { ...forms.assess, customerImpact: e.target.value } })} />
                      <textarea className="field" placeholder="Business impact" value={forms.assess.businessImpact} onChange={(e) => setForms({ ...forms, assess: { ...forms.assess, businessImpact: e.target.value } })} />
                      <textarea className="field" placeholder="Current hypothesis" value={forms.assess.currentHypothesis} onChange={(e) => setForms({ ...forms, assess: { ...forms.assess, currentHypothesis: e.target.value } })} />
                    </Action>
                  </>
                ) : null}
                {canTeamLeadAct || canEscalationManagerAct ? (
                  <Action
                    title={incident.currentOwnerRole === 'Escalation Manager' ? 'Escalate to Senior Manager' : 'Escalate'}
                    icon={Users}
                    disabled={!canEscalateByEta}
                    disabledReason={`Escalation unlocks only after ETA ${formatDateTime(incident.etaDueAt)} is missed.`}
                    onSubmit={() => patch('delegate', forms.delegate)}
                  >
                    <PersonSelect label="Escalation manager" value={forms.delegate.escalationManager} onChange={(value) => setForms({ ...forms, delegate: { ...forms.delegate, escalationManager: value } })} />
                    <PersonSelect label="Senior manager" value={forms.delegate.seniorManager} onChange={(value) => setForms({ ...forms, delegate: { ...forms.delegate, seniorManager: value } })} />
                    <textarea className="field" placeholder="Delegation note" value={forms.delegate.delegationNote} onChange={(e) => setForms({ ...forms, delegate: { ...forms.delegate, delegationNote: e.target.value } })} />
                  </Action>
                ) : null}
                {canSeniorManagerAct ? (
                  <>
                    <Action title="Resolve" icon={Wrench} onSubmit={() => patch('resolve', { ...forms.resolve, resolvedBy: incident.seniorManager })}>
                      <input className="field" value={incident.seniorManager} disabled />
                      <textarea className="field" placeholder="Resolution summary" value={forms.resolve.resolutionSummary} onChange={(e) => setForms({ ...forms, resolve: { ...forms.resolve, resolutionSummary: e.target.value } })} />
                      <textarea className="field" placeholder="Mitigation steps" value={forms.resolve.mitigationSteps} onChange={(e) => setForms({ ...forms, resolve: { ...forms.resolve, mitigationSteps: e.target.value } })} />
                    </Action>
                    <Action title="RCA" icon={FileCheck} onSubmit={() => patch('rca', forms.rca)}>
                      <PersonSelect label="Owner" value={forms.rca.owner} onChange={(value) => setForms({ ...forms, rca: { ...forms.rca, owner: value } })} />
                      <input className="field" type="date" value={forms.rca.dueDate} onChange={(e) => setForms({ ...forms, rca: { ...forms.rca, dueDate: e.target.value } })} />
                      <textarea className="field" placeholder="Root cause" value={forms.rca.rootCause} onChange={(e) => setForms({ ...forms, rca: { ...forms.rca, rootCause: e.target.value } })} />
                      <textarea className="field" placeholder="Contributing factors" value={forms.rca.contributingFactors} onChange={(e) => setForms({ ...forms, rca: { ...forms.rca, contributingFactors: e.target.value } })} />
                      <textarea className="field" placeholder="Corrective actions" value={forms.rca.correctiveActions} onChange={(e) => setForms({ ...forms, rca: { ...forms.rca, correctiveActions: e.target.value } })} />
                      <textarea className="field" placeholder="Preventive actions" value={forms.rca.preventiveActions} onChange={(e) => setForms({ ...forms, rca: { ...forms.rca, preventiveActions: e.target.value } })} />
                      <label className="flex items-center gap-2 text-sm font-semibold"><input type="checkbox" checked={forms.rca.approved} onChange={(e) => setForms({ ...forms, rca: { ...forms.rca, approved: e.target.checked } })} /> Approved</label>
                    </Action>
                    <Action title="Close" icon={LockKeyhole} onSubmit={() => patch('close', forms.close)}>
                      <PersonSelect label="Closed by" value={forms.close.closedBy} onChange={(value) => setForms({ ...forms, close: { ...forms.close, closedBy: value } })} />
                      <textarea className="field" placeholder="Closure summary" value={forms.close.closureSummary} onChange={(e) => setForms({ ...forms, close: { ...forms.close, closureSummary: e.target.value } })} />
                    </Action>
                  </>
                ) : null}
              </div>
            </section>
          </div>

          <aside className="space-y-6">
            <section className="section p-5">
              <h2 className="mb-4 text-lg font-bold">Impact Snapshot</h2>
              <Info label="Service" value={incident.impactedService} />
              <Info label="SLA" value={sla.label} />
              <Info label="ETA" value={formatDateTime(incident.etaDueAt)} />
              <Info label="Current Role" value={incident.currentOwnerRole} />
              <Info label="Current Owner" value={incident.currentOwner} />
              <Info label="Customer Impact" value={incident.assessment?.customerImpact || 'Pending'} />
              <Info label="Business Impact" value={incident.assessment?.businessImpact || 'Pending'} />
              <Info label="Resolution" value={incident.resolution?.resolutionSummary || 'Pending'} />
              <Info label="RCA Approved" value={incident.rca ? String(incident.rca.approved) : 'Pending'} />
            </section>

            <section className="section p-5">
              <h2 className="mb-4 text-lg font-bold">Timeline</h2>
              <div className="space-y-4">
                {incident.timeline.map((event) => (
                  <div key={event.id} className="border-l-2 border-slate-300 pl-3">
                    <p className="text-xs font-bold text-slate-500">{event.eventType} - {new Date(event.occurredAt).toLocaleString()}</p>
                    <p className="mt-1 text-sm font-semibold">{event.actor}</p>
                    <p className="mt-1 text-sm text-slate-600">{event.message}</p>
                  </div>
                ))}
              </div>
            </section>
          </aside>
        </div>
      ) : null}
    </>
  )
}

function Info({ label, value }) {
  return (
    <div className="mb-3">
      <p className="label">{label}</p>
      <p className="mt-1 text-sm font-semibold text-slate-800">{value}</p>
    </div>
  )
}

function SlaInfo({ label, value }) {
  return (
    <div className="rounded border border-line bg-panel p-3">
      <p className="label">{label}</p>
      <p className="mt-2 text-sm font-bold text-ink">{value}</p>
    </div>
  )
}

function PersonSelect({ label, value, onChange }) {
  return (
    <select className="field" value={value} onChange={(event) => onChange(event.target.value)} required>
      <option value="" disabled>{label}</option>
      {people.map((name) => (
        <option key={name} value={name}>{name}</option>
      ))}
    </select>
  )
}

function buildNotice(action, incident) {
  const messages = {
    communicate: `Communication submitted. ${incident.currentOwnerRole} ${incident.currentOwner} remains responsible for the ticket.`,
    assess: `Assessment submitted. Next step is delegation to ${incident.escalationManager}.`,
    delegate: `Ticket escalated to ${incident.currentOwnerRole} ${incident.currentOwner}.`,
    resolve: `Resolution submitted by ${incident.currentOwnerRole} ${incident.currentOwner}.`,
    rca: 'RCA submitted and attached to the incident record.',
    close: 'Incident closed successfully.',
  }

  return {
    title: 'Submission complete',
    message: messages[action] || 'Action submitted successfully.',
  }
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

function Action({ title, icon: Icon, children, onSubmit, disabled = false, disabledReason = '' }) {
  return (
    <form
      className="rounded border border-line bg-panel p-4"
      onSubmit={(event) => {
        event.preventDefault()
        onSubmit()
      }}
    >
      <div className="mb-3 flex items-center gap-2">
        <Icon size={18} className="text-teal" />
        <h3 className="text-sm font-bold">{title}</h3>
      </div>
      <div className="space-y-3">{children}</div>
      {disabled ? <p className="mt-3 rounded border border-amber-200 bg-amber-50 p-2 text-xs font-semibold text-amber-700">{disabledReason}</p> : null}
      <button type="submit" className="btn btn-primary mt-3 w-full disabled:cursor-not-allowed disabled:opacity-50" disabled={disabled}>Submit</button>
    </form>
  )
}
