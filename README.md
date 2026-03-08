CAR RENTAL AGENCY FLEET MANAGEMENT SYSTEM

Description
The Car Rental Agency Fleet Management System is a Java desktop application that 
allows administrators to manage car inventory and rental requests while customers 
can rent vehicles and manage payment information. The system uses Java Swing for 
the graphical user interface and MySQL for database management.

FEATURES

Admin Features
-Create car records
-View cars in the system
-Update car information
-Delete cars
-View all rental requests

Customer Features
-View available cars
-Rent a car
-Return rented cars
-Add or update payment information
-View personal rental requests

Technologies Used
-Java (Swing) for the graphical user interface
-MySQL for database management
-JDBC for database connectivity
-SQL for queries and database operations

DATABASE SETUP

Step 1
Install MySQL Server.

Step 2
Create the database by running the following command:

CREATE DATABASE car_rental_agency;
USE car_rental_agency;

Step 3
Run the provided SQL script to create all tables and insert the sample data.

How to Run the System

Step 1
Open the project in a Java IDE such as Eclipse, IntelliJ IDEA, or NetBeans.

Step 2
Ensure the MySQL server is running.

Step 3
Verify the database connection in the code:

jdbc:mysql://localhost:3306/car_rental_agency
username: root
password: root

Step 4
Run the file named login.java to start the application.

DEFAULT ACCOUNTS

Admin Account
Username: admin_sarah
Password: admin123

Customer Account
Username: john_customer
Password: password123

##Authors
KENDALL SUMMER BARTOLATA,
JULIANNA MIKHAILA REGIO,
FRANCO LUIS LEYSA
