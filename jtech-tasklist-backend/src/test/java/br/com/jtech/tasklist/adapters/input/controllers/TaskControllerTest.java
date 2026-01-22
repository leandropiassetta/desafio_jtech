package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.CreateTaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.adapters.input.protocols.UpdateTaskRequest;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import br.com.jtech.tasklist.application.core.usecases.CreateTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.GetTaskByIdUseCase;
import br.com.jtech.tasklist.application.core.usecases.ListTasksUseCase;
import br.com.jtech.tasklist.application.core.usecases.TaskNotFoundException;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTaskUseCase createTaskUseCase;

    @MockBean
    private ListTasksUseCase listTasksUseCase;

    @MockBean
    private GetTaskByIdUseCase getTaskByIdUseCase;

    @MockBean
    private UpdateTaskUseCase updateTaskUseCase;

    @MockBean
    private DeleteTaskUseCase deleteTaskUseCase;

    private Task task;
    private CreateTaskRequest createTaskRequest;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Test Task", "Test Description", TaskStatus.PENDING, Instant.now(), Instant.now());
        createTaskRequest = new CreateTaskRequest("Test Task", "Test Description", TaskStatus.PENDING);
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        when(createTaskUseCase.execute(any(), any(), any())).thenReturn(task);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testCreateTaskValidationError() throws Exception {
        CreateTaskRequest invalidRequest = new CreateTaskRequest("", "", null);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTaskByIdSuccess() throws Exception {
        when(getTaskByIdUseCase.execute(1L)).thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        when(getTaskByIdUseCase.execute(999L)).thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListTasksSuccess() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(Arrays.asList(task));
        when(listTasksUseCase.execute(any(Pageable.class), eq(null))).thenReturn(page);

        mockMvc.perform(get("/tasks?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void testUpdateTaskSuccess() throws Exception {
        UpdateTaskRequest updateRequest = new UpdateTaskRequest("Updated Task", "Updated Description", TaskStatus.DONE);
        Task updatedTask = new Task(1L, "Updated Task", "Updated Description", TaskStatus.DONE, Instant.now(), Instant.now());
        when(updateTaskUseCase.execute(eq(1L), any(), any(), any())).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("concluída"));
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        UpdateTaskRequest updateRequest = new UpdateTaskRequest("Updated Task", "Updated Description", TaskStatus.DONE);
        when(updateTaskUseCase.execute(eq(999L), any(), any(), any()))
                .thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTaskSuccess() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        doThrow(new TaskNotFoundException("Task not found")).when(deleteTaskUseCase).execute(999L);

        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTaskInvalidStatus() throws Exception {
        String invalidRequest = "{\"title\": \"Test\", \"description\": \"Test\", \"status\": \"invalido\"}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void testUpdateTaskTitleExceedsMaxLength() throws Exception {
        String longTitle = "a".repeat(121); // 121 characters
        UpdateTaskRequest updateRequest = new UpdateTaskRequest(longTitle, "Description", TaskStatus.PENDING);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("title"));
    }

    @Test
    void testListTasksWithStatusFilter() throws Exception {
        Task doneTask = new Task(2L, "Done Task", "Done Description", TaskStatus.DONE, Instant.now(), Instant.now());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(Arrays.asList(doneTask));
        
        when(listTasksUseCase.execute(any(Pageable.class), eq(TaskStatus.DONE))).thenReturn(page);

        mockMvc.perform(get("/tasks?page=0&size=10&status=concluída"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].status").value("concluída"));
    }
}
