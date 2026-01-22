package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.application.core.domains.TaskStatus;

import java.util.Optional;

public record UpdateTaskRequest(
    Optional<String> title,

    Optional<String> description,

    Optional<TaskStatus> status
) {}
