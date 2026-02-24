-- Create Database
CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;

-- ============================================
-- 1. TABLE CREATION
-- ============================================

-- 1.1 ADMIN TABLE
CREATE TABLE admin (
admin_id INT PRIMARY KEY AUTO_INCREMENT,
admin_uuid VARCHAR(36) UNIQUE NOT NULL,
username VARCHAR(50) UNIQUE NOT NULL,
password_hash VARCHAR(255) NOT NULL,
full_name VARCHAR(100) NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
role ENUM('super_admin', 'fleet_manager', 'customer_service') DEFAULT 'fleet_manager',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
last_login TIMESTAMP NULL,
is_active BOOLEAN DEFAULT TRUE
);

-- 1.2 CUSTOMER TABLE
CREATE TABLE customer (
customer_id INT PRIMARY KEY AUTO_INCREMENT,
customer_uuid VARCHAR(36) UNIQUE NOT NULL,
first_name VARCHAR(50) NOT NULL,
last_name VARCHAR(50) NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
phone VARCHAR(20) NOT NULL,
address TEXT,
license_number VARCHAR(50) UNIQUE NOT NULL,
license_expiry DATE NOT NULL,
date_of_birth DATE NOT NULL,
registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
is_active BOOLEAN DEFAULT TRUE,
INDEX idx_email (email),
INDEX idx_license (license_number)
);

-- 1.3 CAR TABLE (with availability tracking)
CREATE TABLE car (
car_id INT PRIMARY KEY AUTO_INCREMENT,
car_uuid VARCHAR(36) UNIQUE NOT NULL,
make VARCHAR(50) NOT NULL,
model VARCHAR(50) NOT NULL,
year INT NOT NULL,
license_plate VARCHAR(20) UNIQUE NOT NULL,
vin VARCHAR(17) UNIQUE NOT NULL,
color VARCHAR(30),
daily_rate DECIMAL(10,2) NOT NULL,
weekly_rate DECIMAL(10,2),
monthly_rate DECIMAL(10,2),
mileage INT DEFAULT 0,
fuel_type ENUM('petrol', 'diesel', 'electric', 'hybrid') DEFAULT 'petrol',
transmission ENUM('manual', 'automatic') DEFAULT 'automatic',
seats INT DEFAULT 5,
status ENUM('available', 'rented', 'maintenance', 'reserved') DEFAULT 'available',
is_available BOOLEAN GENERATED ALWAYS AS (status = 'available') STORED,
location VARCHAR(100),
last_maintenance DATE,
next_maintenance DATE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
INDEX idx_make_model (make, model),
INDEX idx_status (status),
INDEX idx_availability (is_available)
);

-- 1.4 RENTAL_REQUEST TABLE
CREATE TABLE rental_request (
request_id INT PRIMARY KEY AUTO_INCREMENT,
request_uuid VARCHAR(36) UNIQUE NOT NULL,
customer_id INT NOT NULL,
car_id INT NOT NULL,
request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
start_date DATE NOT NULL,
end_date DATE NOT NULL,
pickup_location VARCHAR(100),
dropoff_location VARCHAR(100),
status ENUM('pending', 'approved', 'rejected', 'cancelled', 'expired') DEFAULT 'pending',
total_days INT GENERATED ALWAYS AS (DATEDIFF(end_date, start_date)) STORED,
daily_rate_at_request DECIMAL(10,2) NOT NULL,
estimated_total DECIMAL(10,2) GENERATED ALWAYS AS (daily_rate_at_request * DATEDIFF(end_date, start_date)) STORED,
special_requests TEXT,
admin_notes TEXT,
reviewed_by INT,
reviewed_at TIMESTAMP NULL,
FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
FOREIGN KEY (car_id) REFERENCES car(car_id) ON DELETE RESTRICT,
FOREIGN KEY (reviewed_by) REFERENCES admin(admin_id) ON DELETE SET NULL,
INDEX idx_status (status),
INDEX idx_dates (start_date, end_date),
INDEX idx_customer (customer_id),
INDEX idx_car (car_id)
);

