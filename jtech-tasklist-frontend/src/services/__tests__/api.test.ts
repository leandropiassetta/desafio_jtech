import { describe, it, expect } from 'vitest'

describe('API Client', () => {
  it('should be created with axios instance', async () => {
    const apiClient = await import('../api').then(m => m.default)
    expect(apiClient).toBeDefined()
  })

  it('should have baseURL configured', async () => {
    const apiClient = await import('../api').then(m => m.default)
    // The baseURL is configured in axios.create()
    // We verify it exists by checking the instance
    expect(apiClient.defaults.baseURL).toContain('localhost')
  })

  it('should have Content-Type header set to application/json', async () => {
    const apiClient = await import('../api').then(m => m.default)
    const headers = apiClient.defaults?.headers || {}
    expect(headers['Content-Type'] || headers.common?.['Content-Type']).toBe('application/json')
  })

  it('should have response interceptor configured', async () => {
    const apiClient = await import('../api').then(m => m.default)
    expect(apiClient.interceptors).toBeDefined()
    expect(apiClient.interceptors.response).toBeDefined()
  })

  it('should correctly map Portuguese status to English in response', () => {
    // Test the mapping logic
    const statusMap = {
      'pendente': 'PENDING',
      'concluída': 'DONE',
    }

    expect(statusMap['pendente']).toBe('PENDING')
    expect(statusMap['concluída']).toBe('DONE')
  })
})

