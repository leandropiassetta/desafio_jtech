package br.com.jtech.tasklist.application.core.domains;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Task(Long id, String title, String description, TaskStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
