export default function ErrorBanner({ message }) {
  if (!message) return null
  return <div className="mb-4 rounded border border-rose/30 bg-rose/10 p-3 text-sm font-semibold text-rose">{message}</div>
}
