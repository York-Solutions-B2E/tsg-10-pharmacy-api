# TSG-10 Pharmacy API


## Table of Contents
- [Running in Development](#running-in-development)
- [Running in Production](#running-in-production)
- [API Endpoint Reference](#endpoint-reference)
  - [Orders](#orders-api)
  - [Medication Inventory](#medication-inventory-api)
  - [Prescription Management](#prescription-management-api)
- [Entity Relationship Diagram](#entity-relationship-diagram)
- [System Architecture](#system-architecture)


## Running in Development
[Back to Table of Contents](#table-of-contents)

To enable hot-reloading
***Note***: As you make changes and save you will see the API rebuild.
```bash
IntelliJ IDEA:
Enable Auto-Make:
Go to File > Settings > Build, Execution, Deployment > Compiler.
Check "Build project automatically".
```

Start the API
```bash
gradlew bootRun
```

Run tests (terminal)

***Note***: You can view the report generated in ./build/reports/tests/test/index.html
```bash
gradlew test
```
Alternatively just use your IDE to run the tests with coverage.

## Running in Production
[Back to Table of Contents](#table-of-contents)
```
TBD
```


# Endpoint Reference

## Orders API
[Back to Table of Contents](#table-of-contents)

### Create One Order
**POST** - `/api/orders/`

**Status Code:** `201 Created`

**Request:**
```json
{
    "medId": "123456",
    "quantity": 100,
    "deliveryDate": "2024-12-27"
}
```

**Response:**
```json
{
    "id": "123456",
    "medId": "123456",
    "quantity": 100,
    "deliveryDate": "2024-12-27",
    "status": "ORDERED",
    "createdAt": "2024-12-26T10:00:00",
    "updatedAt": "2024-12-26T10:00:00"
}
```

---

### Create Many Orders
**POST** - `/api/orders/batch/`

**Status Code:** `201 Created`

**Request:**
```json
[
    {
        "medId": "123456",
        "quantity": 100,
        "deliveryDate": "2024-12-27"
    },
    {
        "medId": "654321",
        "quantity": 50,
        "deliveryDate": "2024-12-30"
    }
]
```

**Response:**
```json
[
    {
        "id": "123456",
        "medId": "123456",
        "quantity": 100,
        "deliveryDate": "2024-12-27",
        "status": "ORDERED",
        "createdAt": "2024-12-26T10:00:00",
        "updatedAt": "2024-12-26T10:00:00"
    },
    {
        "id": "654321",
        "medId": "654321",
        "quantity": 50,
        "deliveryDate": "2024-12-30",
        "status": "ORDERED",
        "createdAt": "2024-12-26T11:00:00",
        "updatedAt": "2024-12-27T12:00:00"
    }
]
```

---

### Read All Orders
**GET** - `/api/orders/`

**Status Code:** `200 OK`

**Request:**
(No payload)

**Response:**
```json
[
    {
        "id": "123456",
        "medId": "123456",
        "quantity": 100,
        "deliveryDate": "2024-12-27",
        "status": "ORDERED",
        "createdAt": "2024-12-26T10:00:00",
        "updatedAt": "2024-12-26T10:00:00"
    },
    {
        "id": "654321",
        "medId": "654321",
        "quantity": 50,
        "deliveryDate": "2024-12-30",
        "status": "ORDERED",
        "createdAt": "2024-12-26T11:00:00",
        "updatedAt": "2024-12-27T12:00:00"
    }
]
```

---

### Read One Order by ID
**GET** - `/api/orders/<order_id>`

**Status Code:** `200 OK`

**Request:**
(No payload)

**Response:**
```json
{
    "id": "123456",
    "medId": "123456",
    "quantity": 100,
    "deliveryDate": "2024-12-27",
    "status": "ORDERED",
    "createdAt": "2024-12-26T10:00:00",
    "updatedAt": "2024-12-26T10:00:00"
}
```

---

### Update One Order by ID
**PUT** - `/api/orders/<order_id>`

**Status Code:** `200 OK`

**Request:**
```json
{
    "quantity": 150,
    "deliveryDate": "2024-12-30"
}
```

**Response:**
```json
{
    "id": "123456",
    "medId": "123456",
    "quantity": 150,
    "deliveryDate": "2024-12-30",
    "status": "RECEIVED",
    "createdAt": "2024-12-26T10:00:00",
    "updatedAt": "2024-12-27T15:00:00"
}
```

---

### Delete One Order by ID
**DELETE** - `/api/orders/<order_id>`

**Status Code:** `204 No Content`

**Request:**
(No payload)

**Response:**
(No payload)



# Inventory API
[Back to Table of Contents](#table-of-contents)

### Create One Inventory
**POST** - `/api/inventory/`  
**Status Code** `201 Created`

**Request:**
```json
{
    "medicineId": 1,
    "stockQuantity": 100
}
```
medicineId: Long (required, reference to Medicine table)  
stockQuantity: Integer (required, must be >= 0)

**Response:**
```json
{
    "id": 1,
    "medicineId": 1,
    "stockQuantity": 100
}
```

id: Long (database-generated ID)  
medicineId: Long  
stockQuantity: Integer

### Create Many Inventories
**POST** - `/api/inventory/bulk`  
**Status Code** `201 Created`

**Request:**
```json
[{
    "medicineId": 1,
    "stockQuantity": 50
},
{
    "medicineId": 2,
    "stockQuantity": 75
}]
```
Accepts an array of InventoryRequest objects.

**Response:**
```json
[{
    "id": 1,
    "medicineId": 1,
    "stockQuantity": 50
},
{
    "id": 2,
    "medicineId": 2,
    "stockQuantity": 75
}]
```

Returns an array of created InventoryResponse objects, each with their newly assigned id.

### Read All Inventories
**GET** - `/api/inventory/`  
**Status Code:** `200 OK`

**Request:**  
No payload required.

**Response:**
```json
[{
    "id": 1,
    "medicineId": 1,
    "stockQuantity": 100
},
{
    "id": 2,
    "medicineId": 2,
    "stockQuantity": 75
}]
```

Array of all Inventory records, empty array [] if none exist.

### Read One Inventory by ID
**GET** - `/api/inventory/{id}`  
**Status Code:** `200 OK` or `404 Not Found`

**Request:**  
No payload required.

**Path Variable:** `{id}` (Long, required)

**Response:**
```json
{
    "id": 1,
    "medicineId": 1,
    "stockQuantity": 100
}
```

If ID not found, returns 404 with error message.

### Update One Inventory by ID
**PUT** - `/api/inventory/{id}`  
**Status Code:** `200 OK` or `404 Not Found`

**Request:**
```json
{
    "medicineId": 2,
    "stockQuantity": 50
}
```

**Path Variable:** `{id}` (Long, required)  
Body: InventoryRequest with updated fields.

**Response:**
```json
{
    "id": 1,
    "medicineId": 2,
    "stockQuantity": 50
}
```

If ID not found, returns 404 with error message.

### Delete One Inventory by ID
**DELETE** - `/api/inventory/{id}`  
**Status Code:** `204 No Content` or `404 Not Found`

**Request:**  
No payload required.

**Path Variable:** `{id}` (Long, required)

**Response:**  
No payload on success. If ID not found, returns 404 with error message.

# Prescription Management API
[Back to Table of Contents](#table-of-contents)

# Entity Relationship Diagram
[Back to Table of Contents](#table-of-contents)
```mermaid

```


# System Architecture
[Back to Table of Contents](#table-of-contents)
```mermaid

```

