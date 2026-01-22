package br.com.jtech.tasklist.config.infra.handlers;

import br.com.jtech.tasklist.application.core.usecases.TaskNotFoundException;
import br.com.jtech.tasklist.config.infra.exceptions.ProblemDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for RFC 7807 Problem Details responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (MethodArgumentNotValidException).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        List<ProblemDetailsResponse.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ProblemDetailsResponse.FieldError(error.getField(), error.getDefaultMessage()));
        }

        ProblemDetailsResponse problemDetails = new ProblemDetailsResponse(
                "about:blank",
                "Validation error",
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request parameters. See errors for details.",
                getRequestUri(request),
                fieldErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    /**
     * Handle TaskNotFoundException.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ProblemDetailsResponse> handleTaskNotFound(
            TaskNotFoundException ex,
            WebRequest request) {
        
        ProblemDetailsResponse problemDetails = new ProblemDetailsResponse(
                "about:blank",
                "Task not found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                getRequestUri(request)
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    /**
     * Handle invalid enum values (status field).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailsResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        
        String detail = ex.getMessage() != null ? ex.getMessage() : "Invalid argument";
        
        ProblemDetailsResponse problemDetails = new ProblemDetailsResponse(
                "about:blank",
                "Bad request",
                HttpStatus.BAD_REQUEST.value(),
                detail,
                getRequestUri(request)
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    /**
     * Handle invalid JSON parsing errors.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetailsResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {
        
        String detail = "Invalid request body. Check the format and values.";

        ProblemDetailsResponse problemDetails = new ProblemDetailsResponse(
                "about:blank",
                "Bad request",
                HttpStatus.BAD_REQUEST.value(),
                detail,
                getRequestUri(request)
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    /**
     * Extract request URI from WebRequest.
     */
    private String getRequestUri(WebRequest request) {
        String uri = request.getDescription(false);
        return uri.startsWith("uri=") ? uri.substring(4) : uri;
    }
}
