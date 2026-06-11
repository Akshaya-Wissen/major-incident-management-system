import { useMemo, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { Bot, Send, Sparkles, X } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'

const prompts = [
  'Summarize this incident',
  'Suggest next actions',
  'Draft stakeholder update',
  'Check SLA escalation risk',
  'Draft RCA outline',
]

export default function AiAssistant() {
  const location = useLocation()
  const incidentId = useMemo(() => {
    const match = location.pathname.match(/^\/incidents\/(\d+)/)
    return match ? Number(match[1]) : null
  }, [location.pathname])
  const [open, setOpen] = useState(false)
  const [input, setInput] = useState('')
  const [messages, setMessages] = useState([
    {
      role: 'assistant',
      text: 'Ask me about any ticket using its visible ID, for example INC-3 or incident 3. I can summarize, check SLA risk, suggest next actions, draft updates, or outline RCA.',
    },
  ])
  const [loading, setLoading] = useState(false)

  const send = async (message = input) => {
    const trimmed = message.trim()
    if (!trimmed || loading) return

    setMessages((current) => [...current, { role: 'user', text: trimmed }])
    setInput('')
    setLoading(true)
    try {
      const response = await api.post('/ai/chat', { message: trimmed, incidentId })
      setMessages((current) => [...current, { role: 'assistant', text: response.data.answer }])
    } catch (error) {
      setMessages((current) => [...current, { role: 'assistant', text: getErrorMessage(error) }])
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <button
        type="button"
        className="fixed bottom-5 right-5 z-40 grid h-12 w-12 place-items-center rounded-full bg-teal text-white shadow-soft hover:bg-teal/90"
        onClick={() => setOpen(true)}
        aria-label="Open AI assistant"
      >
        <Bot size={22} />
      </button>

      {open ? (
        <div className="fixed bottom-20 right-5 z-50 flex h-[620px] max-h-[calc(100vh-7rem)] w-[min(420px,calc(100vw-2rem))] flex-col rounded border border-line bg-white shadow-soft">
          <div className="flex items-center justify-between border-b border-line p-4">
            <div className="flex items-center gap-2">
              <div className="grid h-8 w-8 place-items-center rounded bg-teal text-white">
                <Sparkles size={17} />
              </div>
              <div>
                <h2 className="text-sm font-bold">AI Incident Assistant</h2>
                <p className="text-xs text-slate-500">{incidentId ? `INC-${incidentId} context enabled` : 'Mention INC-1, incident 1, or ticket 1'}</p>
              </div>
            </div>
            <button type="button" className="rounded p-1 text-slate-500 hover:bg-slate-100" onClick={() => setOpen(false)} aria-label="Close AI assistant">
              <X size={18} />
            </button>
          </div>

          <div className="border-b border-line p-3">
            <div className="flex flex-wrap gap-2">
              {prompts.map((prompt) => (
                <button
                  key={prompt}
                  type="button"
                  className="rounded border border-line bg-panel px-2 py-1 text-xs font-semibold text-slate-600 hover:bg-slate-100"
                  onClick={() => send(prompt)}
                >
                  {prompt}
                </button>
              ))}
            </div>
          </div>

          <div className="flex-1 space-y-3 overflow-y-auto p-4">
            {messages.map((message, index) => (
              <div key={`${message.role}-${index}`} className={message.role === 'user' ? 'text-right' : 'text-left'}>
                <div className={`inline-block max-w-[90%] whitespace-pre-wrap rounded border px-3 py-2 text-sm leading-6 ${
                  message.role === 'user'
                    ? 'border-teal bg-teal text-white'
                    : 'border-line bg-panel text-slate-700'
                }`}>
                  {message.text}
                </div>
              </div>
            ))}
            {loading ? <p className="text-xs font-semibold text-slate-500">Thinking...</p> : null}
          </div>

          <form
            className="flex gap-2 border-t border-line p-3"
            onSubmit={(event) => {
              event.preventDefault()
              send()
            }}
          >
            <input
              className="field"
              placeholder="Ask about INC-1, ETA, RCA..."
              value={input}
              onChange={(event) => setInput(event.target.value)}
            />
            <button type="submit" className="btn btn-primary shrink-0" disabled={loading}>
              <Send size={16} />
            </button>
          </form>
        </div>
      ) : null}
    </>
  )
}
