<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useTaskStore } from '@/stores/taskStore'
import type { Task, TaskRequest, TaskStatus } from '@/types/task'
import { TaskStatus as TaskStatusEnum } from '@/types/task'

/* Store e estado geral */
const taskStore = useTaskStore()
const searchQuery = ref('')
const filterStatus = ref<TaskStatus | null>(null)

/* Estado dos modais */
const showCreateEditModal = ref(false)
const showDeleteConfirmModal = ref(false)
const editingTaskId = ref<number | null>(null)

/* Formulário */
const formTitle = ref('')
const formDescription = ref('')
const formStatus = ref<TaskStatus>(TaskStatusEnum.PENDING)
const formTitleError = ref('')
const formDescriptionError = ref('')

/* Delete */
const deleteTaskId = ref<number | null>(null)
const deleteTaskTitle = ref('')

/* Computados */
const filteredTasks = computed(() => {
  let result = taskStore.tasks
  
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(task =>
      task.title.toLowerCase().includes(query) ||
      (task.description && task.description.toLowerCase().includes(query))
    )
  }
  
  return result
})

const isLoadingTasks = computed(() => taskStore.loading)
const hasError = computed(() => !!taskStore.error)
const isEmptyState = computed(() => !isLoadingTasks.value && filteredTasks.value.length === 0 && !searchQuery.value.trim())

/* Lifecycle */
onMounted(async () => {
  await loadTasks()
})

/* Functions */
const loadTasks = async () => {
  await taskStore.fetchTasks({ status: filterStatus.value })
}

const openCreateModal = () => {
  editingTaskId.value = null
  formTitle.value = ''
  formDescription.value = ''
  formStatus.value = TaskStatusEnum.PENDING
  formTitleError.value = ''
  formDescriptionError.value = ''
  showCreateEditModal.value = true
}

const openEditModal = (task: Task) => {
  editingTaskId.value = task.id
  formTitle.value = task.title
  formDescription.value = task.description || ''
  formStatus.value = task.status
  formTitleError.value = ''
  formDescriptionError.value = ''
  showCreateEditModal.value = true
}

const closeCreateEditModal = () => {
  showCreateEditModal.value = false
  formTitle.value = ''
  formDescription.value = ''
  formStatus.value = TaskStatusEnum.PENDING
  formTitleError.value = ''
  formDescriptionError.value = ''
  editingTaskId.value = null
}

const validateForm = (): boolean => {
  formTitleError.value = ''
  formDescriptionError.value = ''

  if (!formTitle.value.trim()) {
    formTitleError.value = 'Título é obrigatório'
    return false
  }

  if (formTitle.value.length > 120) {
    formTitleError.value = 'Título não pode exceder 120 caracteres'
    return false
  }

  if (formDescription.value.length > 1000) {
    formDescriptionError.value = 'Descrição não pode exceder 1000 caracteres'
    return false
  }

  return true
}

const submitForm = async () => {
  if (!validateForm()) {
    return
  }

  const request: TaskRequest = {
    title: formTitle.value.trim(),
    description: formDescription.value.trim() || undefined,
    status: formStatus.value,
  }

  if (editingTaskId.value) {
    const result = await taskStore.updateTask(editingTaskId.value, request)
    if (result) {
      closeCreateEditModal()
      await loadTasks()
    }
  } else {
    const result = await taskStore.createTask(request)
    if (result) {
      closeCreateEditModal()
      await loadTasks()
    }
  }
}

const openDeleteConfirm = (task: Task) => {
  deleteTaskId.value = task.id
  deleteTaskTitle.value = task.title
  showDeleteConfirmModal.value = true
}

const closeDeleteConfirm = () => {
  showDeleteConfirmModal.value = false
  deleteTaskId.value = null
  deleteTaskTitle.value = ''
}

const confirmDelete = async () => {
  if (deleteTaskId.value) {
    await taskStore.deleteTask(deleteTaskId.value)
    closeDeleteConfirm()
    await loadTasks()
  }
}

