import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { getAdminSession } from '../services/session.js'

export default function ProtectedRoute() {
  const location = useLocation()
  const session = getAdminSession()

  if (!session) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}
