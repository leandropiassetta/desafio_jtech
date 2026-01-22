package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ListTasksUseCase {
    private final TaskRepositoryPort taskRepository;

    public Page<Task> execute(Pageable pageable, TaskStatus status) {
        if (status != null) {
            return taskRepository.findAll(pageable, status);
        }
        return taskRepository.findAll(pageable);
    }
}
