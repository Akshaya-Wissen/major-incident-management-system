import React from 'react'
import { RefreshCcw } from 'lucide-react'

export default class AppErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = { error: null }
  }

  static getDerivedStateFromError(error) {
    return { error }
  }

  render() {
    if (!this.state.error) {
      return this.props.children
    }

    return (
      <div className="min-h-screen bg-panel p-6">
        <div className="mx-auto mt-10 max-w-2xl rounded border border-rose/30 bg-white p-6 shadow-soft">
          <p className="label text-rose">Frontend error</p>
          <h1 className="mt-2 text-2xl font-bold text-ink">The application could not render.</h1>
          <p className="mt-3 text-sm text-slate-600">{this.state.error.message}</p>
          <button className="btn btn-primary mt-5" type="button" onClick={() => window.location.reload()}>
            <RefreshCcw size={16} />
            Reload
          </button>
        </div>
      </div>
    )
  }
}
