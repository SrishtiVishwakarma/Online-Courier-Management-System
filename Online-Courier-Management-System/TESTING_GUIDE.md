# Testing Guide - Mobile Fields Fix

## Prerequisites
- Database migration has been run successfully
- Backend application is rebuilt and running
- Frontend is accessible

## Test Plan

### 1. Database Verification

**Check if columns exist:**
```sql
USE ocms;
DESCRIBE couriers;
```

**Expected output should include:**
- `sender_mobile` VARCHAR(20)
- `recipient_mobile` VARCHAR(20)

### 2. Backend Verification

**Restart the application:**
```bash
cd online_courier
mvn clean package
java -jar target/online_courier-0.0.1-SNAPSHOT.jar
```

**Check for errors:**
- No field mapping errors in startup logs
- Application starts successfully on port 8080

### 3. Frontend Form Testing

**Test Case 1: Create New Courier**

1. Navigate to `frontend/send-courier.html`
2. Fill in all fields:
   - Sender Name: Test User
   - Sender City: Select any city
   - **Sender Mobile: 9876543210** ✓ NEW
   - Sender Address: Test Address
   - Receiver Name: Test Receiver
   - Receiver City: Select any city
   - **Receiver Mobile: 9876543211** ✓ NEW
   - Receiver Address: Test Receiver Address
   - Weight: 2.5 kg
   - Notes: Test courier

3. Click "Create Courier"

**Expected Result:**
- ✓ Form submits successfully
- ✓ Success message displays
- ✓ Courier ID is generated
- ✓ No error about `recipient_mobile` field

**Common Issues:**
- If you get validation error: Ensure mobile numbers are exactly 10 digits
- If you get field error: Database migration may not have run

### 4. View Courier Details Testing

**Test Case 2: View Courier Status**

1. Navigate to `frontend/courier-status.html`
2. Enter the courier ID from Test Case 1
3. View courier details

**Expected Result:**
- ✓ Sender mobile displays: "Sender Mobile: 9876543210"
- ✓ Receiver mobile displays: "Receiver Mobile: 9876543211"
- ✓ All other courier details display correctly

### 5. Admin View Testing

**Test Case 3: Admin Track Courier**

1. Login as admin
2. Navigate to `frontend/admin-track.html`
3. Enter courier ID

**Expected Result:**
- ✓ Both mobile numbers display
- ✓ Complete courier information visible

### 6. Staff Operations Testing

**Test Case 4: Staff Pickup**

1. Login as staff user
2. Navigate to `frontend/staff-pickups.html`
3. View available pickups

**Expected Result:**
- ✓ Sender mobile displays as "Contact: 9876543210"
- ✓ Staff can see contact info for coordination

**Test Case 5: Staff Delivery**

1. Pickup a courier (from Test Case 4)
2. Navigate to `frontend/staff-deliveries.html`
3. View deliveries

**Expected Result:**
- ✓ Recipient mobile displays as "Contact: 9876543211"
- ✓ Staff can contact receiver for delivery

### 7. PDF Receipt Testing

**Test Case 6: Download Receipt**

1. View any courier details
2. Click "Download Receipt (PDF)"
3. Open the downloaded PDF

**Expected Result:**
- ✓ PDF includes "Mobile: 9876543210" in Sender Details section
- ✓ PDF includes "Mobile: 9876543211" in Receiver Details section
- ✓ All other details are correct

### 8. API Testing (Optional)

**Test with cURL:**

```bash
# Create courier with mobile numbers
curl -X POST http://localhost:8080/api/couriers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "senderName": "API Test Sender",
    "senderCity": "Hyderabad",
    "senderMobile": "9999888877",
    "senderAddress": "API Test Address",
    "receiverName": "API Test Receiver",
    "receiverCity": "Visakhapatnam",
    "recipientMobile": "9999888866",
    "receiverAddress": "API Test Receiver Address",
    "weight": 3.5,
    "notes": "API Test"
  }'
```

**Expected Response:**
```json
{
  "courier_id": "OCMS-YYYYMMDD-XXXXX",
  "message": "Courier created successfully"
}
```

### 9. Edge Cases Testing

**Test Case 7: Empty Mobile Numbers (Old Data)**

1. Query database for old couriers without mobile numbers
2. View these couriers in frontend

**Expected Result:**
- ✓ Old couriers display without errors
- ✓ Mobile fields are simply not shown (conditional rendering)
- ✓ No null pointer exceptions

**Test Case 8: Invalid Mobile Numbers**

1. Try entering 9-digit number: "987654321"
2. Try entering 11-digit number: "98765432101"

**Expected Result:**
- ✓ HTML5 validation prevents submission
- ✓ Error message: "Please match the requested format"

## Verification Checklist

- [ ] Database columns added successfully
- [ ] Backend starts without errors
- [ ] Can create new courier with mobile numbers
- [ ] Mobile numbers appear in user courier status view
- [ ] Mobile numbers appear in admin track view
- [ ] Mobile numbers appear in staff pickup list
- [ ] Mobile numbers appear in staff delivery list
- [ ] Mobile numbers appear in PDF receipt
- [ ] Old couriers (without mobiles) still display correctly
- [ ] Form validation works for mobile fields

## Troubleshooting

### Error: "Field 'recipient_mobile' doesn't have a default value"
**Solution:** Run the database migration script again:
```bash
mysql -u root -p ocms < database/add_mobile_fields.sql
```

### Error: "Cannot resolve field recipientMobile"
**Solution:** Rebuild the backend:
```bash
cd online_courier
mvn clean install
```

### Mobile fields not showing in frontend
**Solution:** Clear browser cache and hard reload (Ctrl+Shift+R)

### PDF not showing mobile numbers
**Solution:** Verify PdfService.java was updated correctly

## Success Criteria

✅ **All Tests Pass** - The fix is complete and working correctly

✅ **No Database Errors** - Courier creation works without SQL errors

✅ **Enhanced User Experience** - Staff can now contact senders/receivers easily

✅ **Backward Compatible** - Old couriers without mobiles still work
