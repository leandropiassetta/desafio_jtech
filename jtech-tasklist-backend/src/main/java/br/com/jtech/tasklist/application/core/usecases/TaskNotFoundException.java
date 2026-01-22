package br.com.jtech.tasklist.application.core.usecases;

public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
