# Medicine API Reference

## 1. Create a Medicine
**Endpoint**: `POST /api/medicines`  
**Method**: POST  
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
{
  "name": "GummyVita",
  "code": "GVX-004"
}
```

**Response**:
```json
{
  "id": 1,
  "name": "GummyVita",
  "code": "GVX-004",
  "createdAt": "2024-12-30T20:46:52.946441500Z",
  "updatedAt": "2024-12-30T20:46:52.946441500Z"
}
```

---

## 2. Create a Batch of Medicines
**Endpoint**: `POST /api/medicines/batch`  
**Method**: POST  
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
[
  {
    "name": "BerryBoost",
    "code": "BBX-014"
  },
  {
    "name": "Lollipoprin",
    "code": "LPX-005"
  }
]
```

**Response**:
```json
[
  {
    "id": 2,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2024-12-30T20:47:19.744882600Z",
    "updatedAt": "2024-12-30T20:47:19.744882600Z"
  },
  {
    "id": 3,
    "name": "Lollipoprin",
    "code": "LPX-005",
    "createdAt": "2024-12-30T20:47:19.746393300Z",
    "updatedAt": "2024-12-30T20:47:19.746393300Z"
  }
]
```

---

## 3. Read All Medicines
**Endpoint**: `GET /api/medicines`  
**Method**: GET

**Response**:
```json
[
  {
    "id": 1,
    "name": "GummyVita",
    "code": "GVX-004",
    "createdAt": "2024-12-30T20:46:52.946442Z",
    "updatedAt": "2024-12-30T20:46:52.946442Z"
  },
  {
    "id": 2,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2024-12-30T20:47:19.744883Z",
    "updatedAt": "2024-12-30T20:47:19.744883Z"
  },
  {
    "id": 3,
    "name": "Lollipoprin",
    "code": "LPX-005",
    "createdAt": "2024-12-30T20:47:19.746393Z",
    "updatedAt": "2024-12-30T20:47:19.746393Z"
  }
]
```

---

## 4. Read a Medicine by ID
**Endpoint**: `GET /api/medicines/{id}`  
**Method**: GET

**Example**: `GET /api/medicines/1`

**Response**:
```json
{
  "id": 1,
  "name": "GummyVita",
  "code": "GVX-004",
  "createdAt": "2024-12-30T20:46:52.946442Z",
  "updatedAt": "2024-12-30T20:46:52.946442Z"
}
```

---

## 5. Update a Medicine by ID
**Endpoint**: `PUT /api/medicines/{id}`  
**Method**: PUT  
**Headers**:
- Content-Type: `application/json`

**Example**: `PUT /api/medicines/1`

**Request Body**:
```json
{
  "name": "BerryBoost",
  "code": "BBX-015"   // Updating the medicine code to 015
}
```

**Response**:
```json
{
  "id": 1,
  "name": "BerryBoost",
  "code": "BBX-015",
  "createdAt": "2024-12-30T20:46:52.946442Z",
  "updatedAt": "2024-12-30T20:49:45.162176600Z"
}
```

---

## 6. Delete a Medicine by ID
**Endpoint**: `DELETE /api/medicines/{id}`  
**Method**: DELETE

**Example**: `DELETE /api/medicines/1`

**Response**:
- Status `204 No Content`.
