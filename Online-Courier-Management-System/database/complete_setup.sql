-- Complete Setup Script for OCMS Testing
USE ocms;

-- 1. Create Hyderabad and Visakhapatnam staff accounts
-- Password for all: 'Admin@123'
INSERT INTO users (name, username, email, password, role, city, status) VALUES
('Ravi Staff Hyderabad', 'staff.hyderabad', 'staff.hyderabad@ocms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6', 'STAFF', 'Hyderabad', 'ACTIVE'),
('Srinivas Staff Visakhapatnam', 'staff.visakhapatnam', 'staff.visakhapatnam@ocms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6', 'STAFF', 'Visakhapatnam', 'ACTIVE')
ON DUPLICATE KEY UPDATE status='ACTIVE';

-- 2. Create test courier from Visakhapatnam to Hyderabad (IN_TRANSIT)
SET @alice := (SELECT id FROM users WHERE email='alice@ocms.com');
SET @vizag_staff := (SELECT id FROM users WHERE email='staff.visakhapatnam@ocms.com');

-- Delete if exists (for re-running script)
DELETE FROM courier_logs WHERE courier_id IN (SELECT id FROM couriers WHERE courier_id = 'OCMS-20251104-00001');
DELETE FROM couriers WHERE courier_id = 'OCMS-20251104-00001';

INSERT INTO couriers (courier_id, tracking_number, user_id, sender_id, sender_name, sender_address, sender_city, sender_mobile,
  pickup_address, pickup_city, pickup_pincode,
  recipient_name, recipient_address, recipient_city, recipient_mobile,
  destination_address, destination_city, destination_pincode, 
  weight, delivery_charge, calculated_price, notes, status, assigned_staff_id)
VALUES
('OCMS-20251104-00001', 'OCMS-20251104-00001', @alice, @alice, 'Srinivas Kumar', 'RTC Complex Road, Visakhapatnam', 'Visakhapatnam', '9876543210',
 'RTC Complex Road, Visakhapatnam', 'Visakhapatnam', '530020',
 'Raam Mohan', 'HITEC City, Hyderabad', 'Hyderabad', '9876543211',
 'HITEC City, Hyderabad', 'Hyderabad', '500081', 
 2.00, 60.00, 60.00, 'Important Documents', 'IN_TRANSIT', NULL);

-- 3. Add courier logs
SET @new_courier := (SELECT id FROM couriers WHERE courier_id='OCMS-20251104-00001');

INSERT INTO courier_logs (courier_id, status, staff_id, note, timestamp) VALUES
(@new_courier, 'CREATED', NULL, 'Courier created by user', NOW() - INTERVAL 2 HOUR),
(@new_courier, 'PICKED_UP', @vizag_staff, 'Picked up by staff', NOW() - INTERVAL 1 HOUR),
(@new_courier, 'IN_TRANSIT', @vizag_staff, 'In transit to destination city', NOW() - INTERVAL 1 HOUR);

-- 4. Verify all staff accounts
SELECT '=== STAFF ACCOUNTS ===' as info;
SELECT id, name, email, role, city, status 
FROM users 
WHERE role = 'STAFF'
ORDER BY city;

-- 5. Verify IN_TRANSIT courier
SELECT '=== IN_TRANSIT COURIER FOR HYDERABAD ===' as info;
SELECT c.courier_id, c.sender_city, c.receiver_city, c.status, c.weight, 
       CONCAT('₹', c.delivery_charge) as charge,
       CONCAT(c.sender_name, ' → ', c.receiver_name) as route
FROM couriers c
WHERE c.receiver_city = 'Hyderabad' AND c.status = 'IN_TRANSIT';

-- 6. Verify logs
SELECT '=== COURIER LOGS ===' as info;
SELECT cl.courier_id, co.courier_id as code, cl.status, u.name as staff_name, cl.note, cl.timestamp
FROM courier_logs cl
LEFT JOIN users u ON cl.staff_id = u.id
LEFT JOIN couriers co ON cl.courier_id = co.id
WHERE co.courier_id = 'OCMS-20251104-00001'
ORDER BY cl.timestamp;

SELECT '=== SETUP COMPLETE ===' as info;
SELECT 'Login as staff.hyderabad@ocms.com with password: Admin@123' as instruction;