const toggleTaskStatus = async (task: Task) => {
  await taskStore.toggleTaskStatus(task)
  await loadTasks()
}

const filterByStatus = async (status: TaskStatus | null) => {
  filterStatus.value = status
  await loadTasks()
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

const handleModalKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape') {
    if (showCreateEditModal.value) {
      closeCreateEditModal()
    } else if (showDeleteConfirmModal.value) {
      closeDeleteConfirm()
    }
  }
}
</script>

<template>
  <div class="tasks-view">
    <!-- Error Banner -->
    <div v-if="hasError" class="error-banner" role="alert">
      <span class="error-icon">⚠️</span>
      <span>{{ taskStore.error }}</span>
      <button
        type="button"
        class="error-close"
        @click="taskStore.error = null"
        aria-label="Fechar mensagem de erro"
      >
        ✕
      </button>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <!-- Título da seção -->
      <div class="toolbar-header">
        <h1>Minhas Tarefas</h1>
      </div>

      <!-- Controles -->
      <div class="toolbar-controls">
        <!-- Search -->
        <div class="search-group">
          <input
            v-model="searchQuery"
            type="text"
            class="search-input"
            placeholder="Buscar tarefas..."
            aria-label="Buscar tarefas por título ou descrição"
          />
          <span class="search-icon">🔍</span>
        </div>

        <!-- Status Filters -->
        <div class="filter-chips">
          <button
            type="button"
            :class="['filter-chip', { active: filterStatus === null }]"
            @click="filterByStatus(null)"
            aria-label="Mostrar todas as tarefas"
          >
            Todas
          </button>
          <button
            type="button"
            :class="['filter-chip', { active: filterStatus === TaskStatusEnum.PENDING }]"
            @click="filterByStatus(TaskStatusEnum.PENDING)"
            aria-label="Mostrar tarefas pendentes"
          >
            Pendentes
          </button>
          <button
            type="button"
            :class="['filter-chip', { active: filterStatus === TaskStatusEnum.DONE }]"
            @click="filterByStatus(TaskStatusEnum.DONE)"
            aria-label="Mostrar tarefas concluídas"
          >
            Concluídas
          </button>
        </div>

        <!-- Primary Button -->
        <button
          type="button"
          class="btn btn-primary"
          @click="openCreateModal"
          aria-label="Criar nova tarefa"
        >
          + Nova Tarefa
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoadingTasks" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Carregando tarefas...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="isEmptyState" class="empty-state">
      <div class="empty-icon">📋</div>
      <h2>Nenhuma tarefa ainda</h2>
      <p>Comece criando sua primeira tarefa!</p>
      <button
        type="button"
        class="btn btn-primary"
        @click="openCreateModal"
      >
        Criar Primeira Tarefa
      </button>
    </div>

    <!-- No Results State (quando há filtro ativo) -->
    <div v-else-if="filteredTasks.length === 0" class="empty-state">
      <div class="empty-icon">🔍</div>
      <h2>Nenhuma tarefa encontrada</h2>
      <p>Tente ajustar seus filtros de busca</p>
    </div>

    <!-- Tasks List -->
    <div v-else class="tasks-grid">
      <article
        v-for="task in filteredTasks"
        :key="task.id"
        class="task-card"
      >
        <!-- Task Header -->
        <div class="task-card-header">
          <div class="task-title-group">
            <input
              type="checkbox"
              :checked="task.status === TaskStatusEnum.DONE"
              @change="toggleTaskStatus(task)"
              class="task-checkbox"
              :aria-label="`Marcar tarefa '${task.title}' como ${task.status === TaskStatusEnum.DONE ? 'pendente' : 'concluída'}`"
            />
            <h3 
              :class="['task-title', { completed: task.status === TaskStatusEnum.DONE }]"
              :title="task.title"
            >
              {{ task.title }}
            </h3>
          </div>

          <div class="task-header-right">
            <span :class="['status-badge', task.status.toLowerCase()]">
              {{ task.status === TaskStatusEnum.PENDING ? 'Pendente' : 'Concluída' }}
            </span>
            <div class="task-actions" role="group" aria-label="Ações da tarefa">
              <button
                type="button"
                class="action-btn btn-edit"
                @click="openEditModal(task)"
                aria-label="Editar tarefa"
                data-tooltip="Editar tarefa"
              >
                ✎
              </button>
              <button
                type="button"
                class="action-btn btn-delete"
                @click="openDeleteConfirm(task)"
                aria-label="Deletar tarefa"
                data-tooltip="Deletar tarefa"
              >
                🗑
              </button>
            </div>
          </div>
        </div>

        <!-- Task Description -->
        <div v-if="task.description" class="task-description">
          {{ task.description }}
        </div>

        <!-- Task Meta -->
        <div class="task-meta">
          <small>
            <span class="meta-label">Criada:</span>
            {{ formatDate(task.createdAt) }}
          </small>
          <small v-if="task.updatedAt !== task.createdAt">
            <span class="meta-label">Atualizada:</span>
            {{ formatDate(task.updatedAt) }}
          </small>
        </div>
      </article>
    </div>

    <!-- Pagination -->
    <div v-if="!isEmptyState && taskStore.totalPages > 1" class="pagination-container">
      <button
        type="button"
        class="pagination-btn"
        :disabled="!taskStore.canPreviousPage"
        @click="taskStore.previousPage()"
        aria-label="Página anterior"
      >
        ← Anterior
      </button>

      <button
        type="button"
        class="pagination-btn"
        :disabled="!taskStore.canNextPage"
        @click="taskStore.nextPage()"
        aria-label="Próxima página"
      >
        Próxima →
      </button>
    </div>

    <!-- Create/Edit Modal -->
    <teleport to="body">
      <div
        v-if="showCreateEditModal"
        class="modal-overlay"
        @click.self="closeCreateEditModal"
        @keydown="handleModalKeydown"
      >
        <dialog class="modal-dialog" open>
          <div class="modal-header">
            <h2>{{ editingTaskId ? 'Editar Tarefa' : 'Nova Tarefa' }}</h2>
            <button
              type="button"
              class="modal-close"
              @click="closeCreateEditModal"
              aria-label="Fechar modal"
            >
              ✕
            </button>
          </div>

          <form @submit.prevent="submitForm" class="modal-form">
            <!-- Title Field -->
            <div class="form-group">
              <label for="form-title" class="form-label">
                Título <span class="required">*</span>
              </label>
              <input
                id="form-title"
                v-model="formTitle"
                type="text"
                class="form-input"
                placeholder="Digite o título da tarefa"
                maxlength="120"
                required
                aria-describedby="title-error title-counter"
              />
              <div v-if="formTitleError" id="title-error" class="form-error" role="alert">
                {{ formTitleError }}
              </div>
              <div id="title-counter" class="form-counter">
                {{ formTitle.length }}/120
              </div>
            </div>

            <!-- Description Field -->
            <div class="form-group">
              <label for="form-description" class="form-label">
                Descrição <span class="optional">(opcional)</span>
              </label>
              <textarea
                id="form-description"
                v-model="formDescription"
                class="form-textarea"
                placeholder="Digite uma descrição detalhada..."
                maxlength="1000"
                aria-describedby="description-error description-counter"
              ></textarea>
              <div v-if="formDescriptionError" id="description-error" class="form-error" role="alert">
                {{ formDescriptionError }}
              </div>
              <div id="description-counter" class="form-counter">
                {{ formDescription.length }}/1000
              </div>
            </div>

            <!-- Status Field -->
            <div class="form-group">
              <label for="form-status" class="form-label">
                Status <span class="required">*</span>
              </label>
              <select
                id="form-status"
                v-model="formStatus"
                class="form-select"
                required
              >
                <option :value="TaskStatusEnum.PENDING">Pendente</option>
                <option :value="TaskStatusEnum.DONE">Concluída</option>
              </select>
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
              <button
                type="button"
                class="btn btn-secondary"
                @click="closeCreateEditModal"
              >
                Cancelar
              </button>
              <button
                type="submit"
                class="btn btn-primary"
              >
                {{ editingTaskId ? 'Atualizar' : 'Criar' }}
              </button>
            </div>
          </form>
        </dialog>
      </div>
    </teleport>

    <!-- Delete Confirm Modal -->
    <teleport to="body">
      <div
        v-if="showDeleteConfirmModal"
        class="modal-overlay"
        @click.self="closeDeleteConfirm"
        @keydown="handleModalKeydown"
      >
        <dialog class="modal-dialog modal-confirm" open>
          <div class="modal-header">
            <h2>Confirmar Exclusão</h2>
            <button
              type="button"
              class="modal-close"
              @click="closeDeleteConfirm"
              aria-label="Fechar modal"
            >
              ✕
            </button>
          </div>

          <div class="modal-body">
            <p>Tem certeza que deseja deletar a tarefa?</p>
            <p class="modal-task-title">{{ deleteTaskTitle }}</p>
            <p class="modal-warning">⚠️ Esta ação não pode ser desfeita.</p>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="btn btn-secondary"
              @click="closeDeleteConfirm"
            >
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-danger"
              @click="confirmDelete"
            >
              Deletar
            </button>
          </div>
        </dialog>
      </div>
    </teleport>
  </div>
