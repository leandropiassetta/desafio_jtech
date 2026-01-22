package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteTaskUseCase {
    private final TaskRepositoryPort taskRepository;

    public void execute(Long id) throws TaskNotFoundException {
        if (!taskRepository.findById(id).isPresent()) {
            throw new TaskNotFoundException("Task with id " + id + " was not found");
        }
        taskRepository.deleteById(id);
    }
}
