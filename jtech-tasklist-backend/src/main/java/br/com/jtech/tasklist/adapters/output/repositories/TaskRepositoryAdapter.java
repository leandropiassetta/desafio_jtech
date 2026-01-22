package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskRepositoryAdapter implements TaskRepositoryPort {
    private final TaskSpringDataRepository taskSpringDataRepository;

    public TaskRepositoryAdapter(TaskSpringDataRepository taskSpringDataRepository) {
        this.taskSpringDataRepository = taskSpringDataRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = toEntity(task);
        TaskEntity saved = taskSpringDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskSpringDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<Task> findAll(Pageable pageable, TaskStatus status) {
        return taskSpringDataRepository.findByStatus(status, pageable).map(this::toDomain);
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        return taskSpringDataRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        taskSpringDataRepository.deleteById(id);
    }

    private TaskEntity toEntity(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setStatus(task.getStatus());
        entity.setCreatedAt(task.getCreatedAt());
        entity.setUpdatedAt(task.getUpdatedAt());
        return entity;
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
