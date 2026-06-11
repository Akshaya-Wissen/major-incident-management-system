import { Outlet, NavLink } from 'react-router-dom'
import { Activity, ClipboardList, FileText, LayoutDashboard, Library, LogOut } from 'lucide-react'
import { clearAdminSession, getAdminSession } from './services/session.js'
import AiAssistant from './components/AiAssistant.jsx'

const nav = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/board', label: 'Incident Board', icon: ClipboardList },
  { to: '/rca', label: 'RCA', icon: FileText },
  { to: '/knowledge-base', label: 'Knowledge Base', icon: Library },
]

export default function App() {
  const session = getAdminSession()
  const logout = () => {
    clearAdminSession()
    window.location.href = '/login'
  }

  return (
    <div className="min-h-screen">
      <aside className="fixed inset-y-0 left-0 hidden w-64 border-r border-line bg-white lg:block">
        <div className="flex h-16 items-center gap-3 border-b border-line px-5">
          <div className="grid h-9 w-9 place-items-center rounded bg-rose text-white">
            <Activity size={20} />
          </div>
          <div>
            <p className="text-sm font-bold">Major Incident</p>
            <p className="text-xs text-slate-500">Availability command center</p>
          </div>
        </div>
        <nav className="space-y-1 p-3">
          {nav.map((item) => {
            const Icon = item.icon
            return (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded px-3 py-2 text-sm font-semibold ${
                    isActive ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100'
                  }`
                }
              >
                <Icon size={18} />
                {item.label}
              </NavLink>
            )
          })}
        </nav>
        <div className="absolute bottom-0 left-0 right-0 border-t border-line p-3">
          <p className="px-3 text-xs font-semibold text-slate-500">{session?.displayName || 'Admin'}</p>
          <p className="px-3 text-xs text-slate-400">{session?.role || 'ADMIN'}</p>
          <button className="mt-2 flex w-full items-center gap-3 rounded px-3 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100" type="button" onClick={logout}>
            <LogOut size={18} />
            Sign Out
          </button>
        </div>
      </aside>

      <div className="lg:pl-64">
        <header className="sticky top-0 z-10 border-b border-line bg-white/95 px-4 py-3 backdrop-blur lg:hidden">
          <div className="flex items-center gap-2 overflow-x-auto">
            {nav.map((item) => {
              const Icon = item.icon
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    `inline-flex shrink-0 items-center gap-2 rounded px-3 py-2 text-sm font-semibold ${
                      isActive ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'
                    }`
                  }
                >
                  <Icon size={16} />
                  {item.label}
                </NavLink>
              )
            })}
            <button className="inline-flex shrink-0 items-center gap-2 rounded bg-slate-100 px-3 py-2 text-sm font-semibold text-slate-700" type="button" onClick={logout}>
              <LogOut size={16} />
              Sign Out
            </button>
          </div>
        </header>
        <main className="mx-auto max-w-[1680px] px-4 py-6 sm:px-6 lg:px-8">
          <Outlet />
        </main>
        <AiAssistant />
      </div>
    </div>
  )
}