-- 1.5 RENTAL_CONTRACT TABLE (approved requests become contracts)
CREATE TABLE rental_contract (
contract_id INT PRIMARY KEY AUTO_INCREMENT,
contract_uuid VARCHAR(36) UNIQUE NOT NULL,
request_id INT UNIQUE NOT NULL,
customer_id INT NOT NULL,
car_id INT NOT NULL,
admin_id INT NOT NULL,
contract_number VARCHAR(50) UNIQUE NOT NULL,
start_date DATE NOT NULL,
end_date DATE NOT NULL,
actual_pickup DATETIME NULL,
actual_return DATETIME NULL,
daily_rate DECIMAL(10,2) NOT NULL,
total_amount DECIMAL(10,2) NOT NULL,
discount_amount DECIMAL(10,2) DEFAULT 0.00,
tax_amount DECIMAL(10,2) DEFAULT 0.00,
final_amount DECIMAL(10,2) GENERATED ALWAYS AS (total_amount - discount_amount + tax_amount) STORED,
payment_status ENUM('pending', 'partial', 'paid', 'refunded') DEFAULT 'pending',
contract_status ENUM('active', 'completed', 'cancelled', 'no_show') DEFAULT 'active',
odometer_start INT,
odometer_end INT,
notes TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
signed_at TIMESTAMP NULL,
FOREIGN KEY (request_id) REFERENCES rental_request(request_id) ON DELETE RESTRICT,
FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
FOREIGN KEY (car_id) REFERENCES car(car_id) ON DELETE RESTRICT,
FOREIGN KEY (admin_id) REFERENCES admin(admin_id) ON DELETE RESTRICT,
INDEX idx_contract_number (contract_number),
INDEX idx_dates (start_date, end_date),
INDEX idx_status (contract_status),
INDEX idx_payment (payment_status)
);

-- 1.6 PAYMENT TABLE
CREATE TABLE payment (
payment_id INT PRIMARY KEY AUTO_INCREMENT,
payment_uuid VARCHAR(36) UNIQUE NOT NULL,
contract_id INT NOT NULL,
customer_id INT NOT NULL,
payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
amount DECIMAL(10,2) NOT NULL,
payment_method ENUM('cash', 'credit_card', 'debit_card', 'bank_transfer', 'online') NOT NULL,
transaction_id VARCHAR(100),
card_last_four VARCHAR(4),
payment_status ENUM('pending', 'completed', 'failed', 'refunded') DEFAULT 'completed',
receipt_number VARCHAR(50) UNIQUE,
notes TEXT,
processed_by INT,
FOREIGN KEY (contract_id) REFERENCES rental_contract(contract_id) ON DELETE RESTRICT,
FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
FOREIGN KEY (processed_by) REFERENCES admin(admin_id) ON DELETE SET NULL,
INDEX idx_contract (contract_id),
INDEX idx_transaction (transaction_id)
);

-- 1.7 MAINTENANCE TABLE
CREATE TABLE maintenance (
maintenance_id INT PRIMARY KEY AUTO_INCREMENT,
car_id INT NOT NULL,
maintenance_date DATE NOT NULL,
maintenance_type ENUM('routine', 'repair', 'inspection', 'accident') NOT NULL,
description TEXT NOT NULL,
cost DECIMAL(10,2),
odometer_reading INT,
performed_by VARCHAR(100),
next_maintenance_date DATE,
status ENUM('scheduled', 'in_progress', 'completed', 'cancelled') DEFAULT 'scheduled',
notes TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
completed_at TIMESTAMP NULL,
FOREIGN KEY (car_id) REFERENCES car(car_id) ON DELETE RESTRICT,
INDEX idx_car_date (car_id, maintenance_date)
);

