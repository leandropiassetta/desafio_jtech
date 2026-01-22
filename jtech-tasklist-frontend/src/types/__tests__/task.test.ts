import { describe, it, expect } from 'vitest'
import { TaskStatus, TaskStatusToPortuguese } from '../task'

describe('Task Types', () => {
  describe('TaskStatus enum', () => {
    it('should have PENDING status', () => {
      expect(TaskStatus.PENDING).toBe('PENDING')
    })

    it('should have DONE status', () => {
      expect(TaskStatus.DONE).toBe('DONE')
    })
  })

  describe('TaskStatusToPortuguese mapping', () => {
    it('should map PENDING to pendente', () => {
      expect(TaskStatusToPortuguese[TaskStatus.PENDING]).toBe('pendente')
    })

    it('should map DONE to concluída', () => {
      expect(TaskStatusToPortuguese[TaskStatus.DONE]).toBe('concluída')
    })

    it('should have mapping for all statuses', () => {
      const statuses = Object.values(TaskStatus)
      statuses.forEach(status => {
        expect(TaskStatusToPortuguese[status as TaskStatus]).toBeDefined()
      })
    })
  })
})
