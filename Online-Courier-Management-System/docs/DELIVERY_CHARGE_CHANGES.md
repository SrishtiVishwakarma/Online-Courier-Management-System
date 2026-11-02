# Delivery Charge Implementation

## Overview
Implemented fixed delivery charge of ₹30 per kg with weight rounding (e.g., 1.2kg is charged as 2kg).

## Changes Made

### 1. Backend Changes

#### Entity Layer (`Courier.java`)
- Added `deliveryCharge` field (BigDecimal) with database column mapping

#### Service Layer (`CourierService.java`)
- Added delivery charge calculation in `createCourier()` method
- Formula: `Math.ceil(weight) * 30`
- Example: 1.2kg → 2kg → ₹60

#### DTO Layer (`CourierDTO.java`)
- Added `deliveryCharge` field to transfer delivery charge to frontend
- Updated `mapToDTO()` method to include delivery charge

#### PDF Service (`PdfService.java`)
- Added **DELIVERY CHARGES** section to receipt
- Displays:
  - Actual Weight (e.g., 1.2 kg)
  - Chargeable Weight (e.g., 2 kg - rounded up)
  - Rate: ₹30 per kg
  - **Total Delivery Charge** (prominently displayed)

### 2. Database Changes

#### Migration Script (`database/add_delivery_charge.sql`)
- Adds `delivery_charge` column to `couriers` table
- Updates existing couriers with calculated charges
- Run this script to migrate existing database

#### Updated Setup Script (`database/complete_setup.sql`)
- Sample courier now includes delivery_charge value
- Verification queries now show delivery charge

## How to Apply Changes

### Step 1: Run Database Migration
```sql
-- Run the migration script
mysql -u root -p ocms < database/add_delivery_charge.sql
```

### Step 2: Restart Spring Boot Application
The application will automatically pick up the entity changes through JPA.

```bash
# Stop the application (Ctrl+C in terminal where it's running)
# Restart it
./mvnw spring-boot:run
```

### Step 3: Test the Feature

#### Create a New Courier
1. Login as a user (e.g., alice@ocms.com)
2. Create a courier with weight 1.2 kg
3. Expected: Delivery charge = ₹60 (2 kg × ₹30)

#### Download Receipt
1. Go to courier details page
2. Click "Download Receipt"
3. Receipt should show:
   - Actual Weight: 1.2 kg
   - Chargeable Weight: 2 kg
   - Rate: ₹30 per kg
   - Total Delivery Charge: ₹60

## Examples

| Actual Weight | Chargeable Weight | Delivery Charge |
|---------------|-------------------|-----------------|
| 0.5 kg        | 1 kg              | ₹30             |
| 1.0 kg        | 1 kg              | ₹30             |
| 1.2 kg        | 2 kg              | ₹60             |
| 2.8 kg        | 3 kg              | ₹90             |
| 5.0 kg        | 5 kg              | ₹150            |

## Technical Details

### Calculation Logic
```java
BigDecimal chargePerKg = new BigDecimal("30");
long roundedWeight = (long) Math.ceil(request.getWeight().doubleValue());
BigDecimal deliveryCharge = chargePerKg.multiply(BigDecimal.valueOf(roundedWeight));
```

### Database Schema
```sql
ALTER TABLE couriers 
ADD COLUMN delivery_charge DECIMAL(10,2) NULL AFTER weight;
```

## Files Modified

1. `online_courier/src/main/java/com/example/online_courier/entity/Courier.java`
2. `online_courier/src/main/java/com/example/online_courier/service/CourierService.java`
3. `online_courier/src/main/java/com/example/online_courier/dto/CourierDTO.java`
4. `online_courier/src/main/java/com/example/online_courier/service/PdfService.java`
5. `database/add_delivery_charge.sql` (new)
6. `database/complete_setup.sql` (updated)

## Backward Compatibility

- Existing couriers without delivery_charge will be updated by the migration script
- New couriers automatically get delivery_charge calculated
- API responses now include `deliveryCharge` field in CourierDTO
