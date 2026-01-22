package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateTaskUseCaseTest {
    @Mock
    private TaskRepositoryPort taskRepository;

    private CreateTaskUseCase createTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createTaskUseCase = new CreateTaskUseCase(taskRepository);
    }

    @Test
    void testCreateTaskWithStatusProvided() {
        String title = "Test Task";
        String description = "Test Description";
        TaskStatus status = TaskStatus.DONE;

        Task savedTask = new Task(1L, title, description, status, null, null);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = createTaskUseCase.execute(title, description, status);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTaskWithoutStatusDefaultsToPending() {
        String title = "Test Task";
        String description = "Test Description";

        Task savedTask = new Task(1L, title, description, TaskStatus.PENDING, null, null);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = createTaskUseCase.execute(title, description, null);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
