Inventory API
=============

### Create One Inventory

**POST** - `/api/inventory/`\
**Status Code** `201 Created`

**Request:**

```json
{
  "medicineId": 1,
  "stockQuantity": 100
}
```

medicineId: Long (required, reference to Medicine table)\
stockQuantity: Integer (required, must be >= 0)\
sufficientStock: Boolean (optional)

**Response:**

```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "ChocoRelief",
    "code": "CRX-001",
    "createdAt": "2025-01-06T21:09:42.689349Z",
    "updatedAt": "2025-01-06T21:09:42.689349Z"
  },
  "stockQuantity": 100,
  "sufficientStock": true,
  "minimumOrderCount": 0,
  "deliveryDate": null
}
```

id: Long (database-generated ID)\
medicineId: Long\
stockQuantity: Integer\
sufficientStock: Boolean
minimumOrderCount: Integer

### Create Many Inventories

**POST** - `/api/inventory/bulk`\
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
[
  {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "ChocoRelief",
      "code": "CRX-001",
      "createdAt": "2025-01-06T21:09:42.689349Z",
      "updatedAt": "2025-01-06T21:09:42.689349Z"
    },
    "stockQuantity": 50,
    "sufficientStock": true,
    "minimumOrderCount": 0,
    "deliveryDate": null
  },
  {
    "id": 2,
    "medicine": {
      "id": 2,
      "name": "MintyCure",
      "code": "MCX-002",
      "createdAt": "2025-01-06T21:09:42.762950Z",
      "updatedAt": "2025-01-06T21:09:42.762950Z"
    },
    "stockQuantity": 75,
    "sufficientStock": true,
    "minimumOrderCount": 0,
    "deliveryDate": null
  }
]
```

Returns an array of created InventoryResponse objects, each with their newly assigned id.

### Read All Inventories

**GET** - `/api/inventory/`\
**Status Code:** `200 OK`

**Request:**\
No payload required.

**Response:**

```json
[
  {
    "id": 1,
    "medicine": {
      "id": 1,
      "name": "ChocoRelief",
      "code": "CRX-001",
      "createdAt": "2025-01-06T21:09:42.689349Z",
      "updatedAt": "2025-01-06T21:09:42.689349Z"
    },
    "stockQuantity": 100,
    "sufficientStock": true,
    "deliveryDate": "2025-01-09"
  },
  {
    "id": 2,
    "medicine": {
      "id": 2,
      "name": "MintyCure",
      "code": "MCX-002",
      "createdAt": "2025-01-06T21:09:42.762950Z",
      "updatedAt": "2025-01-06T21:09:42.762950Z"
    },
    "stockQuantity": 200,
    "sufficientStock": true,
    "deliveryDate": "2025-01-16"
  }
]
```

Array of all Inventory records, empty array [] if none exist.

### Read One Inventory by ID

**GET** - `/api/inventory/{id}`\
**Status Code:** `200 OK` or `404 Not Found`

**Request:**\
No payload required.

**Path Variable:** `{id}` (Long, required)

**Response:**

```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "ChocoRelief",
    "code": "CRX-001",
    "createdAt": "2025-01-06T21:09:42.689349Z",
    "updatedAt": "2025-01-06T21:09:42.689349Z"
  },
  "stockQuantity": 100,
  "sufficientStock": true,
  "deliveryDate": "2025-01-09",
  "minimumOrderCount": "20"
}
```

If ID not found, returns 404 with error message.
If there is no minimum order count, that field will read "0".

### Update One Inventory by ID

**PUT** - `/api/inventory/{id}`\
**Status Code:** `200 OK` or `404 Not Found`

**Request:**

```json
{
  "stockQuantity": 123
}
```

**Path Variable:** `{id}` (Long, required)\
Body: InventoryRequest with updated fields.

**Response:**

```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "ChocoRelief",
    "code": "CRX-001",
    "createdAt": "2025-01-06T21:09:42.689349Z",
    "updatedAt": "2025-01-06T21:09:42.689349Z"
  },
  "stockQuantity": 123,
  "sufficientStock": true,
  "deliveryDate": null
}
```

If ID not found, returns 404 with error message.

### Adjust Stock Quantity

**PUT** - `/api/inventory/{id}/adjust-stock/{pillAdjustment}`\
**Status Code:** `200 OK` or `404 Not Found`

**Request:**\
No payload required.

**Path Variables:**

-   `{id}` (Long, required): ID of the inventory to adjust
-   `{pillAdjustment}` (Integer, required): Number of pills to add or subtract from stock
    -   Positive number: Adds pills to stock (e.g., +100 adds 100 pills)
    -   Negative number: Removes pills from stock (e.g., -30 removes 30 pills)

**Response:**

```json
{
  "id": 1,
  "medicine": {
    "id": 1,
    "name": "ChocoRelief",
    "code": "CRX-001",
    "createdAt": "2025-01-06T21:09:42.689349Z",
    "updatedAt": "2025-01-06T21:09:42.689349Z"
  },
  "stockQuantity": 173,
  "sufficientStock": true,
  "deliveryDate": null
}
```

If ID not found, returns 404 with error message.\
If adjustment would result in negative stock, returns 400 with error message.


### Delete One Inventory by ID

**DELETE** - `/api/inventory/{id}`\
**Status Code:** `204 No Content` or `404 Not Found`

**Request:**\
No payload required.

**Path Variable:** `{id}` (Long, required)

**Response:**\
No payload on success. If ID not found, returns 404 with error message.