-- 1.8 AUDIT_LOG TABLE
CREATE TABLE audit_log (
log_id INT PRIMARY KEY AUTO_INCREMENT,
log_uuid VARCHAR(36) UNIQUE NOT NULL,
table_name VARCHAR(50) NOT NULL,
record_id INT NOT NULL,
action ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
old_data JSON,
new_data JSON,
changed_by INT,
changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ip_address VARCHAR(45),
FOREIGN KEY (changed_by) REFERENCES admin(admin_id) ON DELETE SET NULL,
INDEX idx_record (table_name, record_id),
INDEX idx_time (changed_at)
);

-- ============================================
-- 2. AVAILABILITY VALIDATION FUNCTIONS
-- ============================================

DELIMITER //

-- 2.1 Function to check if car is available for specific dates
CREATE FUNCTION IsCarAvailable(
p_car_id INT,
p_start_date DATE,
p_end_date DATE
) RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
DECLARE v_is_available BOOLEAN DEFAULT FALSE;
DECLARE v_car_status VARCHAR(20);
DECLARE v_conflicts INT;

-- Check car status
SELECT status INTO v_car_status
FROM car
WHERE car_id = p_car_id;

IF v_car_status = 'available' THEN
-- Check for conflicting requests
SELECT COUNT(*) INTO v_conflicts
FROM (
SELECT request_id FROM rental_request
WHERE car_id = p_car_id
AND status IN ('pending', 'approved')
AND (
(start_date BETWEEN p_start_date AND p_end_date)
OR (end_date BETWEEN p_start_date AND p_end_date)
OR (p_start_date BETWEEN start_date AND end_date)
)
UNION
SELECT contract_id FROM rental_contract
WHERE car_id = p_car_id
AND contract_status = 'active'
AND (
(start_date BETWEEN p_start_date AND p_end_date)
OR (end_date BETWEEN p_start_date AND p_end_date)
OR (p_start_date BETWEEN start_date AND end_date)
)
) AS conflicts;

IF v_conflicts = 0 THEN
SET v_is_available = TRUE;
END IF;
END IF;

RETURN v_is_available;
END//

-- ============================================
-- 3. TRIGGERS FOR AVAILABILITY VALIDATION
-- ============================================

-- 3.1 Trigger to prevent double-booking on INSERT
CREATE TRIGGER prevent_double_booking_before_insert
BEFORE INSERT ON rental_request
FOR EACH ROW
BEGIN
DECLARE v_conflicts INT;
DECLARE v_car_status VARCHAR(20);

-- Check if car is available
SELECT status INTO v_car_status
FROM car
WHERE car_id = NEW.car_id;

IF v_car_status != 'available' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Car is not available for rent';
END IF;

-- Check for overlapping requests
SELECT COUNT(*) INTO v_conflicts
FROM rental_request
WHERE car_id = NEW.car_id
AND status IN ('pending', 'approved')
AND request_id != NEW.request_id
AND (
(start_date BETWEEN NEW.start_date AND NEW.end_date)
OR (end_date BETWEEN NEW.start_date AND NEW.end_date)
OR (NEW.start_date BETWEEN start_date AND end_date)
);

IF v_conflicts > 0 THEN
SIGNAL SQLSTATE '45001'
SET MESSAGE_TEXT = 'Car is already booked for these dates';
END IF;

-- Check for active contracts
SELECT COUNT(*) INTO v_conflicts
FROM rental_contract
WHERE car_id = NEW.car_id
AND contract_status = 'active'
AND (
(start_date BETWEEN NEW.start_date AND NEW.end_date)
OR (end_date BETWEEN NEW.start_date AND NEW.end_date)
OR (NEW.start_date BETWEEN start_date AND end_date)
);

IF v_conflicts > 0 THEN
SIGNAL SQLSTATE '45002'
SET MESSAGE_TEXT = 'Car has an active contract for these dates';
END IF;
END//

-- 3.2 Trigger to update car availability when request is approved
CREATE TRIGGER after_request_approval
AFTER UPDATE ON rental_request
FOR EACH ROW
BEGIN
IF NEW.status = 'approved' AND OLD.status = 'pending' THEN
UPDATE car SET status = 'reserved' WHERE car_id = NEW.car_id;
END IF;
END//

