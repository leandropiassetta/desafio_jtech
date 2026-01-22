import axios from 'axios'
import type { AxiosInstance } from 'axios'

const baseURL = 'http://localhost:8080'

const apiClient: AxiosInstance = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor to convert Portuguese status to English enum
apiClient.interceptors.response.use((response) => {
  if (response.data && Array.isArray(response.data.content)) {
    // Handle paginated responses
    response.data.content = response.data.content.map((task: any) => ({
      ...task,
      status: task.status === 'pendente' ? 'PENDING' : task.status === 'concluída' ? 'DONE' : task.status,
    }))
  } else if (response.data && response.data.status) {
    // Handle single task responses
    response.data.status = response.data.status === 'pendente' ? 'PENDING' : response.data.status === 'concluída' ? 'DONE' : response.data.status
  }
  return response
})

export default apiClient
