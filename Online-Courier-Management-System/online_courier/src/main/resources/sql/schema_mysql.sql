-- OCMS one-shot setup (MySQL 8). Run with "Execute All" (lightning bolt WITHOUT the cursor).
CREATE DATABASE IF NOT EXISTS ocms;
USE ocms;
DROP TABLE IF EXISTS test_connection;

-- MySQL 8 schema for OCMS

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(100),
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL CHECK (role IN ('USER','STAFF','ADMIN')),
    city VARCHAR(100) NULL,
    status VARCHAR(10) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Staff requests table
CREATE TABLE IF NOT EXISTS staff_requests (
                                              id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                              name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    proof_path VARCHAR(255) NULL,
    status VARCHAR(10) DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Couriers table
CREATE TABLE IF NOT EXISTS couriers (
                                        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                        courier_id VARCHAR(50) NOT NULL UNIQUE,
    tracking_number VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    sender_id BIGINT NULL,
    sender_name VARCHAR(150),
    sender_address TEXT,
    sender_city VARCHAR(100),
    sender_mobile VARCHAR(20),
    pickup_address TEXT,
    pickup_city VARCHAR(100),
    pickup_pincode VARCHAR(10),
    recipient_name VARCHAR(150),
    recipient_address TEXT,
    recipient_city VARCHAR(100),
    recipient_mobile VARCHAR(20),
    destination_address TEXT,
    destination_city VARCHAR(100),
    destination_pincode VARCHAR(10),
    weight DECIMAL(8,2),
    delivery_charge DECIMAL(10,2),
    calculated_price DECIMAL(10,2),
    notes TEXT,
    status VARCHAR(15) DEFAULT 'CREATED' CHECK (status IN ('CREATED','PICKED_UP','IN_TRANSIT','DELIVERED','CANCELLED')),
    assigned_staff_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_couriers_sender_id (sender_id),
    INDEX idx_couriers_status (status),
    INDEX idx_couriers_user_id (user_id),
    CONSTRAINT fk_courier_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_courier_staff FOREIGN KEY (assigned_staff_id) REFERENCES users(id) ON DELETE SET NULL
    );

-- Courier logs table
CREATE TABLE IF NOT EXISTS courier_logs (
                                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            courier_id BIGINT NOT NULL,
                                            status VARCHAR(15) NOT NULL CHECK (status IN ('CREATED','PICKED_UP','IN_TRANSIT','DELIVERED','CANCELLED')),
    staff_id BIGINT NULL,
    note VARCHAR(255) NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_logs_courier_id (courier_id),
    CONSTRAINT fk_log_courier FOREIGN KEY (courier_id) REFERENCES couriers(id) ON DELETE CASCADE,
    CONSTRAINT fk_log_staff FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE SET NULL
    );



-- ---------- Sample data (admin pw: ocsm@123, everyone else: Admin@123) ----------
INSERT IGNORE INTO users (name, username, email, password, role, city, status) VALUES
('Admin User','admin','admin@ocms.com','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi','ADMIN',NULL,'ACTIVE'),
('Sanjay Staff Mumbai','staff.mumbai','staff.mumbai@ocms.com','$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6','STAFF','Mumbai','ACTIVE'),
('Neha Staff Delhi','staff.delhi','staff.delhi@ocms.com','$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6','STAFF','Delhi','ACTIVE'),
('Alice User','alice','alice@ocms.com','$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6','USER',NULL,'ACTIVE'),
('Bob User','bob','bob@ocms.com','$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6','USER',NULL,'ACTIVE');

INSERT INTO staff_requests (name, email, city, status) VALUES ('Rohit Pending','rohit.pending@ocms.com','Bengaluru','PENDING');

SET @alice := (SELECT id FROM users WHERE email='alice@ocms.com');
SET @bob   := (SELECT id FROM users WHERE email='bob@ocms.com');
SET @smum  := (SELECT id FROM users WHERE email='staff.mumbai@ocms.com');

INSERT INTO couriers (courier_id, tracking_number, user_id, sender_id, sender_name, sender_address, sender_city, sender_mobile,
                      pickup_address, pickup_city, pickup_pincode, recipient_name, recipient_address, recipient_city, recipient_mobile,
                      destination_address, destination_city, destination_pincode, weight, delivery_charge, calculated_price, notes, status, assigned_staff_id)
VALUES
    ('OCMS-20251102-00001','OCMS-20251102-00001',@alice,@alice,'Alice User','123 Marine Drive, Mumbai','Mumbai','9876543210',
     '123 Marine Drive, Mumbai','Mumbai','400001','Bob User','55 Connaught Place, New Delhi','New Delhi','9876543211',
     '55 Connaught Place, New Delhi','New Delhi','110001',1.20,50.00,50.00,'Documents','CREATED',NULL),
    ('OCMS-20251102-00002','OCMS-20251102-00002',@bob,@bob,'Bob User','91 CP, New Delhi','New Delhi','9876543212',
     '91 CP, New Delhi','New Delhi','110001','Alice User','123 Marine Drive, Mumbai','Mumbai','9876543210',
     '123 Marine Drive, Mumbai','Mumbai','400001',2.50,80.00,80.00,'Books','PICKED_UP',@smum);

INSERT INTO courier_logs (courier_id, status, staff_id, note)
SELECT id,'CREATED',NULL,'Courier created' FROM couriers;
INSERT INTO courier_logs (courier_id, status, staff_id, note)
SELECT id,'PICKED_UP',@smum,'Picked up by Mumbai staff' FROM couriers WHERE courier_id='OCMS-20251102-00002';

SHOW TABLES;
SELECT id, username, role FROM users;