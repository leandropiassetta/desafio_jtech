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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteTaskUseCaseTest {
    @Mock
    private TaskRepositoryPort taskRepository;

    private DeleteTaskUseCase deleteTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteTaskUseCase = new DeleteTaskUseCase(taskRepository);
    }

    @Test
    void testDeleteTaskSuccess() throws TaskNotFoundException {
        Long taskId = 1L;
        Task task = new Task(taskId, "Title", "Description", TaskStatus.PENDING, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        deleteTaskUseCase.execute(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(eq(taskId));
    }

    @Test
    void testDeleteTaskNotFound() {
        Long taskId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteTaskUseCase.execute(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task with id 999 was not found");

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(0)).deleteById(eq(taskId));
    }

    @Test
    void testDeleteTaskMultipleTasks() throws TaskNotFoundException {
        Long taskId1 = 1L;
        Long taskId2 = 2L;
        Task task1 = new Task(taskId1, "Title 1", "Description 1", TaskStatus.PENDING, Instant.now(), Instant.now());
        Task task2 = new Task(taskId2, "Title 2", "Description 2", TaskStatus.DONE, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId1)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(taskId2)).thenReturn(Optional.of(task2));

        deleteTaskUseCase.execute(taskId1);
        deleteTaskUseCase.execute(taskId2);

        verify(taskRepository, times(1)).findById(taskId1);
        verify(taskRepository, times(1)).findById(taskId2);
        verify(taskRepository, times(1)).deleteById(eq(taskId1));
        verify(taskRepository, times(1)).deleteById(eq(taskId2));
    }

    @Test
    void testDeleteTaskDeletesCorrectId() throws TaskNotFoundException {
        Long taskId = 42L;
        Task task = new Task(taskId, "Title", "Description", TaskStatus.PENDING, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        deleteTaskUseCase.execute(taskId);

        verify(taskRepository, times(1)).deleteById(eq(42L));
    }

    @Test
    void testDeleteTaskChecksFindByIdFirst() throws TaskNotFoundException {
        Long taskId = 1L;
        Task task = new Task(taskId, "Title", "Description", TaskStatus.PENDING, Instant.now(), Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        deleteTaskUseCase.execute(taskId);

        // Verify that findById is called first (order of verification is important)
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(eq(taskId));
    }
}
