package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class UpdateTaskUseCase {
    private final TaskRepositoryPort taskRepository;

    public Task execute(Long id, String title, String description, TaskStatus status) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " was not found"));

        // Only update fields that were provided (not null)
        if (title != null) {
            if (title.isBlank()) {
                throw new IllegalArgumentException("Title must not be blank");
            }
            if (title.length() > 120) {
                throw new IllegalArgumentException("Title must not exceed 120 characters");
            }
            task.setTitle(title);
        }
        if (description != null) {
            if (description.length() > 1000) {
                throw new IllegalArgumentException("Description must not exceed 1000 characters");
            }
            task.setDescription(description);
        }
        if (status != null) {
            task.setStatus(status);
        }
        
        task.setUpdatedAt(Instant.now());

        return taskRepository.save(task);
    }
}
