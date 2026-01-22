import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import apiClient from '@/services/api'
import type { Task, TaskRequest, TaskPage, TaskStatus } from '@/types/task'
import { TaskStatusToPortuguese } from '@/types/task'

/**
 * Extract a human-friendly error message from an axios error or fallback to generic message
 */
function extractErrorMessage(err: unknown): string {
  if (axios.isAxiosError(err)) {
    // Try to extract error message from response body
    if (err.response?.data?.message) {
      return err.response.data.message
    }
    if (err.response?.data?.error) {
      return err.response.data.error
    }
    if (err.response?.status === 404) {
      return 'Tarefa não encontrada'
    }
    if (err.response?.status === 400) {
      return 'Dados inválidos. Verifique os campos.'
    }
    if (err.response?.status === 409) {
      return 'Conflito ao atualizar. A tarefa pode ter sido alterada.'
    }
    if (err.response?.status === 500) {
      return 'Erro interno do servidor. Tente novamente.'
    }
  }
  return 'Ocorreu um erro ao processar sua solicitação.'
}

export const useTaskStore = defineStore('task', () => {
  // State
  const tasks = ref<Task[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const selectedStatus = ref<TaskStatus | null>(null)

  // Computed properties
  const isEmpty = computed(() => tasks.value.length === 0)
  const canNextPage = computed(() => currentPage.value < totalPages.value - 1)
  const canPreviousPage = computed(() => currentPage.value > 0)

  /**
   * Fetch tasks with pagination and optional status filter
   * @param page - Page number (0-indexed)
   * @param size - Page size
   * @param status - Optional status filter (PENDING or DONE)
   */
  const fetchTasks = async (options?: { page?: number; size?: number; status?: TaskStatus | null }) => {
    const { page = 0, size = pageSize.value, status = selectedStatus.value } = options || {}

    loading.value = true
    error.value = null

    try {
      const params: Record<string, any> = {
        page,
        size,
        sort: 'id,desc',
      }

      if (status) {
        params.status = status
      }

      const response = await apiClient.get<TaskPage>('/tasks', { params })

      tasks.value = response.data.content
      currentPage.value = page
      pageSize.value = size
      totalPages.value = response.data.totalPages
      totalElements.value = response.data.totalElements || response.data.content.length
      selectedStatus.value = status || null
    } catch (err) {
      error.value = extractErrorMessage(err)
      console.error('Failed to fetch tasks:', err)
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetch a single task by ID
   * @param id - Task ID
   */
  const getTaskById = async (id: number): Promise<Task | null> => {
    try {
      const response = await apiClient.get<Task>(`/tasks/${id}`)
      return response.data
    } catch (err) {
      error.value = extractErrorMessage(err)
      console.error('Failed to fetch task:', err)
      return null
    }
  }

  /**
   * Create a new task and refresh the current list
   * @param payload - Task creation payload
   */
  const createTask = async (payload: TaskRequest): Promise<Task | null> => {
    loading.value = true
    error.value = null

    try {
      // Convert status enum to Portuguese string for API
      const apiPayload = {
        ...payload,
        status: payload.status ? TaskStatusToPortuguese[payload.status] : undefined,
      }
      const response = await apiClient.post<Task>('/tasks', apiPayload)
      // Refresh current page to maintain consistency
      await fetchTasks({ page: 0, size: pageSize.value, status: selectedStatus.value })
      return response.data
    } catch (err) {
      error.value = extractErrorMessage(err)
      console.error('Failed to create task:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Update an existing task and refresh the current list
   * @param id - Task ID
   * @param payload - Task update payload
   */
  const updateTask = async (id: number, payload: TaskRequest): Promise<Task | null> => {
    loading.value = true
    error.value = null

    try {
      const response = await apiClient.put<Task>(`/tasks/${id}`, {
        ...payload,
        status: payload.status ? TaskStatusToPortuguese[payload.status] : undefined,
      })
      // Refresh current page to maintain consistency
      await fetchTasks({ page: currentPage.value, size: pageSize.value, status: selectedStatus.value })
      return response.data
    } catch (err) {
      error.value = extractErrorMessage(err)
      console.error('Failed to update task:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Delete a task and refresh the current list
   * @param id - Task ID
   */
  const deleteTask = async (id: number): Promise<boolean> => {
    loading.value = true
    error.value = null

    try {
      await apiClient.delete(`/tasks/${id}`)
      // Refresh current page to maintain consistency
      await fetchTasks({ page: currentPage.value, size: pageSize.value, status: selectedStatus.value })
      return true
    } catch (err) {
      error.value = extractErrorMessage(err)
      console.error('Failed to delete task:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * Toggle task status between PENDING and DONE
   * @param task - Task to toggle
   */
  const toggleTaskStatus = async (task: Task): Promise<Task | null> => {
    const newStatus = task.status === 'PENDING' ? 'DONE' : 'PENDING'
    return updateTask(task.id, {
      title: task.title,
      description: task.description,
      status: newStatus as TaskStatus,
    })
  }

  /**
   * Move to the next page if available
   */
  const nextPage = async (): Promise<void> => {
    if (canNextPage.value) {
      await fetchTasks({
        page: currentPage.value + 1,
        size: pageSize.value,
        status: selectedStatus.value,
      })
    }
  }

  /**
   * Move to the previous page if available
   */
  const previousPage = async (): Promise<void> => {
    if (canPreviousPage.value) {
      await fetchTasks({
        page: currentPage.value - 1,
        size: pageSize.value,
        status: selectedStatus.value,
      })
    }
  }

  /**
   * Clear error message
   */
  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    tasks,
    loading,
    error,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    selectedStatus,
    // Computed
    isEmpty,
    canNextPage,
    canPreviousPage,
    // Actions
    fetchTasks,
    getTaskById,
    createTask,
    updateTask,
    deleteTask,
    toggleTaskStatus,
    nextPage,
    previousPage,
    clearError,
  }
})
