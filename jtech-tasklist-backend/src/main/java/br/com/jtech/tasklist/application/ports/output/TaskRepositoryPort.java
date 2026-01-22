package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);

    Optional<Task> findById(Long id);

    Page<Task> findAll(Pageable pageable, TaskStatus status);

    Page<Task> findAll(Pageable pageable);

    void deleteById(Long id);
}
