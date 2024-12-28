package york.pharmacy.sample;

import york.pharmacy.sample.dto.TaskRequest;
import york.pharmacy.sample.dto.TaskResponse;

public class TaskMapper {

    // Map TaskRequest to Task
    public static Task toEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.isCompleted());
        return task;
    }

    // Map Task to TaskResponse
    public static TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
}
