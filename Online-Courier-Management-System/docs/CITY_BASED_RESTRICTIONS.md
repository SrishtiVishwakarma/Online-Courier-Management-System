# City-Based Courier Delivery Restrictions

## Overview

This document explains the city-based restrictions implemented in the Online Courier Management System (OCMS). These restrictions ensure that staff can only handle couriers relevant to their assigned city.

## Rules

### 1. Pickup Restrictions
**Rule:** Staff can ONLY pick up couriers where the **sender's city** matches the **staff's city**.

**Example:**
- A courier sent from Pune → Hyderabad
- Only staff from **Pune** can pick it up
- Staff from Hyderabad, Mumbai, or other cities **cannot** pick it up

**Implementation:**
- Backend: `CourierService.getAvailablePickupsForCity(String city)` filters by `sender_city`
- Validation: `CourierService.markPickedUp()` validates staff city matches sender city
- Frontend: `staff-pickups.html` displays only pickups from staff's city

### 2. Delivery Restrictions
**Rule:** Staff can ONLY deliver couriers where the **receiver's city** matches the **staff's city**.

**Example:**
- A courier sent from Pune → Hyderabad
- Only staff from **Hyderabad** can deliver it
- Staff from Pune, Mumbai, or other cities **cannot** deliver it

**Implementation:**
- Backend: `CourierService.getStaffDeliveries(String staffEmail)` filters by `receiver_city`
- Validation: `CourierService.markDelivered()` validates staff city matches receiver city
- Frontend: `staff-deliveries.html` displays only deliveries to staff's city

## Workflow Example

### Scenario: Courier from Visakhapatnam → Hyderabad

1. **CREATED Status**
   - Courier is created by user in Visakhapatnam
   - Status: `CREATED`
   - Visible in: Visakhapatnam staff's "Available Pickups"
   - **NOT** visible in: Hyderabad staff's pickups

2. **PICKED_UP Status**
   - Visakhapatnam staff picks up the courier
   - Status changes: `CREATED` → `PICKED_UP` → `IN_TRANSIT`
   - `assigned_staff` is cleared (set to `null`)
   - Now visible in: Hyderabad staff's "Deliveries"

3. **IN_TRANSIT Status**
   - Courier is in transit to Hyderabad
   - Status: `IN_TRANSIT`
   - Visible in: Hyderabad staff's "Deliveries"
   - **NOT** visible in: Visakhapatnam or other cities' deliveries

4. **DELIVERED Status**
   - Hyderabad staff delivers the courier
   - Status: `DELIVERED`
   - `assigned_staff` is set to the delivering staff
   - Removed from all active pickups/deliveries lists

## Database Schema

### Staff (users table)
```sql
- id: BIGINT (PK)
- name: VARCHAR
- email: VARCHAR (UNIQUE)
- city: VARCHAR  -- Staff's assigned city
- role: ENUM('USER', 'STAFF', 'ADMIN')
```

### Couriers (couriers table)
```sql
- id: BIGINT (PK)
- courier_id: VARCHAR (UNIQUE)
- sender_city: VARCHAR     -- Source city for pickup
- receiver_city: VARCHAR   -- Destination city for delivery
- status: ENUM('CREATED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED')
- assigned_staff_id: BIGINT (FK to users.id)
```

## Backend API Endpoints

### GET /api/staff/available-pickups
**Returns:** Couriers with `sender_city = staff.city` and `status = CREATED`

**Example Response:**
```json
[
  {
    "courierId": "OCMS-20251104-00001",
    "senderCity": "Visakhapatnam",
    "receiverCity": "Hyderabad",
    "status": "CREATED",
    "weight": 2.0
  }
]
```

### GET /api/staff/deliveries
**Returns:** Couriers with `receiver_city = staff.city` and `status = IN_TRANSIT`

**Example Response:**
```json
[
  {
    "courierId": "OCMS-20251104-00001",
    "senderCity": "Visakhapatnam",
    "receiverCity": "Hyderabad",
    "status": "IN_TRANSIT",
    "weight": 2.0
  }
]
```

### POST /api/staff/couriers/{courierId}/pickup
**Validation:** Throws error if `staff.city != courier.sender_city`

**Success:** Changes status to `IN_TRANSIT`, clears `assigned_staff`

### POST /api/staff/couriers/{courierId}/deliver
**Validation:** Throws error if `staff.city != courier.receiver_city`

**Success:** Changes status to `DELIVERED`, sets `assigned_staff` to delivering staff

## Frontend Implementation

### Staff Pickups Page (staff-pickups.html)
- Displays staff's city at the top
- Shows only couriers from sender's city matching staff city
- Clear messaging: "You can only pick up couriers from senders in {city}"

### Staff Deliveries Page (staff-deliveries.html)
- Displays staff's city at the top
- Shows only couriers to receiver's city matching staff city
- Clear messaging: "You can only deliver couriers to receivers in {city}"

## Verification

Run the verification script to see city-based filtering in action:
```bash
mysql -u root -p ocms < verify_city_restrictions.sql
```

## Testing the Restrictions

### Test Staff Accounts
- **Visakhapatnam:** staff.visakhapatnam@ocms.com (Password: Admin@123)
- **Hyderabad:** staff.hyderabad@ocms.com (Password: Admin@123)
- **Mumbai:** staff.mumbai@ocms.com (Password: Admin@123)

### Test Scenario
1. Login as Visakhapatnam staff
2. Check "Available Pickups" - should see couriers from Visakhapatnam only
3. Pick up a courier going to Hyderabad
4. Logout and login as Hyderabad staff
5. Check "Your Deliveries" - should see the courier picked up in step 3
6. Deliver the courier

## Benefits

1. **Clear Responsibility:** Each staff member knows exactly which couriers they're responsible for
2. **Reduced Errors:** Staff cannot accidentally pick up or deliver wrong couriers
3. **Efficient Operations:** Staff only see relevant couriers for their location
4. **Better Tracking:** Easy to identify which city a courier is currently in based on its status

## Code References

### Backend Files
- `CourierService.java` (lines 76-80, 143-153)
- `StaffController.java` (lines 60-66, 80-83)
- `CourierRepository.java` (lines 15-16)

### Frontend Files
- `staff-pickups.html` (lines 30-68)
- `staff-deliveries.html` (lines 30-68)
