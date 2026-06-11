export default function PageHeader({ title, description, children }) {
  return (
    <div className="mb-6 flex flex-col gap-4 border-b border-line pb-5 md:flex-row md:items-end md:justify-between">
      <div>
        <h1 className="text-2xl font-bold text-ink">{title}</h1>
        <p className="mt-1 max-w-3xl text-sm text-slate-600">{description}</p>
      </div>
      {children ? <div className="flex items-center gap-2">{children}</div> : null}
    </div>
  )
}
