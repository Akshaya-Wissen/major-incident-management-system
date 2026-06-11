const styles = {
  SEV1: 'bg-rose text-white',
  SEV2: 'bg-amber text-white',
  SEV3: 'bg-slate-600 text-white',
}

export default function SeverityBadge({ severity }) {
  return <span className={`inline-flex rounded px-2 py-1 text-xs font-bold leading-tight ${styles[severity]}`}>{severity}</span>
}
