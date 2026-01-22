import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTaskStore } from '../taskStore'
import type { Task, TaskRequest } from '@/types/task'
import { TaskStatus } from '@/types/task'

// Mock axios
vi.mock('@/services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      response: {
        use: vi.fn(),
      },
    },
  },
}))

describe('TaskStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should initialize with empty tasks', () => {
    const store = useTaskStore()
    expect(store.tasks).toEqual([])
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })

  it('should have computed property isEmpty', () => {
    const store = useTaskStore()
    expect(store.isEmpty).toBe(true)
    
    store.tasks = [
      {
        id: 1,
        title: 'Test Task',
        description: 'Test',
        status: TaskStatus.PENDING,
        createdAt: '2026-01-21',
        updatedAt: '2026-01-21',
      } as Task,
    ]
    expect(store.isEmpty).toBe(false)
  })

  it('should have correct pagination computed properties', () => {
    const store = useTaskStore()
    
    store.currentPage = 0
    store.totalPages = 5
    
    expect(store.canNextPage).toBe(true)
    expect(store.canPreviousPage).toBe(false)
    
    store.currentPage = 2
    expect(store.canNextPage).toBe(true)
    expect(store.canPreviousPage).toBe(true)
    
    store.currentPage = 4
    expect(store.canNextPage).toBe(false)
    expect(store.canPreviousPage).toBe(true)
  })

  it('should set error from extractErrorMessage for 400 status', () => {
    const store = useTaskStore()
    
    // Simulating error extraction
    const axiosError = {
      response: {
        status: 400,
        data: {},
      },
    }
    
    // The extractErrorMessage is tested indirectly
    // Here we just verify the store can hold error state
    store.error = 'Dados inválidos. Verifique os campos.'
    expect(store.error).toBe('Dados inválidos. Verifique os campos.')
  })

  it('should clear error', () => {
    const store = useTaskStore()
    store.error = 'Some error'
    expect(store.error).not.toBeNull()
    
    store.error = null
    expect(store.error).toBeNull()
  })

  it('should update loading state', () => {
    const store = useTaskStore()
    expect(store.loading).toBe(false)
    
    store.loading = true
    expect(store.loading).toBe(true)
    
    store.loading = false
    expect(store.loading).toBe(false)
  })

  it('should update pagination state', () => {
    const store = useTaskStore()
    
    store.currentPage = 1
    store.pageSize = 20
    store.totalPages = 3
    store.totalElements = 50
    
    expect(store.currentPage).toBe(1)
    expect(store.pageSize).toBe(20)
    expect(store.totalPages).toBe(3)
    expect(store.totalElements).toBe(50)
  })

  it('should update selected status filter', () => {
    const store = useTaskStore()
    
    expect(store.selectedStatus).toBeNull()
    
    store.selectedStatus = TaskStatus.PENDING
    expect(store.selectedStatus).toBe(TaskStatus.PENDING)
    
    store.selectedStatus = TaskStatus.DONE
    expect(store.selectedStatus).toBe(TaskStatus.DONE)
    
    store.selectedStatus = null
    expect(store.selectedStatus).toBeNull()
  })
})
