const styles = {
  success: 'border-emerald-200 bg-emerald-50 text-emerald-700',
  warning: 'border-amber-200 bg-amber-50 text-amber-700',
  danger: 'border-rose-200 bg-rose-50 text-rose-700',
  neutral: 'border-slate-200 bg-slate-50 text-slate-600',
}

export default function SlaBadge({ state }) {
  return (
    <span className={`inline-flex max-w-full rounded border px-2 py-1 text-xs font-bold leading-tight ${styles[state.tone] || styles.neutral}`}>
      {state.label}
    </span>
  )
}
