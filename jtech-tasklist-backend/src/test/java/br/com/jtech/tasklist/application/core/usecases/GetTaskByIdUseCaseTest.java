package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class GetTaskByIdUseCaseTest {
    @Mock
    private TaskRepositoryPort taskRepository;

    private GetTaskByIdUseCase getTaskByIdUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getTaskByIdUseCase = new GetTaskByIdUseCase(taskRepository);
    }

    @Test
    void testGetTaskByIdSuccess() throws TaskNotFoundException {
        Long taskId = 1L;
        Task task = new Task(taskId, "Test Task", "Test Description", TaskStatus.PENDING, null, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = getTaskByIdUseCase.execute(taskId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void testGetTaskByIdNotFound() {
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getTaskByIdUseCase.execute(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task with id 999 was not found");
    }
}
