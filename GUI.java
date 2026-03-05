package pckExer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Car {
    int id;
    String make;
    String model;
    int year;
    double rate;
    boolean isAvailable;
    String separator = "   |   ";

    public Car(int id, String make, String model, int year, double rate, boolean isAvailable) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.rate = rate;
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "ID: " + id + separator + "Make: " + make + separator + "Model: " + model + separator + "Year: " + year
                + separator + "Rate: $" + rate + separator + "Available: " + isAvailable;
    }
}

public class GUI extends JFrame implements ActionListener {
    JButton C       = new JButton("Create");
    JButton R       = new JButton("Read");
    JButton U       = new JButton("Update");
    JButton D       = new JButton("Delete");
    JButton Rent    = new JButton("Rent");
    JButton Payment = new JButton("Payment");
    JTextArea textArea;

    Connection conn;
    String role;
    String username; // logged-in username, used to filter customer requests

    public GUI(String role, String username) {
        this.role     = role;
        this.username = username;

        setTitle("Car Rental - " + role + " (" + username + ")");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top   = new JPanel(new BorderLayout());
        JPanel left  = new JPanel();
        JPanel right = new JPanel();

        if (role.equals("Admin")) {
            C.addActionListener(this);
            R.addActionListener(this);
            U.addActionListener(this);
            D.addActionListener(this);
            left.add(C);
            right.add(R);
            right.add(U);
            right.add(D);
        } else if (role.equals("Customer")) {
            Rent.addActionListener(this);
            Payment.addActionListener(this);
            R.addActionListener(this);
            left.add(Rent);
            left.add(Payment);
            right.add(R);
        }

        top.add(left,  BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        textArea = new JTextArea("Database output will appear here.", 15, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(top,        BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        connectDatabase();
        setVisible(true);
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/car_rental_agency", "root", "Apr@2024102110");
            textArea.setText("Connected to database");
        } catch (Exception e) {
            textArea.setText("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // ── CREATE ────────────────────────────────────────────────────────────
        if (source == C) {
            try {
                int id       = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID:"));
                String make  = JOptionPane.showInputDialog("Enter Make:");
                String model = JOptionPane.showInputDialog("Enter Model:");
                int year     = Integer.parseInt(JOptionPane.showInputDialog("Enter Year:"));
                double rate  = Double.parseDouble(JOptionPane.showInputDialog("Enter Rate:"));
                boolean isAvailable = JOptionPane.showConfirmDialog(null,
                        "Is the car available?", "Availability",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

                String sql = "INSERT INTO Car VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.setString(2, make);
                pst.setString(3, model);
                pst.setInt(4, year);
                pst.setDouble(5, rate);
                pst.setBoolean(6, isAvailable);
                pst.executeUpdate();
                textArea.setText("Car added successfully!");
            } catch (Exception ex) {
                textArea.setText("Error inserting car: " + ex.getMessage());
            }

        // ── READ ──────────────────────────────────────────────────────────────
        } else if (source == R) {
            String[] options = role.equals("Admin")
                ? new String[]{"Cars", "All Rental Requests"}
                : new String[]{"Cars", "My Rental Requests"};

            int choice = JOptionPane.showOptionDialog(this,
                "What would you like to view?", "Read",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

            if (choice == 0) {
                // View Cars
                try {
                    int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID or 0 for all:"));
                    if (id == 0) {
                        PreparedStatement pst = conn.prepareStatement("SELECT * FROM Car");
                        ResultSet rs = pst.executeQuery();
                        StringBuilder sb = new StringBuilder();
                        while (rs.next()) {
                            sb.append("ID: ").append(rs.getInt("car_id"))
                              .append(" | Make: ").append(rs.getString("make"))
                              .append(" | Model: ").append(rs.getString("model"))
                              .append(" | Year: ").append(rs.getInt("year"))
                              .append(" | Rate: $").append(rs.getDouble("rate"))
                              .append(" | Available: ").append(rs.getString("is_available"))
                              .append("\n");
                        }
                        textArea.setText(sb.length() > 0 ? sb.toString() : "No cars found.");
                    } else {
                        PreparedStatement pst = conn.prepareStatement("SELECT * FROM Car WHERE car_id=?");
                        pst.setInt(1, id);
                        ResultSet rs = pst.executeQuery();
                        if (rs.next()) {
                            textArea.setText(
                                "ID: " + rs.getInt("car_id") +
                                " | Make: " + rs.getString("make") +
                                " | Model: " + rs.getString("model") +
                                " | Year: " + rs.getInt("year") +
                                " | Rate: $" + rs.getDouble("rate") +
                                " | Available: " + rs.getString("is_available")
                            );
                        } else {
                            textArea.setText("Car not found.");
                        }
                    }
                } catch (Exception ex) {
                    textArea.setText("Error reading cars: " + ex.getMessage());
                }

            } else if (choice == 1) {
                // View Rental Requests
                try {
                    StringBuilder sb = new StringBuilder();

                    if (role.equals("Admin")) {
                        // Admin sees ALL requests with customer name and car info
                        String sql =
                            "SELECT rr.request_id, rr.status, rr.start_date, rr.end_date, rr.total_cost, " +
                            "       c.name AS customer_name, " +
                            "       car.make, car.model, car.year " +
                            "FROM RentalRequest rr " +
                            "JOIN Customer c ON rr.customer_id = c.customer_id " +
                            "JOIN Car car ON rr.car_id = car.car_id " +
                            "ORDER BY rr.request_date DESC";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        ResultSet rs = pst.executeQuery();
                        sb.append("=== ALL RENTAL REQUESTS ===\n\n");
                        while (rs.next()) {
                            sb.append("Request ID: ").append(rs.getString("request_id"))
                              .append(" | Customer: ").append(rs.getString("customer_name"))
                              .append(" | Car: ").append(rs.getString("make")).append(" ")
                                                 .append(rs.getString("model")).append(" (")
                                                 .append(rs.getInt("year")).append(")")
                              .append(" | Status: ").append(rs.getString("status"))
                              .append(" | From: ").append(rs.getString("start_date"))
                              .append(" To: ").append(rs.getString("end_date"))
                              .append(" | Total: $").append(rs.getDouble("total_cost"))
                              .append("\n");
                        }
                    } else {
                        // Customer sees ONLY their own requests, filtered by username
                        String sql =
                            "SELECT rr.request_id, rr.status, rr.start_date, rr.end_date, rr.total_cost, " +
                            "       car.make, car.model, car.year " +
                            "FROM RentalRequest rr " +
                            "JOIN Customer c ON rr.customer_id = c.customer_id " +
                            "JOIN User u ON c.login_id = u.login_id " +
                            "JOIN Car car ON rr.car_id = car.car_id " +
                            "WHERE u.username = ? " +
                            "ORDER BY rr.request_date DESC";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setString(1, username);
                        ResultSet rs = pst.executeQuery();
                        sb.append("=== MY RENTAL REQUESTS ===\n\n");
                        while (rs.next()) {
                            sb.append("Request ID: ").append(rs.getString("request_id"))
                              .append(" | Car: ").append(rs.getString("make")).append(" ")
                                                 .append(rs.getString("model")).append(" (")
                                                 .append(rs.getInt("year")).append(")")
                              .append(" | Status: ").append(rs.getString("status"))
                              .append(" | From: ").append(rs.getString("start_date"))
                              .append(" To: ").append(rs.getString("end_date"))
                              .append(" | Total: $").append(rs.getDouble("total_cost"))
                              .append("\n");
                        }
                    }

                    textArea.setText(sb.length() > 30 ? sb.toString() : sb.toString() + "No requests found.");
                } catch (Exception ex) {
                    textArea.setText("Error reading requests: " + ex.getMessage());
                }
            }

        // ── UPDATE ────────────────────────────────────────────────────────────
        } else if (source == U) {
            String target = JOptionPane.showInputDialog("What would you like to update? Type CAR or REQUEST:");
            if (target == null) return;

            if (target.equalsIgnoreCase("car")) {
                try {
                    int id       = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to update:"));
                    String make  = JOptionPane.showInputDialog("Enter new Brand:");
                    String model = JOptionPane.showInputDialog("Enter new Model:");
                    int year     = Integer.parseInt(JOptionPane.showInputDialog("Enter new Year:"));
                    double rate  = Double.parseDouble(JOptionPane.showInputDialog("Enter new Rate:"));
                    boolean isAvailable = JOptionPane.showConfirmDialog(null,
                            "Is the car available?", "Availability",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

                    String sql = "UPDATE Car SET make=?, model=?, year=?, rate=?, is_available=? WHERE car_id=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, make);
                    pst.setString(2, model);
                    pst.setInt(3, year);
                    pst.setDouble(4, rate);
                    pst.setString(5, isAvailable ? "Yes" : "No");
                    pst.setInt(6, id);
                    int rows = pst.executeUpdate();
                    textArea.setText(rows > 0 ? "Car updated successfully." : "Car not found.");
                } catch (Exception ex) {
                    textArea.setText("Error updating car: " + ex.getMessage());
                }

            } else if (target.equalsIgnoreCase("request")) {
                try {
                    String requestId = JOptionPane.showInputDialog("Enter Request ID to update:");
                    if (requestId == null) return;

                    // Check if request exists first
                    PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT request_id FROM RentalRequest WHERE request_id=?");
                    checkStmt.setString(1, requestId);
                    ResultSet checkRS = checkStmt.executeQuery();

                    if (!checkRS.next()) {
                        textArea.setText("Request ID \"" + requestId + "\" not found. Please check and try again.");
                        return;
                    }

                    String[] statusOptions = {"Pending", "Approved", "Rejected", "Completed"};
                    String newStatus = (String) JOptionPane.showInputDialog(
                        this,
                        "Select new status for Request " + requestId + ":",
                        "Update Request Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        statusOptions,
                        statusOptions[0]
                    );
                    if (newStatus == null) return;

                    String sql = "UPDATE RentalRequest SET status=? WHERE request_id=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, newStatus);
                    pst.setString(2, requestId);
                    int rows = pst.executeUpdate();
                    textArea.setText(rows > 0
                        ? "Request " + requestId + " status updated to: " + newStatus
                        : "Request ID \"" + requestId + "\" not found. Please check and try again.");
                } catch (Exception ex) {
                    textArea.setText("Error updating request: " + ex.getMessage());
                }

            } else {
                textArea.setText("Invalid input. Please type CAR or REQUEST.");
            }

        // ── DELETE ────────────────────────────────────────────────────────────
        } else if (source == D) {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to delete:"));
                PreparedStatement pst = conn.prepareStatement("DELETE FROM Car WHERE car_id=?");
                pst.setInt(1, id);
                int rows = pst.executeUpdate();
                textArea.setText(rows > 0 ? "Car deleted successfully." : "Car not found.");
            } catch (Exception ex) {
                textArea.setText("Error deleting car: " + ex.getMessage());
            }

        // ── RENT ──────────────────────────────────────────────────────────────
        } else if (source == Rent) {
            try {
                // Get customer_id from logged-in username automatically
                String getCustomerId = "SELECT c.customer_id FROM Customer c JOIN User u ON c.login_id = u.login_id WHERE u.username=?";
                PreparedStatement idStmt = conn.prepareStatement(getCustomerId);
                idStmt.setString(1, username);
                ResultSet idRS = idStmt.executeQuery();
                if (!idRS.next()) {
                    textArea.setText("Customer account not found.");
                    return;
                }
                String customerId = idRS.getString("customer_id");

                // Check if customer has payment method
                String paymentCheck = "SELECT * FROM Payment WHERE customerId=?";
                PreparedStatement paymentStmt = conn.prepareStatement(paymentCheck);
                paymentStmt.setString(1, customerId);
                ResultSet paymentRS = paymentStmt.executeQuery();
                if (!paymentRS.next()) {
                    textArea.setText("You must add a payment method before renting.");
                    return;
                }

                String action = JOptionPane.showInputDialog("Type RENT or UNRENT:");
                if (action == null) return;

                if (action.equalsIgnoreCase("rent")) {
                    int carId = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID:"));

                    PreparedStatement carStmt = conn.prepareStatement("SELECT * FROM Car WHERE car_id=?");
                    carStmt.setInt(1, carId);
                    ResultSet carRS = carStmt.executeQuery();

                    if (carRS.next()) {
                        if (carRS.getString("is_available").equals("No")) {
                            textArea.setText("Car is not available.");
                            return;
                        }

                        // Generate short request ID like REQ001, REQ002, etc.
                        PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) FROM RentalRequest");
                        ResultSet countRS = countStmt.executeQuery();
                        countRS.next();
                        int count = countRS.getInt(1) + 1;
                        String requestId = String.format("REQ%03d", count);
                        PreparedStatement requestStmt = conn.prepareStatement(
                            "INSERT INTO RentalRequest (request_id, customer_id, car_id, status) VALUES (?, ?, ?, 'Pending')");
                        requestStmt.setString(1, requestId);
                        requestStmt.setString(2, customerId);
                        requestStmt.setInt(3, carId);
                        requestStmt.executeUpdate();

                        PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE Car SET is_available='No' WHERE car_id=?");
                        updateStmt.setInt(1, carId);
                        updateStmt.executeUpdate();

                        textArea.setText("Rental request created. Request ID: " + requestId);
                    } else {
                        textArea.setText("Car not found.");
                    }

                } else if (action.equalsIgnoreCase("unrent")) {
                    String requestId = JOptionPane.showInputDialog("Enter Rental Request ID:");

                    PreparedStatement requestStmt = conn.prepareStatement(
                        "SELECT status, car_id FROM RentalRequest WHERE request_id=?");
                    requestStmt.setString(1, requestId);
                    ResultSet rs = requestStmt.executeQuery();

                    if (rs.next()) {
                        String status = rs.getString("status");
                        int carId     = rs.getInt("car_id");

                        if (status.equals("Pending")) {
                            PreparedStatement pst = conn.prepareStatement(
                                "UPDATE RentalRequest SET status='Cancelled' WHERE request_id=?");
                            pst.setString(1, requestId);
                            pst.executeUpdate();
                            textArea.setText("Rental request cancelled.");

                        } else if (status.equals("Approved")) {
                            PreparedStatement pst = conn.prepareStatement(
                                "UPDATE RentalRequest SET status='Completed' WHERE request_id=?");
                            pst.setString(1, requestId);
                            pst.executeUpdate();

                            PreparedStatement carStmt = conn.prepareStatement(
                                "UPDATE Car SET is_available='Yes' WHERE car_id=?");
                            carStmt.setInt(1, carId);
                            carStmt.executeUpdate();
                            textArea.setText("Rental completed. Car returned.");

                        } else {
                            textArea.setText("This request cannot be unrented (status: " + status + ").");
                        }
                    } else {
                        textArea.setText("Rental request not found.");
                    }

                } else {
                    textArea.setText("Invalid action. Please type RENT or UNRENT.");
                }

            } catch (Exception ex) {
                textArea.setText("Error processing rental: " + ex.getMessage());
            }

        // ── PAYMENT ───────────────────────────────────────────────────────────
        } else if (source == Payment) {
            try {
                // Get customer_id from logged-in username automatically
                String getCustomerId = "SELECT c.customer_id FROM Customer c JOIN User u ON c.login_id = u.login_id WHERE u.username=?";
                PreparedStatement idStmt = conn.prepareStatement(getCustomerId);
                idStmt.setString(1, username);
                ResultSet idRS = idStmt.executeQuery();
                if (!idRS.next()) {
                    textArea.setText("Customer account not found.");
                    return;
                }
                String customerId = idRS.getString("customer_id");

                String cardNum  = JOptionPane.showInputDialog("Enter Card Number:");
                String cardName = JOptionPane.showInputDialog("Enter Card Name:");
                String cvv      = JOptionPane.showInputDialog("Enter CVV:");

                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM Payment WHERE customerId=?");
                checkStmt.setString(1, customerId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE Payment SET cardNum=?, cardName=?, cvv=? WHERE customerId=?");
                    updateStmt.setString(1, cardNum);
                    updateStmt.setString(2, cardName);
                    updateStmt.setString(3, cvv);
                    updateStmt.setString(4, customerId);
                    updateStmt.executeUpdate();
                    textArea.setText("Payment information updated.");
                } else {
                    PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO Payment (customerId, cardNum, cardName, cvv) VALUES (?, ?, ?, ?)");
                    insertStmt.setString(1, customerId);
                    insertStmt.setString(2, cardNum);
                    insertStmt.setString(3, cardName);
                    insertStmt.setString(4, cvv);
                    insertStmt.executeUpdate();
                    textArea.setText("Payment method added.");
                }

            } catch (Exception ex) {
                textArea.setText("Error processing payment method: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {}
}
