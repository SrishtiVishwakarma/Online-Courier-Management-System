# OCMS API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints except `/auth/login`, `/auth/register`, and `/staff/request` require JWT authentication.

**Header Format:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

## Authentication Endpoints

### POST /auth/login
Login with email and password.

**Request:**
```json
{
  "email": "alice@ocms.com",
  "password": "Admin@123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER",
  "user": {
    "id": 4,
    "name": "Alice User",
    "email": "alice@ocms.com",
    "role": "USER",
    "city": null,
    "status": "ACTIVE"
  }
}
```

**Errors:**
- 401: Invalid credentials
- 400: Missing required fields

---

### POST /auth/register
Register a new USER account.

**Request:**
```json
{
  "email": "newuser@example.com",
  "password": "SecurePass123"
}
```

**Response (200 OK):**
```json
"User registered successfully"
```

---

### POST /staff/request
Submit staff registration request (public endpoint).

**Request:**
```json
{
  "name": "John Staff",
  "email": "john.staff@example.com",
  "password": "StaffPass123",
  "city": "Mumbai"
}
```

**Response (200 OK):**
```json
"Staff request submitted"
```

---

## USER Endpoints

### GET /user/profile
Get current user profile.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 4,
  "name": "Alice User",
  "email": "alice@ocms.com",
  "role": "USER",
  "city": null,
  "status": "ACTIVE"
}
```

---

### PUT /user/profile
Update user profile.

**Request:**
```json
{
  "name": "Alice Updated"
}
```

**Response (200 OK):**
```json
"Profile updated"
```

---

### POST /couriers
Create a new courier shipment.

**Request:**
```json
{
  "senderName": "Alice User",
  "senderAddress": "123 Marine Drive, Mumbai",
  "senderCity": "Mumbai",
  "receiverName": "Bob User",
  "receiverAddress": "55 Connaught Place",
  "receiverCity": "New Delhi",
  "weight": 2.5,
  "notes": "Handle with care"
}
```

**Response (200 OK):**
```json
{
  "courier_id": "OCMS-20251102-00005",
  "courierId": "OCMS-20251102-00005"
}
```

---

### GET /couriers/{courierId}
Get courier details by courier ID.

**Response (200 OK):**
```json
{
  "id": 5,
  "courierId": "OCMS-20251102-00005",
  "senderName": "Alice User",
  "senderCity": "Mumbai",
  "receiverName": "Bob User",
  "receiverCity": "New Delhi",
  "weight": 2.5,
  "status": "CREATED",
  "createdAt": "2025-11-02T18:30:00",
  "logs": [
    {
      "status": "CREATED",
      "note": "Courier created by user",
      "timestamp": "2025-11-02T18:30:00",
      "courierId": "OCMS-20251102-00005"
    }
  ]
}
```

---

### GET /couriers/{courierId}/receipt
Download PDF receipt.

**Response (200 OK):**
- Content-Type: application/pdf
- Content-Disposition: attachment; filename="receipt-OCMS-20251102-00005.pdf"
- Binary PDF data

---

### GET /users/{userId}/couriers
Get all couriers for a specific user.

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "courierId": "OCMS-20251102-00005",
    "senderName": "Alice User",
    "senderCity": "Mumbai",
    "receiverName": "Bob User",
    "receiverCity": "New Delhi",
    "weight": 2.5,
    "status": "CREATED",
    "createdAt": "2025-11-02T18:30:00",
    "logs": [...]
  }
]
```

---

## STAFF Endpoints

### GET /staff/summary
Get staff dashboard summary.

**Response (200 OK):**
```json
{
  "pendingPickups": 3,
  "deliveries": 2
}
```

---

### GET /staff/available-pickups
Get list of couriers available for pickup in staff's city.

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "courierId": "OCMS-20251102-00005",
    "senderCity": "Mumbai",
    "receiverCity": "New Delhi",
    "weight": 2.5,
    "status": "CREATED",
    "logs": [...]
  }
]
```

**Notes:**
- Only returns couriers where `senderCity == staff.city`
- Only returns couriers with status CREATED

---

### POST /staff/couriers/{courierId}/assign
Assign courier to logged-in staff.

**Response (200 OK):**
```json
"Courier assigned"
```

**Errors:**
- 403: Staff city doesn't match courier sender city

---

### POST /staff/couriers/{courierId}/pickup
Mark courier as picked up.

**Response (200 OK):**
```json
"Courier marked as picked up"
```

**Errors:**
- 403: Staff city doesn't match courier sender city
- 400: Courier already picked up

---

### GET /staff/deliveries
Get list of deliveries assigned to staff.

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "courierId": "OCMS-20251102-00005",
    "senderCity": "Mumbai",
    "receiverCity": "New Delhi",
    "weight": 2.5,
    "status": "PICKED_UP",
    "logs": [...]
  }
]
```

**Notes:**
- Only returns couriers assigned to the logged-in staff
- Only returns couriers with status PICKED_UP or IN_TRANSIT

---

### POST /staff/couriers/{courierId}/deliver
Mark courier as delivered.

**Response (200 OK):**
```json
"Courier marked as delivered"
```

**Errors:**
- 403: Courier not assigned to this staff
- 400: Courier not in valid state for delivery

---

### GET /staff/logs
Get activity logs for logged-in staff.

