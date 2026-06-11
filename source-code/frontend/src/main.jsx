import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import App from './App.jsx'
import Dashboard from './pages/Dashboard.jsx'
import IncidentBoard from './pages/IncidentBoard.jsx'
import IncidentDetails from './pages/IncidentDetails.jsx'
import RcaPage from './pages/RcaPage.jsx'
import KnowledgeBase from './pages/KnowledgeBase.jsx'
import Login from './pages/Login.jsx'
import AppErrorBoundary from './components/AppErrorBoundary.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import './styles.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AppErrorBoundary>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<App />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/board" element={<IncidentBoard />} />
              <Route path="/incidents/:id" element={<IncidentDetails />} />
              <Route path="/rca" element={<RcaPage />} />
              <Route path="/knowledge-base" element={<KnowledgeBase />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AppErrorBoundary>
  </React.StrictMode>,
)
