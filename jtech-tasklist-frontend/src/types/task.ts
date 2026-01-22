export enum TaskStatus {
  PENDING = 'PENDING',
  DONE = 'DONE',
}

// Map enum values to Portuguese values for API communication
export const TaskStatusToPortuguese: Record<TaskStatus, string> = {
  [TaskStatus.PENDING]: 'pendente',
  [TaskStatus.DONE]: 'concluída',
}

export interface Task {
  id: number
  title: string
  description?: string
  status: TaskStatus
  createdAt: string
  updatedAt: string
}

export interface TaskRequest {
  title: string
  description?: string
  status?: TaskStatus
}

export interface TaskPage {
  content: Task[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      empty: boolean
      sorted: boolean
      unsorted: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
  totalElements: number
  totalPages: number
  last: boolean
  size: number
  number: number
  sort: {
    empty: boolean
    sorted: boolean
    unsorted: boolean
  }
  first: boolean
  numberOfElements: number
  empty: boolean
}
