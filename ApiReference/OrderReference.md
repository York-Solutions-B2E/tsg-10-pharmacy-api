
# Order API Reference

## 1. Create an Order
**Endpoint**: `POST /api/orders`  
**Method**: POST  
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
{
  "medicineId": 1,
  "quantity": 100,
  "deliveryDate": "2025-01-03"
}
```

**Response**:
```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2025-01-02T22:03:19.284986Z",
    "updatedAt": "2025-01-02T22:03:19.284986Z"
  },
  "quantity": 100,
  "deliveryDate": "2025-01-03",
  "status": "ORDERED",
  "createdAt": "2025-01-02T22:04:41.328750200Z",
  "updatedAt": "2025-01-02T22:04:41.328750200Z"
}
```

---

## 2. Create Multiple Orders (Batch)
**Endpoint**: `POST /api/orders/batch`  
**Method**: POST  
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
[
  {
    "medicineId": 1,
    "quantity": 100,
    "deliveryDate": "2025-01-15"
  },
  {
    "medicineId": 2,
    "quantity": 50,
    "deliveryDate": "2025-01-28"
  }
]
```

**Response**:
```json
[
  {
    "id": 2,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "quantity": 100,
    "deliveryDate": "2025-01-15",
    "status": "ORDERED",
    "createdAt": "2025-01-02T22:06:10.994086Z",
    "updatedAt": "2025-01-02T22:06:10.994086Z"
  },
  {
    "id": 3,
    "medicine": {
      "id": 2,
      "name": "Lollipoprin",
      "code": "LPX-005",
      "createdAt": "2025-01-02T22:03:19.362863Z",
      "updatedAt": "2025-01-02T22:03:19.362863Z"
    },
    "quantity": 50,
    "deliveryDate": "2025-01-28",
    "status": "ORDERED",
    "createdAt": "2025-01-02T22:06:10.996796500Z",
    "updatedAt": "2025-01-02T22:06:10.996796500Z"
  }
]
```

---

## 3. Get All Orders
**Endpoint**: `GET /api/orders`  
**Method**: GET

**Response**:
```json
[
  {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "quantity": 100,
    "deliveryDate": "2025-01-03",
    "status": "ORDERED",
    "createdAt": "2025-01-02T22:04:41.328750Z",
    "updatedAt": "2025-01-02T22:04:41.328750Z"
  },
  {
    "id": 2,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "quantity": 100,
    "deliveryDate": "2025-01-15",
    "status": "ORDERED",
    "createdAt": "2025-01-02T22:06:10.994086Z",
    "updatedAt": "2025-01-02T22:06:10.994086Z"
  },
  {
    "id": 3,
    "medicine": {
      "id": 2,
      "name": "Lollipoprin",
      "code": "LPX-005",
      "createdAt": "2025-01-02T22:03:19.362863Z",
      "updatedAt": "2025-01-02T22:03:19.362863Z"
    },
    "quantity": 50,
    "deliveryDate": "2025-01-28",
    "status": "ORDERED",
    "createdAt": "2025-01-02T22:06:10.996797Z",
    "updatedAt": "2025-01-02T22:06:10.996797Z"
  }
]
```

---

## 4. Get an Order by ID
**Endpoint**: `GET /api/orders/{id}`  
**Method**: GET

**Example**: `GET /api/orders/1`

**Response**:
```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2025-01-02T22:03:19.284986Z",
    "updatedAt": "2025-01-02T22:03:19.284986Z"
  },
  "quantity": 100,
  "deliveryDate": "2025-01-03",
  "status": "ORDERED",
  "createdAt": "2025-01-02T22:04:41.328750Z",
  "updatedAt": "2025-01-02T22:04:41.328750Z"
}
```

---

## 5. Get Delivery Dates by Medicine ID
**Endpoint**: `GET /api/orders/delivery-dates/{medicineId}`  
**Method**: GET

**Example**: `GET /api/orders/delivery-dates/1`

**Response**:
```json
[
  {
    "orderId": 1,
    "deliveryDate": "2025-01-03"
  },
  {
    "orderId": 2,
    "deliveryDate": "2025-01-15"
  }
]
```

---

## 6. Update an Order
**Endpoint**: `PUT /api/orders/{id}`  
**Method**: PUT  
**Headers**:
- Content-Type: `application/json`

**Example**: `PUT /api/orders/1`

**Request Body**:
```json
{
  "medicineId": 1,
  "quantity": 150,
  "deliveryDate": "2025-01-03",
  "status": "ORDERED"
}
```

**Response**:
```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2025-01-02T22:03:19.284986Z",
    "updatedAt": "2025-01-02T22:03:19.284986Z"
  },
  "quantity": 150,
  "deliveryDate": "2025-01-03",
  "status": "ORDERED",
  "createdAt": "2025-01-02T22:04:41.328750Z",
  "updatedAt": "2025-01-02T22:04:41.328750Z"
}
```

---

## 7. Update Order Status to "RECEIVED"
**Endpoint**: `PUT /api/orders/received/{id}`  
**Method**: PUT

**Example**: `PUT /api/orders/received/1`

**Response**:
```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2025-01-02T22:03:19.284986Z",
    "updatedAt": "2025-01-02T22:03:19.284986Z"
  },
  "quantity": 150,
  "deliveryDate": "2025-01-03",
  "status": "RECEIVED",
  "createdAt": "2025-01-02T22:04:41.328750Z",
  "updatedAt": "2025-01-02T22:10:31.715257800Z"
}
```

---

## 8. Delete an Order by ID
**Endpoint**: `DELETE /api/orders/{id}`  
**Method**: DELETE

**Example**: `DELETE /api/orders/1`

**Response**:
- Status `204 No Content`.