</template>

<style scoped>
/* Container principal */
.tasks-view {
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-2xl);
}

/* Error Banner */
.error-banner {
  display: flex;
  align-items: center;
  gap: var(--ds-space-md);
  padding: var(--ds-space-md) var(--ds-space-lg);
  background-color: var(--ds-color-error-light);
  border: 1px solid var(--ds-color-error);
  border-radius: var(--ds-radius-lg);
  color: #7f1d1d;
  font-size: var(--ds-font-size-sm);
  animation: slideDown 0.3s ease-out;
}

.error-icon {
  font-size: 1.25rem;
  flex-shrink: 0;
}

.error-close {
  margin-left: auto;
  background: none;
  border: none;
  cursor: pointer;
  color: inherit;
  font-size: 1.25rem;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  min-height: 32px;
  border-radius: var(--ds-radius-md);
  transition: background-color var(--ds-transition-fast);
}

.error-close:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Toolbar */
.toolbar {
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-lg);
}

.toolbar-header h1 {
  margin: 0;
  font-size: var(--ds-font-size-2xl);
  color: var(--ds-text-primary);
}

.toolbar-controls {
  display: flex;
  flex-wrap: wrap;
  gap: var(--ds-space-md);
  align-items: center;
}

/* Search Group */
.search-group {
  position: relative;
  flex: 1;
  min-width: 200px;
}

