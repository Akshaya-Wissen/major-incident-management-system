import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 8000,
})

export const getErrorMessage = (error) => {
  const details = error?.response?.data?.details
  if (Array.isArray(details) && details.length > 0) {
    return details.join(' ')
  }
  if (error?.code === 'ECONNABORTED') {
    return 'The backend API did not respond in time. Check that Spring Boot is running on port 8080.'
  }
  if (error?.code === 'ERR_NETWORK') {
    return 'Cannot reach the backend API. Start Spring Boot and refresh this page.'
  }
  return error?.message || 'Request failed'
}
