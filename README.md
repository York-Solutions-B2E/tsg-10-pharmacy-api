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



# Medication Inventory API
[Back to Table of Contents](#table-of-contents)

### Create One MedInventory
**POST** - `/api/med-inventory/`
**Status Code** `201 Created`

**Request:**
```json
{
"medName": "Aspirin",
"stockCount": 100,
"deliveryDate": "2025-01-10"
}
```
medName: String (required, name of the medicine)  
stockCount: Integer (required, must be >= 0)  
deliveryDate: String (optional, format: YYYY-MM-DD)  

**Response:**
```json
{
"id": 1,
"medName": "Aspirin",
"stockCount": 100,
"deliveryDate": "2025-01-10"
}
```

id: Long (database-generated ID)  
medName: String  
stockCount: Integer  
deliveryDate: String (nullable if not set)  

### Create Many MedInventories
**POST** - `/api/med-inventory/bulk`
**Status Code** `201 Created`

**Request:**

```json
[{
"medName": "MedA",
"stockCount": 50,
"deliveryDate": "2024-12-31"
},
{
"medName": "MedB",
"stockCount": 75
}]
```
Accepts an array of MedInventoryRequest objects.

**Response:**

```json
[{
"id": 1,
"medName": "MedA",
"stockCount": 50,
"deliveryDate": "2024-12-31"
},
{
"id": 2,
"medName": "MedB",
"stockCount": 75,
"deliveryDate": null
}]
```

Returns an array of created MedInventoryResponse objects, each with their newly assigned id.

### Read All MedInventories
**GET** - `/api/med-inventory/`
**Status Code:** `200 OK`

**Request:**
No payload (query parameters optional, if any).

**Response Body Example:**

```json
[{
"id": 1,
"medName": "Aspirin",
"stockCount": 100,
"deliveryDate": "2025-01-10"
},
{
"id": 2,
"medName": "MedB",
"stockCount": 75,
"deliveryDate": null
}]
```

Array of all MedInventory records currently stored, empty array [] if none exist.

### Read One MedInventory by ID
**GET** - `/api/med-inventory/{id}`
**Status Code:** `200 OK`

**Request:**
No payload.

**Path Variable:** `{id}` (Long, required)

**Response Body:**

```json
{
"id": 1,
"medName": "Aspirin",
"stockCount": 100,
"deliveryDate": "2025-01-10"
}
```

TODO: Decide how API should respond if the specified {id} does not exist.

### Update One MedInventory by ID
**PUT** - `/api/med-inventory/{id}`
**Status Code:** `200 OK`

**Request:**
```json
{
"medName": "Tylenol",
"stockCount": 50,
"deliveryDate": "2025-02-15"
}
```

**Path Variable:** `{id}` (Long, required)
Body: MedInventoryRequest with updated fields.

**Response:**

```json
{
"id": 1,
"medName": "Tylenol",
"stockCount": 50,
"deliveryDate": "2025-02-15"
}
```

TODO: Decide how API should respond if the specified {id} does not exist.

### Delete One MedInventory by ID
**DELETE** - `/api/med-inventory/{id}`
**Status Code:** `204 No Content`

**Request:**
No payload.

**Path Variable:** `{id}` (Long, required)

**Response:**
No payload (empty response body).

TODO: Decide how API should respond if the specified {id} does not exist.

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