.search-input {
  width: 100%;
  padding: var(--ds-space-sm) var(--ds-space-md) var(--ds-space-sm) 2.5rem;
  border: 1px solid var(--ds-border-color);
  border-radius: var(--ds-radius-lg);
  font-size: var(--ds-font-size-sm);
  background-color: var(--ds-bg-secondary);
  color: white;
  transition: all var(--ds-transition-fast);
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.search-input:hover {
  border-color: var(--ds-border-color-hover);
}

.search-input:focus {
  outline: none;
  border-color: var(--ds-color-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.search-icon {
  position: absolute;
  left: var(--ds-space-md);
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: var(--ds-text-tertiary);
}

/* Filter Chips */
.filter-chips {
  display: flex;
  gap: var(--ds-space-sm);
  flex-wrap: wrap;
}

.filter-chip {
  padding: var(--ds-space-sm) var(--ds-space-md);
  border: 1px solid var(--ds-border-color);
  background-color: var(--ds-bg-primary);
  color: var(--ds-text-secondary);
  border-radius: var(--ds-radius-full);
  font-size: var(--ds-font-size-sm);
  font-weight: var(--ds-font-weight-medium);
  cursor: pointer;
  transition: all var(--ds-transition-fast);
}

.filter-chip:hover {
  border-color: var(--ds-color-primary);
  color: var(--ds-color-primary);
}

.filter-chip.active {
  background-color: var(--ds-color-primary);
  color: white;
  border-color: var(--ds-color-primary);
}

/* Buttons */
.btn {
  padding: var(--ds-space-sm) var(--ds-space-lg);
  border-radius: var(--ds-radius-lg);
  font-size: var(--ds-font-size-sm);
  font-weight: var(--ds-font-weight-medium);
  cursor: pointer;
  border: none;
  transition: all var(--ds-transition-fast);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--ds-space-sm);
}

.btn:focus {
  outline: none;
}

.btn-primary {
  background-color: var(--ds-color-primary);
  color: white;
}

.btn-primary:hover {
  background-color: var(--ds-color-primary-dark);
}

.btn-primary:focus {
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.btn-secondary {
  background-color: var(--ds-bg-tertiary);
  color: var(--ds-text-primary);
  border: 1px solid var(--ds-border-color);
}

.btn-secondary:hover {
  background-color: var(--ds-border-color);
}

.btn-danger {
  background-color: var(--ds-color-error);
  color: white;
}

.btn-danger:hover {
  background-color: #991b1b;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--ds-space-lg);
  padding: var(--ds-space-3xl) var(--ds-space-xl);
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--ds-border-color);
  border-top-color: var(--ds-color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  color: var(--ds-text-secondary);
  font-size: var(--ds-font-size-sm);
  margin: 0;
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--ds-space-lg);
  padding: var(--ds-space-3xl) var(--ds-space-xl);
  text-align: center;
  border: 2px dashed var(--ds-border-color);
  border-radius: var(--ds-radius-xl);
  background-color: var(--ds-bg-secondary);
}

.empty-icon {
  font-size: 3rem;
}

.empty-state h2 {
  margin: 0;
  color: var(--ds-text-primary);
}

.empty-state p {
  margin: 0;
  color: var(--ds-text-secondary);
  font-size: var(--ds-font-size-sm);
}

/* Tasks Grid */
.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--ds-space-lg);
}