**Response (200 OK):**
```json
[
  {
    "status": "PICKED_UP",
    "note": "Picked up by Mumbai staff",
    "timestamp": "2025-11-02T18:35:00",
    "courierId": "OCMS-20251102-00005"
  },
  {
    "status": "DELIVERED",
    "note": "Delivered successfully",
    "timestamp": "2025-11-02T20:15:00",
    "courierId": "OCMS-20251102-00005"
  }
]
```

---

## ADMIN Endpoints

### GET /admin/staff-requests
Get list of pending staff registration requests.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Rohit Pending",
    "email": "rohit.pending@ocms.com",
    "city": "Bengaluru",
    "status": "PENDING"
  }
]
```

---

### POST /admin/staff-requests/{id}/approve
Approve staff registration request.

**Response (200 OK):**
```json
"Staff request approved"
```

**Side Effects:**
- Creates new USER account with role STAFF
- Sets default password to "Staff@123"
- Updates request status to APPROVED

---

### POST /admin/staff-requests/{id}/reject
Reject staff registration request.

**Response (200 OK):**
```json
"Staff request rejected"
```

**Side Effects:**
- Updates request status to REJECTED
- Does not create user account

---

### GET /admin/staff
Get list of all staff members.

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "name": "Sanjay Staff Mumbai",
    "email": "staff.mumbai@ocms.com",
    "role": "STAFF",
    "city": "Mumbai",
    "status": "ACTIVE"
  },
  {
    "id": 3,
    "name": "Neha Staff Delhi",
    "email": "staff.delhi@ocms.com",
    "role": "STAFF",
    "city": "Delhi",
    "status": "ACTIVE"
  }
]
```

---

### GET /admin/dashboard/status-summary
Get courier status summary (counts by status).

**Response (200 OK):**
```json
{
  "CREATED": 5,
  "PICKED_UP": 3,
  "IN_TRANSIT": 2,
  "DELIVERED": 10,
  "CANCELLED": 1
}
```

---

### GET /admin/couriers/search
Search courier by courier ID.

**Query Parameters:**
- `courier_id` (required): Courier ID to search

**Example:**
```
GET /admin/couriers/search?courier_id=OCMS-20251102-00005
```

**Response (200 OK):**
```json
{
  "id": 5,
  "courierId": "OCMS-20251102-00005",
  "senderName": "Alice User",
  "senderCity": "Mumbai",
  "receiverName": "Bob User",
  "receiverCity": "New Delhi",
  "weight": 2.5,
  "status": "DELIVERED",
  "createdAt": "2025-11-02T18:30:00",
  "logs": [
    {
      "status": "CREATED",
      "note": "Courier created by user",
      "timestamp": "2025-11-02T18:30:00",
      "courierId": "OCMS-20251102-00005"
    },
    {
      "status": "PICKED_UP",
      "note": "Picked up by Mumbai staff",
      "timestamp": "2025-11-02T18:35:00",
      "courierId": "OCMS-20251102-00005"
    },
    {
      "status": "DELIVERED",
      "note": "Delivered successfully",
      "timestamp": "2025-11-02T20:15:00",
      "courierId": "OCMS-20251102-00005"
    }
  ]
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "message": "Invalid input data",
  "error": "Bad Request"
}
```

### 401 Unauthorized
```json
{
  "message": "Invalid credentials",
  "error": "Unauthorized"
}
```

### 403 Forbidden
```json
{
  "message": "Access denied",
  "error": "Forbidden"
}
```

### 404 Not Found
```json
{
  "message": "Courier not found",
  "error": "Not Found"
}
```

### 500 Internal Server Error
```json
{
  "message": "An error occurred",
  "error": "Internal Server Error"
}
```

---

## Courier Status Flow

```
CREATED
   ↓
PICKED_UP (by staff from sender city)
   ↓
IN_TRANSIT (optional)
   ↓
DELIVERED (by assigned staff)
```

---

## City-Based Access Control

**Rule**: Staff can only handle couriers from their assigned city.

**Enforcement:**
- Pickups: `courier.senderCity == staff.city`
- Deliveries: `courier.assignedStaff == staff`

**Example:**
- Staff in Mumbai can pickup couriers with `senderCity = "Mumbai"`
- Staff in Mumbai CANNOT pickup couriers with `senderCity = "Delhi"` (returns 403)

---

## Testing with curl

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@ocms.com","password":"Admin@123"}'
```

### Create Courier
```bash
curl -X POST http://localhost:8080/api/couriers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "senderName":"Alice",
    "senderAddress":"123 Mumbai",
    "senderCity":"Mumbai",
    "receiverName":"Bob",
    "receiverAddress":"456 Delhi",
    "receiverCity":"New Delhi",
    "weight":2.5,
    "notes":"Test"
  }'
```

### Download Receipt
```bash
curl -X GET http://localhost:8080/api/couriers/OCMS-20251102-00001/receipt \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -o receipt.pdf
```

---

## Rate Limiting

Currently no rate limiting implemented. Consider adding in production:
- Login attempts: 5 per 15 minutes per IP
- API calls: 100 per minute per user

---

## Security Notes

- All passwords hashed with BCrypt (strength 10)
- JWT tokens expire after 120 minutes
- CORS enabled for specified frontend origins
- SQL injection prevented by JPA parameterized queries
- XSS protection via Content-Security-Policy headers (add in production)

---

**Last Updated**: November 2, 2025
