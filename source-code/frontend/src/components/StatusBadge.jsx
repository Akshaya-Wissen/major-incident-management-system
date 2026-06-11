const styles = {
  DETECTED: 'border-rose/30 bg-rose/10 text-rose',
  COMMUNICATING: 'border-amber/30 bg-amber/10 text-amber',
  ASSESSING: 'border-sky-300 bg-sky-50 text-sky-700',
  DELEGATED: 'border-indigo-300 bg-indigo-50 text-indigo-700',
  RESOLVED: 'border-teal/30 bg-teal/10 text-teal',
  RCA: 'border-violet-300 bg-violet-50 text-violet-700',
  CLOSED: 'border-slate-300 bg-slate-100 text-slate-600',
}

export default function StatusBadge({ status }) {
  return (
    <span className={`inline-flex max-w-full rounded border px-2 py-1 text-xs font-bold leading-tight ${styles[status] || styles.CLOSED}`}>
      {status}
    </span>
  )
}
