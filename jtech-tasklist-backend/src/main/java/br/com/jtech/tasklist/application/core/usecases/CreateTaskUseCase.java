package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTaskUseCase {
    private final TaskRepositoryPort taskRepository;

    public Task execute(String title, String description, TaskStatus status) {
        TaskStatus finalStatus = status != null ? status : TaskStatus.PENDING;
        Task task = new Task(title, description, finalStatus);
        return taskRepository.save(task);
    }
}