-- 3.3 Trigger to update car availability on contract changes
CREATE TRIGGER update_car_availability_on_contract
AFTER UPDATE ON rental_contract
FOR EACH ROW
BEGIN
-- When contract becomes active, mark car as rented
IF NEW.contract_status = 'active' AND OLD.contract_status != 'active' THEN
UPDATE car
SET status = 'rented'
WHERE car_id = NEW.car_id;
END IF;

-- When contract is completed, mark car as available
IF NEW.contract_status = 'completed' AND OLD.contract_status != 'completed' THEN
UPDATE car
SET status = 'available'
WHERE car_id = NEW.car_id;
END IF;

-- When contract is cancelled, mark car as available
IF NEW.contract_status = 'cancelled' AND OLD.contract_status != 'cancelled' THEN
UPDATE car
SET status = 'available'
WHERE car_id = NEW.car_id;
END IF;
END//

-- ============================================
-- 4. STORED PROCEDURES
-- ============================================

-- 4.1 Procedure to create rental request with validation
CREATE PROCEDURE CreateRentalRequestWithValidation(
IN p_customer_id INT,
IN p_car_id INT,
IN p_start_date DATE,
IN p_end_date DATE,
IN p_pickup_location VARCHAR(100),
IN p_dropoff_location VARCHAR(100),
OUT p_result_message VARCHAR(255),
OUT p_success BOOLEAN
)
BEGIN
DECLARE v_is_available BOOLEAN;
DECLARE v_car_status VARCHAR(20);
DECLARE v_daily_rate DECIMAL(10,2);
DECLARE v_conflicting_requests INT;
DECLARE v_conflicting_contracts INT;

-- Start transaction
START TRANSACTION;

-- Get car details
SELECT status, daily_rate INTO v_car_status, v_daily_rate
FROM car
WHERE car_id = p_car_id;

-- Check 1: Is car marked as available in its status?
IF v_car_status != 'available' THEN
SET p_result_message = CONCAT('Car is not available. Current status: ', v_car_status);
SET p_success = FALSE;
ROLLBACK;
ELSE
-- Check 2: Are there any overlapping rental requests (pending or approved)?
SELECT COUNT(*) INTO v_conflicting_requests
FROM rental_request
WHERE car_id = p_car_id
AND status IN ('pending', 'approved')
AND (
(start_date BETWEEN p_start_date AND p_end_date)
OR (end_date BETWEEN p_start_date AND p_end_date)
OR (p_start_date BETWEEN start_date AND end_date)
);

-- Check 3: Are there any overlapping active contracts?
SELECT COUNT(*) INTO v_conflicting_contracts
FROM rental_contract
WHERE car_id = p_car_id
AND contract_status = 'active'
AND (
(start_date BETWEEN p_start_date AND p_end_date)
OR (end_date BETWEEN p_start_date AND p_end_date)
OR (p_start_date BETWEEN start_date AND end_date)
);

-- If no conflicts, create the request
IF v_conflicting_requests = 0 AND v_conflicting_contracts = 0 THEN
INSERT INTO rental_request (
request_uuid,
customer_id,
car_id,
start_date,
end_date,
pickup_location,
dropoff_location,
daily_rate_at_request,
status
) VALUES (
UUID(),
p_customer_id,
p_car_id,
p_start_date,
p_end_date,
p_pickup_location,
p_dropoff_location,
v_daily_rate,
'pending'
);

SET p_result_message = 'Rental request created successfully. Awaiting approval.';
SET p_success = TRUE;
COMMIT;
ELSE
SET p_result_message = 'Car is already booked for selected dates.';
SET p_success = FALSE;
ROLLBACK;
END IF;
END IF;
END//

-- 4.2 Procedure to check car availability for dates
CREATE PROCEDURE CheckCarAvailabilityForDates(
IN p_car_id INT,
IN p_start_date DATE,
IN p_end_date DATE
)
BEGIN
DECLARE v_is_available BOOLEAN;
DECLARE v_car_details VARCHAR(255);