/* Task Card */
.task-card {
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-md);
  padding: var(--ds-space-lg);
  background-color: var(--ds-bg-primary);
  border: 1px solid var(--ds-border-color);
  border-radius: var(--ds-radius-lg);
  box-shadow: var(--ds-shadow-sm);
  transition: all var(--ds-transition-base);
}

.task-card:hover {
  border-color: var(--ds-border-color-hover);
  box-shadow: var(--ds-shadow-md);
}

.task-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--ds-space-md);
}

.task-title-group {
  display: flex;
  align-items: center;
  gap: var(--ds-space-md);
  flex: 1;
  min-width: 0;
}

.task-checkbox {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  cursor: pointer;
  accent-color: var(--ds-color-primary);
}

.task-title {
  margin: 0;
  font-size: var(--ds-font-size-base);
  font-weight: var(--ds-font-weight-semibold);
  color: var(--ds-text-primary);
  transition: all var(--ds-transition-fast);
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  word-break: break-word;
  line-height: 1.4;
  max-height: calc(1.4em * 2);
}

.task-title.completed {
  text-decoration: line-through;
  color: var(--ds-text-tertiary);
}

.task-header-right {
  display: flex;
  align-items: center;
  gap: var(--ds-space-sm);
  flex-shrink: 0;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: var(--ds-radius-full);
  font-size: 0.75rem;
  font-weight: var(--ds-font-weight-semibold);
  white-space: nowrap;
  flex-shrink: 0;
}

.status-badge.pending {
  background-color: var(--ds-color-warning-light);
  color: #92400e;
}

