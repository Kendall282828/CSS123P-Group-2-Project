-- =====================================================
-- CREATE DATABASE
-- =====================================================
CREATE DATABASE IF NOT EXISTS car_rental_agency;
USE car_rental_agency;

-- =====================================================
-- CREATE TABLES
-- =====================================================

-- 1. User table
CREATE TABLE User (
    login_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('Admin', 'Customer') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Admin table
CREATE TABLE Admin (
    admin_id VARCHAR(50) PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    FOREIGN KEY (login_id) REFERENCES User(login_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Customer table
CREATE TABLE Customer (
    customer_id VARCHAR(50) PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    FOREIGN KEY (login_id) REFERENCES User(login_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Car table
CREATE TABLE Car (
    car_id VARCHAR(50) PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    rate DECIMAL(10,2) NOT NULL,
    is_available ENUM('Yes', 'No') DEFAULT 'Yes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Rental Request table
CREATE TABLE RentalRequest (
    request_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    car_id VARCHAR(50) NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected', 'Completed') DEFAULT 'Pending',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_date DATE,
    end_date DATE,
    total_cost DECIMAL(10,2),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (car_id) REFERENCES Car(car_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indexes
CREATE INDEX idx_rental_customer ON RentalRequest(customer_id);
CREATE INDEX idx_rental_car ON RentalRequest(car_id);
CREATE INDEX idx_rental_status ON RentalRequest(status);
CREATE INDEX idx_car_availability ON Car(is_available);

-- 6. CarRentalAgency table
CREATE TABLE CarRentalAgency (
    agency_id INT PRIMARY KEY AUTO_INCREMENT,
    agency_name VARCHAR(100) DEFAULT 'CarRentalAgency',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. AgencyCars junction table
CREATE TABLE AgencyCars (
    agency_id INT,
    car_id VARCHAR(50),
    PRIMARY KEY (agency_id, car_id),
    FOREIGN KEY (agency_id) REFERENCES CarRentalAgency(agency_id) ON DELETE CASCADE,
    FOREIGN KEY (car_id) REFERENCES Car(car_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. AgencyPendingRequests junction table
CREATE TABLE AgencyPendingRequests (
    agency_id INT,
    request_id VARCHAR(50),
    PRIMARY KEY (agency_id, request_id),
    FOREIGN KEY (agency_id) REFERENCES CarRentalAgency(agency_id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES RentalRequest(request_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- INSERT SAMPLE DATA
-- =====================================================

-- Insert User records
INSERT INTO User (login_id, username, password, user_type) VALUES
('U001', 'john_customer', 'password123', 'Customer'),
('U002', 'jane_customer', 'password123', 'Customer'),
('U003', 'admin_sarah', 'admin123', 'Admin'),
('U004', 'admin_mike', 'admin123', 'Admin');

-- Insert Customers
INSERT INTO Customer (customer_id, login_id, name) VALUES
('CUST001', 'U001', 'John Smith'),
('CUST002', 'U002', 'Jane Doe');

-- Insert Admins
INSERT INTO Admin (admin_id, login_id, name) VALUES
('ADM001', 'U003', 'Sarah Johnson'),
('ADM002', 'U004', 'Mike Wilson');

-- Insert Cars
INSERT INTO Car (car_id, make, model, year, rate, is_available) VALUES
('CAR001', 'Toyota', 'Camry', 2022, 45.00, 'Yes'),
('CAR002', 'Honda', 'Civic', 2023, 40.00, 'No'),
('CAR003', 'Ford', 'Raptor', 2024, 50.00, 'Yes'),
('CAR004', 'Tesla', 'Model 3', 2023, 75.00, 'Yes'),
('CAR005', 'Chevrolet', 'Malibu', 2022, 42.00, 'No'),
('CAR006', 'BMW', 'X5', 2024, 95.00, 'Yes'),
('CAR007', 'Mercedes', 'C300', 2023, 85.00, 'No');

-- Insert Car Rental Agencies
INSERT INTO CarRentalAgency (agency_name) VALUES 
('Main Rental Agency'),
('Downtown Branch');

-- Link cars to agencies
INSERT INTO AgencyCars (agency_id, car_id) VALUES
(1, 'CAR001'), (1, 'CAR002'), (1, 'CAR003'), (1, 'CAR004'),
(1, 'CAR005'), (1, 'CAR006'), (1, 'CAR007'),
(2, 'CAR001'), (2, 'CAR003'), (2, 'CAR004'), (2, 'CAR006');

-- Insert Rental Requests
INSERT INTO RentalRequest (request_id, customer_id, car_id, status, start_date, end_date, total_cost) VALUES
('REQ001', 'CUST001', 'CAR003', 'Pending', '2024-03-01', '2024-03-05', 200.00),
('REQ002', 'CUST002', 'CAR002', 'Approved', '2024-02-15', '2024-02-20', 200.00),
('REQ003', 'CUST001', 'CAR005', 'Completed', '2024-02-01', '2024-02-05', 168.00),
('REQ004', 'CUST002', 'CAR007', 'Approved', '2024-02-20', '2024-02-25', 425.00),
('REQ005', 'CUST001', 'CAR001', 'Pending', '2024-03-10', '2024-03-15', 225.00);

-- Link pending requests to agencies
INSERT INTO AgencyPendingRequests (agency_id, request_id) VALUES
(1, 'REQ001'), (1, 'REQ005'), (2, 'REQ001');

-- =====================================================
-- CREATE VIEWS
-- =====================================================

CREATE VIEW CustomerView AS
SELECT c.customer_id, c.name, u.username, u.created_at as account_created
FROM Customer c JOIN User u ON c.login_id = u.login_id;

CREATE VIEW AdminView AS
SELECT a.admin_id, a.name, u.username, u.created_at as account_created
FROM Admin a JOIN User u ON a.login_id = u.login_id;

CREATE VIEW RentalRequestDetails AS
SELECT rr.request_id, rr.status, rr.request_date,
    DATE_FORMAT(rr.start_date, '%Y-%m-%d') as start_date,
    DATE_FORMAT(rr.end_date, '%Y-%m-%d') as end_date,
    rr.total_cost, c.customer_id, c.name as customer_name,
    car.car_id, car.make, car.model, car.year, car.rate, car.is_available as car_availability
FROM RentalRequest rr
JOIN Customer c ON rr.customer_id = c.customer_id
JOIN Car car ON rr.car_id = car.car_id;

CREATE VIEW CarInventoryView AS
SELECT c.car_id, c.make, c.model, c.year, c.rate, c.is_available,
    ac.agency_id, cra.agency_name,
    CASE WHEN c.is_available = 'Yes' THEN 'Available for Rent' ELSE 'Currently Rented' END as status_description
FROM Car c
JOIN AgencyCars ac ON c.car_id = ac.car_id
JOIN CarRentalAgency cra ON ac.agency_id = cra.agency_id;

CREATE VIEW AgencyDashboard AS
SELECT cra.agency_id, cra.agency_name,
    COUNT(DISTINCT ac.car_id) as total_cars,
    SUM(CASE WHEN c.is_available = 'Yes' THEN 1 ELSE 0 END) as available_cars,
    SUM(CASE WHEN c.is_available = 'No' THEN 1 ELSE 0 END) as rented_cars,
    COUNT(DISTINCT apr.request_id) as pending_requests,
    ROUND(AVG(c.rate), 2) as average_rate
FROM CarRentalAgency cra
LEFT JOIN AgencyCars ac ON cra.agency_id = ac.agency_id
LEFT JOIN Car c ON ac.car_id = c.car_id
LEFT JOIN AgencyPendingRequests apr ON cra.agency_id = apr.agency_id
GROUP BY cra.agency_id, cra.agency_name;

-- =====================================================
-- CREATE TRIGGERS
-- =====================================================

DELIMITER $$

CREATE TRIGGER update_car_on_approval
AFTER UPDATE ON RentalRequest
FOR EACH ROW
BEGIN
    IF NEW.status = 'Approved' AND OLD.status = 'Pending' THEN
        UPDATE Car SET is_available = 'No' WHERE car_id = NEW.car_id;
    END IF;
END$$

CREATE TRIGGER update_car_on_completion
AFTER UPDATE ON RentalRequest
FOR EACH ROW
BEGIN
    IF NEW.status = 'Completed' AND OLD.status = 'Approved' THEN
        UPDATE Car SET is_available = 'Yes' WHERE car_id = NEW.car_id;
    END IF;
END$$

CREATE TRIGGER calculate_total_cost
BEFORE UPDATE ON RentalRequest
FOR EACH ROW
BEGIN
    IF NEW.start_date IS NOT NULL AND NEW.end_date IS NOT NULL THEN
        SET NEW.total_cost = DATEDIFF(NEW.end_date, NEW.start_date) * 
            (SELECT rate FROM Car WHERE car_id = NEW.car_id);
    END IF;
END$$

DELIMITER ;

-- =====================================================
-- STORED PROCEDURES
-- =====================================================

DELIMITER $$

CREATE PROCEDURE RentCar(
    IN p_car_id VARCHAR(50),
    IN p_customer_id VARCHAR(50),
    IN p_request_id VARCHAR(50),
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    DECLARE car_available ENUM('Yes', 'No');
    
    START TRANSACTION;
    
    SELECT is_available INTO car_available FROM Car WHERE car_id = p_car_id FOR UPDATE;
    
    IF car_available = 'Yes' THEN
        UPDATE Car SET is_available = 'No' WHERE car_id = p_car_id;
        INSERT INTO RentalRequest (request_id, customer_id, car_id, status, start_date, end_date)
        VALUES (p_request_id, p_customer_id, p_car_id, 'Approved', p_start_date, p_end_date);
        COMMIT;
        SELECT 'Success' as result, 'Car rented successfully' as message;
    ELSE
        ROLLBACK;
        SELECT 'Error' as result, 'Car is not available' as message;
    END IF;
END$$

CREATE PROCEDURE ReturnCar(
    IN p_car_id VARCHAR(50),
    IN p_request_id VARCHAR(50)
)
BEGIN
    START TRANSACTION;
    
    UPDATE Car SET is_available = 'Yes' WHERE car_id = p_car_id;
    UPDATE RentalRequest SET status = 'Completed' 
    WHERE request_id = p_request_id AND car_id = p_car_id;
    
    COMMIT;
    SELECT 'Success' as result, 'Car returned successfully' as message;
END$$

DELIMITER ;

