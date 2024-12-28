
# Testing TaskController with Postman

This README provides concise instructions to test the `TaskController` endpoints in Postman. Ensure the application is running before testing.

---

## Base URL
```
http://localhost:8080/api/tasks
```

---

## Endpoints

### 1. Create Task
- **URL**: `POST /`
- **Description**: Create a single task.
- **Headers**:
    - `Content-Type: application/json`
- **Body**:
  ```json
  {
    "name": "Sample Task",
    "description": "Task description",
    "completed": false
  }
  ```
- **Expected Response**:
    - **Status**: `201 Created`
    - **Body**:
      ```json
      {
        "id": 1,
        "name": "Sample Task",
        "description": "Task description",
        "completed": false
      }
      ```

---

### 2. Create Batch of Tasks
- **URL**: `POST /batch`
- **Description**: Create multiple tasks.
- **Headers**:
    - `Content-Type: application/json`
- **Body**:
  ```json
  [
    {
      "name": "Task 1",
      "description": "First task description",
      "completed": false
    },
    {
      "name": "Task 2",
      "description": "Second task description",
      "completed": true
    }
  ]
  ```
- **Expected Response**:
    - **Status**: `201 Created`
    - **Body**:
      ```json
      [
        {
          "id": 1,
          "name": "Task 1",
          "description": "First task description",
          "completed": false
        },
        {
          "id": 2,
          "name": "Task 2",
          "description": "Second task description",
          "completed": true
        }
      ]
      ```

---

### 3. Get All Tasks
- **URL**: `GET /`
- **Description**: Retrieve all tasks.
- **Headers**: None
- **Expected Response**:
    - **Status**: `200 OK`
    - **Body**:
      ```json
      [
        {
          "id": 1,
          "name": "Sample Task",
          "description": "Task description",
          "completed": false
        }
      ]
      ```

---

### 4. Get Task by ID
- **URL**: `GET /{id}`
- **Description**: Retrieve a single task by its ID.
- **Headers**: None
- **Path Variable**:
    - `{id}`: ID of the task (e.g., `1`).
- **Expected Response**:
    - **Status**: `200 OK`
    - **Body**:
      ```json
      {
        "id": 1,
        "name": "Sample Task",
        "description": "Task description",
        "completed": false
      }
      ```

---

### 5. Update Task
- **URL**: `PUT /{id}`
- **Description**: Update an existing task by its ID.
- **Headers**:
    - `Content-Type: application/json`
- **Path Variable**:
    - `{id}`: ID of the task (e.g., `1`).
- **Body**:
  ```json
  {
    "name": "Updated Task",
    "description": "Updated description",
    "completed": true
  }
  ```
- **Expected Response**:
    - **Status**: `200 OK`
    - **Body**:
      ```json
      {
        "id": 1,
        "name": "Updated Task",
        "description": "Updated description",
        "completed": true
      }
      ```

---

### 6. Delete Task
- **URL**: `DELETE /{id}`
- **Description**: Delete a task by its ID.
- **Headers**: None
- **Path Variable**:
    - `{id}`: ID of the task (e.g., `1`).
- **Expected Response**:
    - **Status**: `204 No Content`

---

## Notes
- **Prerequisites**:
    - Ensure the application is running on `localhost:8080`.
    - Use a tool like Postman to send HTTP requests.
- **Validation**:
    - Invalid inputs will return `400 Bad Request`.
    - Non-existent IDs will return `404 Not Found`.

---

You can now test each endpoint using the given details in Postman!
