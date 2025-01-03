
# Prescription API Reference

## 1. Create a Prescription
**Endpoint**: `POST /api/prescriptions`
**Method**: POST
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
{
    "patientID": 716253,
    "medicineCode": "MED001",
    "prescriptionNumber": 836435,
    "quantity": 30,
    "instructions": "Take after meals"
}
```

**Response**:
```json
{
  "id": 123,
  "patientId": 16283,
  "medicine": {
    "id": 1,
    "name": "BerryBoost",
    "code": "BBX-014",
    "createdAt": "2025-01-02T22:03:19.284986Z",
    "updatedAt": "2025-01-02T22:03:19.284986Z"
  },
  "prescriptionNumber": 15243,
  "quantity": 30,
  "instructions": "Take after meals",
  "status": "NEW"
}
```

---

## 2. Get All Prescriptions
**Endpoint**: `GET /api/prescriptions`  
**Method**: GET

**Response**:
```json
[
  {
    "id": 1,
    "patientId": 16283,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "prescriptionNumber": 15243,
    "quantity": 30,
    "instructions": "Take after meals",
    "status": "NEW"
  },
  {
    "id": 2,
    "patientId": 16283,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "prescriptionNumber": 15243,
    "quantity": 30,
    "instructions": "Take after meals",
    "status": "NEW"
  }
]
```

---

## 3. Get Active Prescriptions
**Endpoint**: `GET /api/prescriptions/active`  
**Method**: GET

**Response**:
```json
[
  {
    "id": 1,
    "patientId": 16283,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "prescriptionNumber": 15243,
    "quantity": 30,
    "instructions": "Take after meals",
    "status": "NEW"
  },
  {
    "id": 2,
    "patientId": 16283,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "prescriptionNumber": 15243,
    "quantity": 30,
    "instructions": "Take after meals",
    "status": "NEW"
  }
]
```

---

## 4. Get a Prescription by ID
**Endpoint**: `GET /api/prescriptions/{id}`  
**Method**: GET

**Example**: `GET /api/prescriptions/1`

**Response**:
```json
{
    "id": 1,
    "patientId": 16283,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "prescriptionNumber": 15243,
    "quantity": 30,
    "instructions": "Take after meals",
    "status": "NEW"
  }
```

---

## 5. Update Prescription Status
**Endpoint**: `PUT /api/prescriptions/{id}`
**Method**: PUT
**Headers**:
- Content-Type: `application/json`

**Request Body**:
```json
{
  "status": "FILLED"
}
```

**Response**:
```json
{
    "id": 1,
    "patientId": 16283,
    "medicine": {
      "id": 1,
      "name": "BerryBoost",
      "code": "BBX-014",
      "createdAt": "2025-01-02T22:03:19.284986Z",
      "updatedAt": "2025-01-02T22:03:19.284986Z"
    },
    "prescriptionNumber": 15243,
    "quantity": 30,
    "instructions": "Take after meals",
    "status": "FILLED"
  }
```