.status-badge.done {
  background-color: var(--ds-color-success-light);
  color: #166534;
}

/* Task Actions */
.task-actions {
  display: flex;
  gap: var(--ds-space-sm);
  flex-shrink: 0;
}

.action-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  border: 1px solid var(--ds-border-color);
  background-color: var(--ds-bg-secondary);
  border-radius: var(--ds-radius-md);
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--ds-transition-fast);
  position: relative;
}

.action-btn:hover {
  background-color: var(--ds-bg-tertiary);
}

.action-btn.btn-edit {
  color: #fbbf24;
  font-weight: bold;
}

.action-btn.btn-edit:hover {
  border-color: #fbbf24;
  color: #fbbf24;
  background-color: rgba(251, 191, 36, 0.15);
  box-shadow: 0 0 8px rgba(251, 191, 36, 0.3);
}

.action-btn.btn-delete:hover {
  border-color: var(--ds-color-error);
  color: var(--ds-color-error);
}

/* Tooltip Styling */
.action-btn[data-tooltip] {
  position: relative;
}

.action-btn[data-tooltip]::before {
  content: attr(data-tooltip);
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: var(--ds-bg-tertiary);
  color: var(--ds-text-primary);
  padding: 0.5rem 0.75rem;
  border-radius: var(--ds-radius-md);
  font-size: 0.75rem;
  font-weight: var(--ds-font-weight-semibold);
  white-space: nowrap;
  border: 1px solid var(--ds-border-color);
  box-shadow: var(--ds-shadow-md);
  opacity: 0;
  visibility: hidden;
  transition: all var(--ds-transition-fast);
  pointer-events: none;
  margin-bottom: 0.5rem;
  z-index: 1000;
}

.action-btn[data-tooltip]::after {
  content: '';
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 5px solid transparent;
  border-top-color: var(--ds-bg-tertiary);
  opacity: 0;
  visibility: hidden;
  transition: all var(--ds-transition-fast);
  pointer-events: none;
  margin-bottom: -5px;
  z-index: 1000;
}

.action-btn[data-tooltip]:hover::before {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(-4px);
}

.action-btn[data-tooltip]:hover::after {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(-4px);
}

/* Task Description */
.task-description {
  color: var(--ds-text-secondary);
  font-size: var(--ds-font-size-sm);
  line-height: 1.5;
  word-break: break-word;
}

/* Task Meta */
.task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--ds-space-md);
  font-size: 0.75rem;
  color: var(--ds-text-tertiary);
  border-top: 1px solid var(--ds-border-color);
  padding-top: var(--ds-space-md);
}

.meta-label {
  font-weight: var(--ds-font-weight-semibold);
}

/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--ds-z-modal);
  padding: var(--ds-space-md);
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Modal Dialog */
.modal-dialog {
  background-color: var(--ds-bg-primary);
  border: none;
  border-radius: var(--ds-radius-xl);
  box-shadow: var(--ds-shadow-xl);
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-dialog.modal-confirm {
  max-width: 400px;
}

/* Modal Header */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--ds-space-lg);
  border-bottom: 1px solid var(--ds-border-color);
}

.modal-header h2 {
  margin: 0;
  font-size: var(--ds-font-size-xl);
  color: var(--ds-text-primary);
}

.modal-close {
  width: 32px;
  height: 32px;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.25rem;
  color: var(--ds-text-secondary);
  border-radius: var(--ds-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--ds-transition-fast);
}

.modal-close:hover {
  background-color: var(--ds-bg-secondary);
  color: var(--ds-text-primary);
}

/* Modal Body */
.modal-body {
  padding: var(--ds-space-lg);
}

.modal-body p {
  margin: 0 0 var(--ds-space-md) 0;
  color: var(--ds-text-secondary);
  font-size: var(--ds-font-size-sm);
}

