package br.com.jtech.tasklist.integration;

import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests with H2 database (SpringBootTest).
 * Tests the complete flow: REST controller → Use Case → JPA → Flyway Migrations
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Cleanup tasks before each test (optional, depends on test isolation needs)
        // In this case, Flyway migrations and schema reset handle it
    }

    @Test
    void testCreateTaskAndRetrieveIt() throws Exception {
        // Create a task
        String createRequest = "{\"title\": \"Integration Test Task\", \"description\": \"Testing JPA and Flyway\", \"status\": \"pendente\"}";
        
        String response = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.status").value("pendente"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskResponse createdTask = objectMapper.readValue(response, TaskResponse.class);

        // Retrieve the created task
        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTask.id()))
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.description").value("Testing JPA and Flyway"))
                .andExpect(jsonPath("$.status").value("pendente"));
    }

    @Test
    void testUpdateTaskPartially() throws Exception {
        // Create a task
        String createRequest = "{\"title\": \"Original Title\", \"description\": \"Original Description\", \"status\": \"pendente\"}";
        
        String createResponse = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskResponse createdTask = objectMapper.readValue(createResponse, TaskResponse.class);

        // Update only the status
        String updateRequest = "{\"status\": \"concluída\"}";
        
        mockMvc.perform(put("/tasks/" + createdTask.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original Title")) // Title unchanged
                .andExpect(jsonPath("$.description").value("Original Description")) // Description unchanged
                .andExpect(jsonPath("$.status").value("concluída")); // Status updated
    }

    @Test
    void testListTasksWithPagination() throws Exception {
        // Create multiple tasks
        for (int i = 1; i <= 15; i++) {
            String createRequest = "{\"title\": \"Task " + i + "\", \"description\": \"Description " + i + "\", \"status\": \"pendente\"}";
            mockMvc.perform(post("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createRequest))
                    .andExpect(status().isCreated());
        }

        // List first page with 10 items
        mockMvc.perform(get("/tasks?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(greaterThan(10)))
                .andExpect(jsonPath("$.totalPages").value(greaterThan(1)));

        // List second page
        mockMvc.perform(get("/tasks?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(greaterThan(0)));
    }

    @Test
    void testListTasksWithStatusFilter() throws Exception {
        // Create tasks with different statuses
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Pending Task\", \"description\": \"Test\", \"status\": \"pendente\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Done Task 1\", \"description\": \"Test\", \"status\": \"concluída\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Done Task 2\", \"description\": \"Test\", \"status\": \"concluída\"}"))
                .andExpect(status().isCreated());

        // Filter by status=concluída
        mockMvc.perform(get("/tasks?page=0&size=10&status=concluída"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].status").value("concluída"))
                .andExpect(jsonPath("$.content[1].status").value("concluída"));

        // Filter by status=pendente
        mockMvc.perform(get("/tasks?page=0&size=10&status=pendente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("pendente"));
    }

    @Test
    void testDeleteTask() throws Exception {
        // Create a task
        String createRequest = "{\"title\": \"Task to Delete\", \"description\": \"Test\", \"status\": \"pendente\"}";
        
        String createResponse = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskResponse createdTask = objectMapper.readValue(createResponse, TaskResponse.class);

        // Delete the task
        mockMvc.perform(delete("/tasks/" + createdTask.id()))
                .andExpect(status().isNoContent());

        // Verify task is deleted
        mockMvc.perform(get("/tasks/" + createdTask.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidStatusReturns400() throws Exception {
        String invalidRequest = "{\"title\": \"Test\", \"description\": \"Test\", \"status\": \"invalidstatus\"}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTitleExceedingMaxLengthReturns400() throws Exception {
        String longTitle = "a".repeat(121); // 121 characters (max is 120)
        String createRequest = "{\"title\": \"" + longTitle + "\", \"description\": \"Test\", \"status\": \"pendente\"}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFlywayMigrationExecuted() throws Exception {
        // If Flyway migrations executed successfully, we should be able to create tasks
        // This proves that:
        // 1. The database schema was created
        // 2. Seed data was inserted (V2__seed_tasks.sql)
        
        mockMvc.perform(get("/tasks?page=0&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(greaterThan(0))) // Seed data exists
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)));
    }
}
