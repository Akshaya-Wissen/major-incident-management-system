const closedStatuses = ['RESOLVED', 'RCA', 'CLOSED']

export function getSlaState(incident) {
  if (!incident?.etaDueAt) {
    return { label: 'No ETA', tone: 'neutral', overdue: false, detail: 'Not scheduled' }
  }

  const dueAt = new Date(incident.etaDueAt)
  const completedAt = incident.resolvedAt ? new Date(incident.resolvedAt) : null
  const now = new Date()

  if (completedAt) {
    const late = completedAt > dueAt
    return {
      label: late ? 'Resolved late' : 'SLA met',
      tone: late ? 'danger' : 'success',
      overdue: late,
      detail: `ETA ${formatDateTime(incident.etaDueAt)}`,
    }
  }

  if (closedStatuses.includes(incident.status)) {
    return { label: 'Complete', tone: 'success', overdue: false, detail: `ETA ${formatDateTime(incident.etaDueAt)}` }
  }

  if (now > dueAt) {
    return { label: 'Overdue', tone: 'danger', overdue: true, detail: `Missed ${formatDateTime(incident.etaDueAt)}` }
  }

  const minutesLeft = Math.round((dueAt.getTime() - now.getTime()) / 60000)
  if (minutesLeft <= 60) {
    return { label: 'Due soon', tone: 'warning', overdue: false, detail: `${minutesLeft}m left` }
  }

  return { label: 'On track', tone: 'success', overdue: false, detail: `Due ${formatDateTime(incident.etaDueAt)}` }
}

export function formatDateTime(value) {
  if (!value) return 'Pending'
  return new Date(value).toLocaleString([], {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  })
}
