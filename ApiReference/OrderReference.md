
# Order API Reference

## Create an Order
**Endpoint**: `POST /api/orders`  
**Method**: POST  
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
{
  "inventoryId": 1,
  "quantity": 100,
  "deliveryDate": "2025-01-09"
}
```

**Response**:
```json
{
  "id": 6,
  "inventory": {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "ChocoRelief",
      "code": "CRX-001",
      "createdAt": "2025-01-06T21:09:42.689349Z",
      "updatedAt": "2025-01-06T21:09:42.689349Z"
    },
    "stockQuantity": 100,
    "sufficientStock": true
  },
  "quantity": 100,
  "deliveryDate": "2025-01-09",
  "status": "ORDERED",
  "createdAt": "2025-01-06T21:11:23.986382300Z",
  "updatedAt": "2025-01-06T21:11:23.986382300Z"
}
```

---

## Create Multiple Orders (Batch)
**Endpoint**: `POST /api/orders/batch`  
**Method**: POST  
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
[
  {
    "inventoryId": 1,
    "quantity": 100,
    "deliveryDate": "2025-01-15"
  },
  {
    "inventoryId": 2,
    "quantity": 50,
    "deliveryDate": "2025-01-28"
  }
]
```

**Response**:
```json
[
  {
    "id": 7,
    "inventory": {
      "id": 1,
      "medicine": {
        "id": 1,
        "name": "ChocoRelief",
        "code": "CRX-001",
        "createdAt": "2025-01-06T21:09:42.689349Z",
        "updatedAt": "2025-01-06T21:09:42.689349Z"
      },
      "stockQuantity": 100,
      "sufficientStock": true
    },
    "quantity": 100,
    "deliveryDate": "2025-01-15",
    "status": "ORDERED",
    "createdAt": "2025-01-06T21:12:27.037639100Z",
    "updatedAt": "2025-01-06T21:12:27.037639100Z"
  },
  {
    "id": 8,
    "inventory": {
      "id": 2,
      "medicine": {
        "id": 2,
        "name": "MintyCure",
        "code": "MCX-002",
        "createdAt": "2025-01-06T21:09:42.762950Z",
        "updatedAt": "2025-01-06T21:09:42.762950Z"
      },
      "stockQuantity": 200,
      "sufficientStock": true
    },
    "quantity": 50,
    "deliveryDate": "2025-01-28",
    "status": "ORDERED",
    "createdAt": "2025-01-06T21:12:27.038672400Z",
    "updatedAt": "2025-01-06T21:12:27.038672400Z"
  }
]
```

---

## Get All Orders
**Endpoint**: `GET /api/orders`  
**Method**: GET

**Response**:
```json
[
  {
    "id": 1,
    "inventory": {
      "id": 1,
      "medicine": {
        "id": 1,
        "name": "ChocoRelief",
        "code": "CRX-001",
        "createdAt": "2025-01-06T21:09:42.689349Z",
        "updatedAt": "2025-01-06T21:09:42.689349Z"
      },
      "stockQuantity": 100,
      "sufficientStock": true
    },
    "quantity": 100,
    "deliveryDate": "2025-01-11",
    "status": "ORDERED",
    "createdAt": "2025-01-06T21:09:42.857667Z",
    "updatedAt": "2025-01-06T21:09:42.857667Z"
  },
  {
    "id": 2,
    "inventory": {
      "id": 2,
      "medicine": {
        "id": 2,
        "name": "MintyCure",
        "code": "MCX-002",
        "createdAt": "2025-01-06T21:09:42.762950Z",
        "updatedAt": "2025-01-06T21:09:42.762950Z"
      },
      "stockQuantity": 200,
      "sufficientStock": true
    },
    "quantity": 200,
    "deliveryDate": "2025-01-16",
    "status": "ORDERED",
    "createdAt": "2025-01-06T21:09:42.860666Z",
    "updatedAt": "2025-01-06T21:09:42.860666Z"
  }
]
```

---

## Get an Order by ID
**Endpoint**: `GET /api/orders/{id}`  
**Method**: GET

**Example**: `GET /api/orders/1`

**Response**:
```json
{
  "id": 1,
  "inventory": {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "ChocoRelief",
      "code": "CRX-001",
      "createdAt": "2025-01-06T21:09:42.689349Z",
      "updatedAt": "2025-01-06T21:09:42.689349Z"
    },
    "stockQuantity": 100,
    "sufficientStock": true
  },
  "quantity": 100,
  "deliveryDate": "2025-01-11",
  "status": "ORDERED",
  "createdAt": "2025-01-06T21:09:42.857667Z",
  "updatedAt": "2025-01-06T21:09:42.857667Z"
}
```

---

## Update an Order
**Endpoint**: `PUT /api/orders/{id}`  
**Method**: PUT  
**Headers**:
- Content-Type: `application/json`

**Example**: `PUT /api/orders/1`

**Request Body**:
```json
{
  "inventoryId": 1,
  "quantity": 150,
  "deliveryDate": "2025-01-14",
  "status": "ORDERED"
}
```

**Response**:
```json
{
  "id": 1,
  "inventory": {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "ChocoRelief",
      "code": "CRX-001",
      "createdAt": "2025-01-06T21:09:42.689349Z",
      "updatedAt": "2025-01-06T21:09:42.689349Z"
    },
    "stockQuantity": 100,
    "sufficientStock": true
  },
  "quantity": 150,  // Updated quantity
  "deliveryDate": "2025-01-14",  // Updated delivery date
  "status": "ORDERED",
  "createdAt": "2025-01-06T21:09:42.857667Z",
  "updatedAt": "2025-01-06T21:09:42.857667Z"
}
```

---

## Update Order Status to "RECEIVED"
**Endpoint**: `PUT /api/orders/received/{id}`  
**Method**: PUT

**Example**: `PUT /api/orders/received/1`

**Response**:
```json
{
  "id": 1,
  "inventory": {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "ChocoRelief",
      "code": "CRX-001",
      "createdAt": "2025-01-06T21:09:42.689349Z",
      "updatedAt": "2025-01-06T21:09:42.689349Z"
    },
    "stockQuantity": 100,
    "sufficientStock": true
  },
  "quantity": 150,
  "deliveryDate": "2025-01-14",
  "status": "RECEIVED",  // Updated Status
  "createdAt": "2025-01-06T21:09:42.857667Z",
  "updatedAt": "2025-01-06T21:18:50.976304600Z"
}
```

---

## Delete an Order by ID
**Endpoint**: `DELETE /api/orders/{id}`  
**Method**: DELETE

**Example**: `DELETE /api/orders/1`

**Response**:
- Status `204 No Content`.
