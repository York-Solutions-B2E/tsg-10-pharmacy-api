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
    "id": 123456,
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
        "id": 123456,
        "medId": "123456",
        "quantity": 100,
        "deliveryDate": "2024-12-27",
        "status": "ORDERED",
        "createdAt": "2024-12-26T10:00:00",
        "updatedAt": "2024-12-26T10:00:00"
    },
    {
        "id": 654321,
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
        "id": 123456,
        "medId": "123456",
        "quantity": 100,
        "deliveryDate": "2024-12-27",
        "status": "ORDERED",
        "createdAt": "2024-12-26T10:00:00",
        "updatedAt": "2024-12-26T10:00:00"
    },
    {
        "id": 654321,
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
    "id": 123456,
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
    "id": 123456,
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

