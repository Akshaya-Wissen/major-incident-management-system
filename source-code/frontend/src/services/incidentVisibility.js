export function visibleToSession(incident, session) {
  if (!session || session.role === 'ADMIN') {
    return true
  }

  return [
    incident.incidentCommander,
    incident.communicationLead,
    incident.technicalLead,
    incident.teamLead,
    incident.escalationManager,
    incident.seniorManager,
    incident.currentOwner,
  ].includes(session.displayName)
}
