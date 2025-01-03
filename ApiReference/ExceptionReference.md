# Backend Exception Reference

## Notes for Frontend Developers
- Use the `status` field to categorize errors (e.g., `400`, `404`, `409`, `500`).
- Show user-friendly error messages based on the `error` and `message` fields.
- For debugging or logging, utilize the `details` field if available.

This document outlines the exception formats returned by the backend. Frontend applications should handle these exceptions gracefully based on the `status` and `error` fields.

---

## Validation Error (400 Bad Request)
**Condition**: Triggered when input validation fails, e.g., missing required fields or invalid data format.

**Example**:
```json
{
  "details": {
    "code": "Medicine Code cannot be null"
  },
  "error": "Bad Request",
  "message": "Validation Failed",
  "timestamp": "2025-01-02T19:37:14.9556934",
  "status": 400
}
```

---

## Resource Not Found (404 Not Found)
**Condition**: Triggered when a requested resource does not exist, e.g., fetching a nonexistent order.

**Example**:
```json
{
  "error": "Not Found",
  "message": "Order with ID 1 not found",
  "timestamp": "2025-01-02T19:27:26.4056809",
  "status": 404
}
```

---

## Database Constraint Violation (409 Conflict)
**Condition**: Triggered when a requested resource violates a database constraint.

**Example**:
```json
{
  "details": "Unique index or primary key violation: \"PUBLIC.CONSTRAINT_INDEX_C ON PUBLIC.medicine(code NULLS FIRST) VALUES ( /* 1 */ 'LPX-005' )\"; SQL statement:\ninsert into medicine (code,created_at,name,updated_at,id) values (?,?,?,?,default) [23505-232]",
  "error": "Conflict",
  "message": "Database constraint violation",
  "timestamp": "2025-01-02T19:33:21.4851046",
  "status": 409
}
```

---

## Internal Server Error (500 Internal Server Error)
**Condition**: Triggered when an unexpected error occurs or when an unhandled exception is raised.

**Example**:
```json
{
  "details": "No static resource api/medicines.",
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2025-01-02T19:41:43.4631596",
  "status": 500
}