-- Get car details
SELECT CONCAT(make, ' ', model, ' (', year, ') - ', license_plate)
INTO v_car_details
FROM car
WHERE car_id = p_car_id;

-- Check availability
SET v_is_available = IsCarAvailable(p_car_id, p_start_date, p_end_date);

-- Return result
SELECT
p_car_id AS car_id,
v_car_details AS car_details,
p_start_date AS requested_start,
p_end_date AS requested_end,
v_is_available AS is_available,
CASE
WHEN v_is_available THEN 'Car is available for these dates'
ELSE 'Car is NOT available for these dates'
END AS message;
END//

DELIMITER ;

-- ============================================
-- 5. VIEWS FOR AVAILABILITY REPORTING
-- ============================================

-- 5.1 View: Available cars right now
CREATE VIEW available_cars_now AS
SELECT
car_id,
CONCAT(make, ' ', model, ' ', year) AS car_name,
license_plate,
daily_rate,
color,
location
FROM car
WHERE status = 'available';

-- 5.2 View: Pending requests with car details
CREATE VIEW pending_requests_with_availability AS
SELECT
r.request_id,
CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
CONCAT(car.make, ' ', car.model) AS car_name,
car.license_plate,
r.start_date,
r.end_date,
r.estimated_total,
r.request_date,
r.status,
CASE
WHEN IsCarAvailable(r.car_id, r.start_date, r.end_date) THEN 'Available'
ELSE 'Not Available'
END AS current_availability
FROM rental_request r
JOIN customer c ON r.customer_id = c.customer_id
JOIN car ON r.car_id = car.car_id
WHERE r.status = 'pending';

-- 5.3 View: Active rentals
CREATE VIEW active_rentals_view AS
SELECT
cont.contract_number,
CONCAT(cust.first_name, ' ', cust.last_name) AS customer,
CONCAT(car.make, ' ', car.model, ' ', car.year) AS car,
car.license_plate,
cont.start_date,
cont.end_date,
DATEDIFF(cont.end_date, cont.start_date) AS rental_days,
cont.final_amount,
cont.payment_status,
cont.contract_status
FROM rental_contract cont
JOIN customer cust ON cont.customer_id = cust.customer_id
JOIN car ON cont.car_id = car.car_id
WHERE cont.contract_status = 'active';

-- 5.4 View: Availability summary
CREATE VIEW availability_summary AS
SELECT
status,
COUNT(*) AS total_cars,
CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM car), 1), '%') AS percentage
FROM car
GROUP BY status;

-- ============================================
-- 6. SAMPLE DATA INSERTION
-- ============================================

-- Insert Admin
INSERT INTO admin (admin_uuid, username, password_hash, full_name, email, role) VALUES
(UUID(), 'admin_john', '$2y$10$YourHashHere', 'John Manager', 'john@carrental.com', 'super_admin'),
(UUID(), 'admin_jane', '$2y$10$YourHashHere', 'Jane Smith', 'jane@carrental.com', 'fleet_manager');

-- Insert Customers (matching your object diagram)
INSERT INTO customer (customer_uuid, first_name, last_name, email, phone, license_number, license_expiry, date_of_birth) VALUES
(UUID(), 'John', 'Smith', 'john.smith@email.com', '555-0101', 'DL123456', '2025-12-31', '1990-05-15'),
(UUID(), 'Maria', 'Garcia', 'maria.garcia@email.com', '555-0102', 'DL789012', '2025-11-30', '1988-08-22'),
(UUID(), 'David', 'Brown', 'david.brown@email.com', '555-0103', 'DL345678', '2025-10-15', '1992-03-10');

