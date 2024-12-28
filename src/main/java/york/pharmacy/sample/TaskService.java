package york.pharmacy.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.sample.dto.TaskRequest;
import york.pharmacy.sample.dto.TaskResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // Create one task
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = TaskMapper.toEntity(taskRequest);
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }

    // Create batch of tasks
    public List<TaskResponse> batchCreateTasks(List<TaskRequest> taskRequests) {
        List<Task> tasks = taskRequests.stream()
                .map(TaskMapper::toEntity)
                .collect(Collectors.toList());
        List<Task> savedTasks = taskRepository.saveAll(tasks);
        return savedTasks.stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Read all tasks
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Read one task by ID
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + id + " not found"));
        return TaskMapper.toResponse(task);
    }

    // Update one task by ID
    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + id + " not found"));

        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.isCompleted());

        Task updatedTask = taskRepository.save(task);
        return TaskMapper.toResponse(updatedTask);
    }

    // Delete one task by ID
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with ID " + id + " not found");
        }
        taskRepository.deleteById(id);
    }
}
