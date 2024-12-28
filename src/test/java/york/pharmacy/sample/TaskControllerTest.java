package york.pharmacy.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import york.pharmacy.sample.dto.TaskRequest;
import york.pharmacy.sample.dto.TaskResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    private TaskController taskController;

    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        taskController = new TaskController(taskService);

        taskRequest = new TaskRequest();
        taskRequest.setName("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setCompleted(false);

        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setName("Test Task");
        taskResponse.setDescription("Test Description");
        taskResponse.setCompleted(false);
    }

    /** Test: createTask - Success */
    @Test
    void testCreateTask_Success() {
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        ResponseEntity<TaskResponse> response = taskController.createTask(taskRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Task", response.getBody().getName());
        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }

    /** Test: batchCreateTasks - Success */
    @Test
    void testBatchCreateTasks_Success() {
        List<TaskRequest> taskRequests = List.of(taskRequest);
        List<TaskResponse> taskResponses = List.of(taskResponse);

        when(taskService.batchCreateTasks(anyList())).thenReturn(taskResponses);

        ResponseEntity<List<TaskResponse>> response = taskController.batchCreateTasks(taskRequests);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getName());
        verify(taskService, times(1)).batchCreateTasks(anyList());
    }

    /** Test: getAllTasks - Success */
    @Test
    void testGetAllTasks_Success() {
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse));

        ResponseEntity<List<TaskResponse>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(taskService, times(1)).getAllTasks();
    }

    /** Test: getTaskById - Success */
    @Test
    void testGetTaskById_Success() {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        ResponseEntity<TaskResponse> response = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Task", response.getBody().getName());
        verify(taskService, times(1)).getTaskById(1L);
    }

    /** Test: updateTask - Success */
    @Test
    void testUpdateTask_Success() {
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(taskResponse);

        ResponseEntity<TaskResponse> response = taskController.updateTask(1L, taskRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Task", response.getBody().getName());
        verify(taskService, times(1)).updateTask(eq(1L), any(TaskRequest.class));
    }

    /** Test: deleteTask - Success */
    @Test
    void testDeleteTask_Success() {
        doNothing().when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(1L);
    }
}
