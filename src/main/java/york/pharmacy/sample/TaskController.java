package york.pharmacy.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import york.pharmacy.sample.dto.TaskRequest;
import york.pharmacy.sample.dto.TaskResponse;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Create a new task
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    // Create multiple tasks (batch)
    @PostMapping("/batch")
    public ResponseEntity<List<TaskResponse>> batchCreateTasks(@Valid @RequestBody List<TaskRequest> taskRequests) {
        List<TaskResponse> taskResponses = taskService.batchCreateTasks(taskRequests);
        return new ResponseEntity<>(taskResponses, HttpStatus.CREATED);
    }

    // Get all tasks
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Get a task by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getTaskById(id);
        return ResponseEntity.ok(taskResponse);
    }

    // Update an existing task by its ID
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(taskResponse);
    }

    // Delete a task by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}

