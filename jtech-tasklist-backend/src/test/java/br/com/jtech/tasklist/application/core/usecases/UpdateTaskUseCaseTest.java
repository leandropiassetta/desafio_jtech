package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateTaskUseCaseTest {
    @Mock
    private TaskRepositoryPort taskRepository;

    private UpdateTaskUseCase updateTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateTaskUseCase = new UpdateTaskUseCase(taskRepository);
    }

    @Test
    void testUpdateTaskSuccess() throws TaskNotFoundException {
        Long taskId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        TaskStatus newStatus = TaskStatus.DONE;

        Task existingTask = new Task(taskId, "Old Title", "Old Description", TaskStatus.PENDING, Instant.now(), Instant.now());
        Task updatedTask = new Task(taskId, newTitle, newDescription, newStatus, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = updateTaskUseCase.execute(taskId, newTitle, newDescription, newStatus);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.getDescription()).isEqualTo(newDescription);
        assertThat(result.getStatus()).isEqualTo(newStatus);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskWithNullDescription() throws TaskNotFoundException {
        Long taskId = 1L;
        String newTitle = "Updated Title";
        TaskStatus newStatus = TaskStatus.DONE;

        Task existingTask = new Task(taskId, "Old Title", "Old Description", TaskStatus.PENDING, Instant.now(), Instant.now());
        Task updatedTask = new Task(taskId, newTitle, null, newStatus, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = updateTaskUseCase.execute(taskId, newTitle, null, newStatus);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskNotFound() {
        Long taskId = 999L;
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateTaskUseCase.execute(taskId, "Title", "Description", TaskStatus.PENDING))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task with id 999 was not found");

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskOnlyTitle() throws TaskNotFoundException {
        Long taskId = 1L;
        String newTitle = "Only Title Updated";
        String existingDescription = "Existing Description";
        TaskStatus existingStatus = TaskStatus.PENDING;

        Task existingTask = new Task(taskId, "Old Title", existingDescription, existingStatus, Instant.now(), Instant.now());
        Task updatedTask = new Task(taskId, newTitle, existingDescription, existingStatus, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = updateTaskUseCase.execute(taskId, newTitle, existingDescription, existingStatus);

        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.getDescription()).isEqualTo(existingDescription);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatusChange() throws TaskNotFoundException {
        Long taskId = 1L;
        Task existingTask = new Task(taskId, "Title", "Description", TaskStatus.PENDING, Instant.now(), Instant.now());
        Task updatedTask = new Task(taskId, "Title", "Description", TaskStatus.DONE, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = updateTaskUseCase.execute(taskId, "Title", "Description", TaskStatus.DONE);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
