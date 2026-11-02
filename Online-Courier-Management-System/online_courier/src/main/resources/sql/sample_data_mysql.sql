-- Sample data for OCMS - MySQL 8

-- Users: admin@ocms.com with password ocsm@123, two staff, two users
-- Admin password: 'ocsm@123' BCrypt hash: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Others password: 'Admin@123' BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6
INSERT IGNORE INTO users (name, username, email, password, role, city, status) VALUES
('Admin User', 'admin', 'admin@ocms.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', NULL, 'ACTIVE'),
('Sanjay Staff Mumbai', 'staff.mumbai', 'staff.mumbai@ocms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6', 'STAFF', 'Mumbai', 'ACTIVE'),
('Neha Staff Delhi', 'staff.delhi', 'staff.delhi@ocms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6', 'STAFF', 'Delhi', 'ACTIVE'),
('Alice User', 'alice', 'alice@ocms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6', 'USER', NULL, 'ACTIVE'),
('Bob User', 'bob', 'bob@ocms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6', 'USER', NULL, 'ACTIVE')
;

-- Staff requests (pending)
INSERT IGNORE INTO staff_requests (name, email, city, proof_path, status) VALUES
('Rohit Pending', 'rohit.pending@ocms.com', 'Bengaluru', NULL, 'PENDING')
;

-- Sample couriers
INSERT INTO couriers (courier_id, tracking_number, user_id, sender_id, sender_name, sender_address, sender_city, sender_mobile,
  pickup_address, pickup_city, pickup_pincode,
  recipient_name, recipient_address, recipient_city, recipient_mobile,
  destination_address, destination_city, destination_pincode, 
  weight, delivery_charge, calculated_price, notes, status, assigned_staff_id)
SELECT
    'OCMS-20251102-00001', 'OCMS-20251102-00001', alice.id, alice.id, 'Alice User', '123 Marine Drive, Mumbai', 'Mumbai', '9876543210',
    '123 Marine Drive, Mumbai', 'Mumbai', '400001',
    'Bob User', '55 Connaught Place, New Delhi', 'New Delhi', '9876543211',
    '55 Connaught Place, New Delhi', 'New Delhi', '110001',
    1.20, 50.00, 50.00, 'Documents', 'CREATED', NULL
FROM alice
WHERE NOT EXISTS (SELECT 1 FROM couriers WHERE courier_id = 'OCMS-20251102-00001');

INSERT INTO couriers (courier_id, tracking_number, user_id, sender_id, sender_name, sender_address, sender_city, sender_mobile,
  pickup_address, pickup_city, pickup_pincode,
  recipient_name, recipient_address, recipient_city, recipient_mobile,
  destination_address, destination_city, destination_pincode, 
  weight, delivery_charge, calculated_price, notes, status, assigned_staff_id)
SELECT
    'OCMS-20251102-00002', 'OCMS-20251102-00002', bob.id, bob.id, 'Bob User', '91 CP, New Delhi', 'New Delhi', '9876543212',
    '91 CP, New Delhi', 'New Delhi', '110001',
    'Alice User', '123 Marine Drive, Mumbai', 'Mumbai', '9876543210',
    '123 Marine Drive, Mumbai', 'Mumbai', '400001',
    2.50, 80.00, 80.00, 'Books', 'PICKED_UP', staff_mumbai.id
FROM bob, staff_mumbai
WHERE NOT EXISTS (SELECT 1 FROM couriers WHERE courier_id = 'OCMS-20251102-00002');

-- Courier logs
INSERT INTO courier_logs (courier_id, status, staff_id, note)
SELECT c.id, 'CREATED', NULL, 'Courier created by user'
FROM couriers c WHERE c.courier_id = 'OCMS-20251102-00001'
AND NOT EXISTS (SELECT 1 FROM courier_logs cl WHERE cl.courier_id = c.id AND cl.status = 'CREATED');

INSERT INTO courier_logs (courier_id, status, staff_id, note)
SELECT c.id, 'CREATED', NULL, 'Courier created'
FROM couriers c WHERE c.courier_id = 'OCMS-20251102-00002'
AND NOT EXISTS (SELECT 1 FROM courier_logs cl WHERE cl.courier_id = c.id AND cl.status = 'CREATED');

INSERT INTO courier_logs (courier_id, status, staff_id, note)
SELECT c.id, 'PICKED_UP', u.id, 'Picked up by Mumbai staff'
FROM couriers c
JOIN users u ON u.email = 'staff.mumbai@ocms.com'
WHERE c.courier_id = 'OCMS-20251102-00002'
AND NOT EXISTS (SELECT 1 FROM courier_logs cl WHERE cl.courier_id = c.id AND cl.status = 'PICKED_UP');