.modal-task-title {
  font-weight: var(--ds-font-weight-semibold);
  color: var(--ds-text-primary);
  padding: var(--ds-space-md);
  background-color: var(--ds-bg-secondary);
  border-radius: var(--ds-radius-md);
  border-left: 3px solid var(--ds-color-primary);
}

.modal-warning {
  color: var(--ds-color-error);
  font-weight: var(--ds-font-weight-medium);
}

/* Modal Form */
.modal-form {
  padding: var(--ds-space-lg);
}

/* Form Groups */
.form-group {
  margin-bottom: var(--ds-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-sm);
}

.form-label {
  font-weight: var(--ds-font-weight-semibold);
  color: var(--ds-text-primary);
  font-size: var(--ds-font-size-sm);
}

.required {
  color: var(--ds-color-error);
}

.optional {
  color: var(--ds-text-tertiary);
  font-weight: var(--ds-font-weight-normal);
}

.form-input,
.form-textarea,
.form-select {
  padding: var(--ds-space-sm) var(--ds-space-md);
  border: 1px solid var(--ds-border-color);
  border-radius: var(--ds-radius-md);
  font-size: var(--ds-font-size-sm);
  background-color: var(--ds-bg-secondary);
  color: var(--ds-text-primary);
  font-family: inherit;
  transition: all var(--ds-transition-fast);
}

.form-input:hover,
.form-textarea:hover,
.form-select:hover {
  border-color: var(--ds-border-color-hover);
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: var(--ds-color-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
  max-height: 300px;
}

.form-error {
  color: var(--ds-color-error);
  font-size: var(--ds-font-size-xs);
  font-weight: var(--ds-font-weight-medium);
}

.form-counter {
  color: var(--ds-text-tertiary);
  font-size: var(--ds-font-size-xs);
  text-align: right;
}

/* Form Actions */
.form-actions {
  display: flex;
  gap: var(--ds-space-md);
  justify-content: flex-end;
  padding: var(--ds-space-lg) 0 0 0;
  border-top: 1px solid var(--ds-border-color);
}

/* Responsive */
@media (max-width: 768px) {
  .toolbar-controls {
    flex-direction: column;
    width: 100%;
  }

  .search-group {
    min-width: unset;
  }

  .filter-chips {
    width: 100%;
    justify-content: flex-start;
  }

  .btn-primary {
    width: 100%;
  }

  .tasks-grid {
    grid-template-columns: 1fr;
  }

  .modal-dialog {
    max-width: 90vw;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions .btn {
    width: 100%;
  }
}

/* Pagination */
.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--ds-space-lg);
  padding: var(--ds-space-2xl) var(--ds-space-lg);
  margin-top: var(--ds-space-2xl);
}

.pagination-btn {
  padding: var(--ds-space-md) var(--ds-space-lg);
  background-color: var(--ds-color-primary);
  color: white;
  border: none;
  border-radius: var(--ds-radius-lg);
  font-size: var(--ds-font-size-sm);
  font-weight: var(--ds-font-weight-semibold);
  cursor: pointer;
  transition: all var(--ds-transition-fast);
  min-width: 120px;
}

.pagination-btn:hover:not(:disabled) {
  background-color: var(--ds-color-primary-hover, #1e40af);
  box-shadow: var(--ds-shadow-md);
  transform: translateY(-2px);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: var(--ds-bg-secondary);
  color: var(--ds-text-tertiary);
}

@media (max-width: 480px) {
  .toolbar-header h1 {
    font-size: var(--ds-font-size-xl);
  }

  .task-card-header {
    flex-direction: column;
  }

  .task-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .modal-overlay {
    padding: var(--ds-space-sm);
  }

  .modal-dialog {
    max-width: 100%;
  }

  .task-title-group {
    gap: var(--ds-space-sm);
  }

  .pagination-container {
    flex-direction: column;
    gap: var(--ds-space-md);
    padding: var(--ds-space-lg);
  }

  .pagination-btn {
    width: 100%;
    min-width: unset;
  }
}
</style>
