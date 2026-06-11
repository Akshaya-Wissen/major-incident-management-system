const SESSION_KEY = 'ims_admin_session'

export function getAdminSession() {
  const raw = window.localStorage.getItem(SESSION_KEY)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw)
  } catch {
    window.localStorage.removeItem(SESSION_KEY)
    return null
  }
}

export function saveAdminSession(session) {
  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

export function clearAdminSession() {
  window.localStorage.removeItem(SESSION_KEY)
}