-- Insert Cars (matching your object diagram)
INSERT INTO car (car_uuid, make, model, year, license_plate, vin, daily_rate, status, color, location) VALUES
(UUID(), 'Toyota', 'Camry', 2022, 'ABC-1234', '1HGBH41JXMN109186', 45.00, 'available', 'Silver', 'Downtown'),
(UUID(), 'Honda', 'Civic', 2023, 'XYZ-5678', '2HGFA16587H123456', 40.00, 'available', 'Blue', 'Downtown'),
(UUID(), 'Ford', 'Raptor', 2024, 'DEF-9012', '3FA6P0HD9MR123456', 50.00, 'maintenance', 'Black', 'Airport'),
(UUID(), 'Tesla', 'Model 3', 2023, 'TES-1234', '5YJ3E1EA7KF123456', 80.00, 'available', 'Red', 'Downtown');

-- Insert Rental Requests (matching your object diagram)
INSERT INTO rental_request (request_uuid, customer_id, car_id, start_date, end_date, daily_rate_at_request, status, pickup_location, dropoff_location) VALUES
(UUID(), 1, 1, '2024-01-15', '2024-01-20', 45.00, 'pending', 'Downtown', 'Downtown'),
(UUID(), 2, 2, '2024-01-16', '2024-01-18', 40.00, 'pending', 'Downtown', 'Airport'),
(UUID(), 3, 4, '2024-02-01', '2024-02-05', 80.00, 'pending', 'Downtown', 'Downtown');

-- ============================================
-- 7. ADDITIONAL INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX idx_car_status ON car(status);
CREATE INDEX idx_request_status ON rental_request(status);
CREATE INDEX idx_contract_dates ON rental_contract(start_date, end_date);
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_car_make_model ON car(make, model);
CREATE INDEX idx_rental_request_dates ON rental_request(start_date, end_date);
CREATE INDEX idx_rental_contract_car_dates ON rental_contract(car_id, start_date, end_date);

-- ============================================
-- 8. USAGE EXAMPLES
-- ============================================

/*

-- Check if a specific car is available
SELECT IsCarAvailable(1, '2024-01-15', '2024-01-20') AS is_available;

-- Check availability with details
CALL CheckCarAvailabilityForDates(1, '2024-01-15', '2024-01-20');

-- Create a rental request
CALL CreateRentalRequestWithValidation(
1, -- customer_id
1, -- car_id
'2024-02-10', -- start_date
'2024-02-15', -- end_date
'Downtown', -- pickup
'Downtown', -- dropoff
@message,
@success
);
SELECT @message, @success;

-- View available cars
SELECT * FROM available_cars_now;

-- View availability summary
SELECT * FROM availability_summary;

-- Approve a request (this will trigger car status update)
UPDATE rental_request SET status = 'approved', reviewed_by = 1, reviewed_at = NOW()
WHERE request_id = 1;

-- Create contract from approved request
INSERT INTO rental_contract (
contract_uuid, request_id, customer_id, car_id, admin_id,
contract_number, start_date, end_date, daily_rate, total_amount
)
SELECT
UUID(), request_id, customer_id, car_id, 1,
CONCAT('CNT-', DATE_FORMAT(NOW(), '%Y%m%d-'), LPAD(request_id, 4, '0')),
start_date, end_date, daily_rate_at_request, estimated_total
FROM rental_request
WHERE request_id = 1;

-- Activate contract (this will trigger car status update to 'rented')
UPDATE rental_contract SET contract_status = 'active' WHERE contract_id = 1;

-- Complete contract (this will trigger car status update to 'available')
UPDATE rental_contract SET
contract_status = 'completed',
actual_return = NOW(),
odometer_end = 12500
WHERE contract_id = 1;

*/

-- ============================================
-- 9. FINAL CHECK: Display sample data
-- ============================================

SELECT 'DATABASE CREATED SUCCESSFULLY!' AS status;
SELECT CONCAT('Total Cars: ', COUNT(*)) AS info FROM car;
SELECT CONCAT('Available Cars: ', COUNT(*)) AS info FROM car WHERE status = 'available';
SELECT CONCAT('Pending Requests: ', COUNT(*)) AS info FROM rental_request WHERE status = 'pending';


SELECT * FROM customer;

SELECT * FROM car;

SELECT * FROM rental_request WHERE status = 'pending';

SELECT status, COUNT(*) FROM car GROUP BY status;