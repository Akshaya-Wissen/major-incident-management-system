import { useEffect, useState } from 'react'
import { Plus, Search } from 'lucide-react'
import { api, getErrorMessage } from '../services/api.js'
import PageHeader from '../components/PageHeader.jsx'
import LoadingState from '../components/LoadingState.jsx'
import ErrorBanner from '../components/ErrorBanner.jsx'

const blank = {
  title: '',
  category: 'Runbook',
  summary: '',
  content: '',
  tags: '',
}

export default function KnowledgeBase() {
  const [articles, setArticles] = useState([])
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(blank)
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    api.get('/knowledge-base', { params: query ? { q: query } : {} })
      .then((response) => setArticles(response.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    try {
      await api.post('/knowledge-base', form)
      setForm(blank)
      setShowForm(false)
      load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  if (loading) return <LoadingState />

  return (
    <>
      <PageHeader
        title="Knowledge Base"
        description="Reusable runbooks, RCA standards, and process notes for incident responders."
      >
        <button className="btn btn-primary" type="button" onClick={() => setShowForm((value) => !value)}>
          <Plus size={16} />
          Article
        </button>
      </PageHeader>
      <ErrorBanner message={error} />

      <form
        className="mb-5 flex gap-2"
        onSubmit={(event) => {
          event.preventDefault()
          load()
        }}
      >
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-3 text-slate-400" />
          <input className="field pl-9" placeholder="Search title, category, or tags" value={query} onChange={(e) => setQuery(e.target.value)} />
        </div>
        <button className="btn" type="submit">Search</button>
      </form>

      {showForm ? (
        <form onSubmit={submit} className="section mb-6 grid gap-4 p-5 md:grid-cols-2">
          <input className="field md:col-span-2" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <select className="field" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
            <option>Runbook</option>
            <option>Process</option>
            <option>Reference</option>
          </select>
          <input className="field" placeholder="Tags" value={form.tags} onChange={(e) => setForm({ ...form, tags: e.target.value })} />
          <textarea className="field md:col-span-2" placeholder="Summary" value={form.summary} onChange={(e) => setForm({ ...form, summary: e.target.value })} />
          <textarea className="field md:col-span-2 min-h-32" placeholder="Content" value={form.content} onChange={(e) => setForm({ ...form, content: e.target.value })} />
          <div className="md:col-span-2">
            <button className="btn btn-primary" type="submit">Create</button>
          </div>
        </form>
      ) : null}

      <div className="grid gap-4 lg:grid-cols-2">
        {articles.map((article) => (
          <article key={article.id} className="section p-5">
            <div className="mb-3 flex items-start justify-between gap-3">
              <div>
                <p className="label">{article.category}</p>
                <h2 className="mt-1 text-lg font-bold">{article.title}</h2>
              </div>
              <span className="rounded bg-panel px-2 py-1 text-xs font-bold text-slate-500">{new Date(article.updatedAt).toLocaleDateString()}</span>
            </div>
            <p className="text-sm font-semibold text-slate-700">{article.summary}</p>
            <p className="mt-3 text-sm leading-6 text-slate-600">{article.content}</p>
            <p className="mt-4 text-xs font-semibold text-teal">{article.tags}</p>
          </article>
        ))}
      </div>
    </>
  )
}
