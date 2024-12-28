package york.pharmacy.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.sample.dto.TaskRequest;
import york.pharmacy.sample.dto.TaskResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setCompleted(false);

        taskRequest = new TaskRequest();
        taskRequest.setName("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setCompleted(false);
    }

    /** Test: createTask - Success */
    @Test
    void testCreateTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse result = taskService.createTask(taskRequest);

        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /** Test: batchCreateTasks - Success */
    @Test
    void testBatchCreateTasks_Success() {
        List<TaskRequest> taskRequests = List.of(taskRequest);
        List<Task> tasks = List.of(task);
        when(taskRepository.saveAll(anyList())).thenReturn(tasks);

        List<TaskResponse> result = taskService.batchCreateTasks(taskRequests);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
        verify(taskRepository, times(1)).saveAll(anyList());
    }

    /** Test: getAllTasks - Success */
    @Test
    void testGetAllTasks_Success() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
        verify(taskRepository, times(1)).findAll();
    }

    /** Test: getTaskById - Success */
    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        verify(taskRepository, times(1)).findById(1L);
    }

    /** Test: updateTask - Success */
    @Test
    void testUpdateTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRequest updatedRequest = new TaskRequest();
        updatedRequest.setName("Updated Task");
        updatedRequest.setDescription("Updated Description");
        updatedRequest.setCompleted(true);

        TaskResponse result = taskService.updateTask(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Updated Task", result.getName());
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /** Test: deleteTask - Success */
    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    /** Test: deleteTask - Not Found */
    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}
