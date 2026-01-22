package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.CreateTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.GetTaskByIdUseCase;
import br.com.jtech.tasklist.application.core.usecases.ListTasksUseCase;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskUseCase;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskUseCaseConfig {
    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepositoryPort taskRepository) {
        return new CreateTaskUseCase(taskRepository);
    }

    @Bean
    public ListTasksUseCase listTasksUseCase(TaskRepositoryPort taskRepository) {
        return new ListTasksUseCase(taskRepository);
    }

    @Bean
    public GetTaskByIdUseCase getTaskByIdUseCase(TaskRepositoryPort taskRepository) {
        return new GetTaskByIdUseCase(taskRepository);
    }

    @Bean
    public UpdateTaskUseCase updateTaskUseCase(TaskRepositoryPort taskRepository) {
        return new UpdateTaskUseCase(taskRepository);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepositoryPort taskRepository) {
        return new DeleteTaskUseCase(taskRepository);
    }
}
