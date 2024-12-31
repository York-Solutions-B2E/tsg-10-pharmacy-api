
# Inventory API

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


