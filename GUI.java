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
    JButton C = new JButton("Create");
    JButton R = new JButton("Read");
    JButton U = new JButton("Update");
    JButton D = new JButton("Delete");
    JButton Rent = new JButton("Rent");
    JTextArea textArea;
    
    Connection conn;

    public GUI(String role) {
        setTitle("Car Rental Database CRUD GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        JPanel left = new JPanel();
        JPanel right = new JPanel();

        // Fixed: use .equals() instead of == for String comparison
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
            R.addActionListener(this);

            left.add(Rent);
            right.add(R);
        }

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        textArea = new JTextArea("Database output will appear here.", 15, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        connectDatabase();

        setVisible(true);
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/car_rental_agency","root","root");
            textArea.setText("Connected to database");
        } catch (Exception e) {
            textArea.setText("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == C) {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID:"));
                String make = JOptionPane.showInputDialog("Enter Make:");
                String model = JOptionPane.showInputDialog("Enter Model:");
                int year = Integer.parseInt(JOptionPane.showInputDialog("Enter Year:"));
                double rate = Double.parseDouble(JOptionPane.showInputDialog("Enter Rate:"));
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
        } else if (source == R) {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID or 0 for all:"));

                if (id == 0) {
                    String sql = "SELECT * FROM Car";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append("ID: ").append(rs.getInt("car_id"))
                          .append(" | Make: ").append(rs.getString("make"))
                          .append(" | Model: ").append(rs.getString("model"))
                          .append(" | Year: ").append(rs.getInt("year"))
                          .append(" | Rate: ").append(rs.getDouble("rate"))
                          .append(" | Available: ").append(rs.getString("is_available"))
                          .append("\n");
                    }
                    textArea.setText(sb.toString());
                } else {
                    String sql = "SELECT * FROM Car WHERE car_id=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        textArea.setText(
                            "ID: " + rs.getInt("car_id") +
                            " | Make: " + rs.getString("make") +
                            " | Model: " + rs.getString("model") +
                            " | Year: " + rs.getInt("year") +
                            " | Rate: " + rs.getDouble("rate") +
                            " | Available: " + rs.getString("is_available")
                        );
                    } else {
                        textArea.setText("Car not found.");
                    }
                }
            } catch (Exception ex) {
                textArea.setText("Error reading data: " + ex.getMessage());
            }
        } else if (source == U) {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to update:"));
                String make = JOptionPane.showInputDialog("Enter new Make:");
                String model = JOptionPane.showInputDialog("Enter new Model:");
                int year = Integer.parseInt(JOptionPane.showInputDialog("Enter new Year:"));
                double rate = Double.parseDouble(JOptionPane.showInputDialog("Enter new Rate:"));
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
        } else if (source == D) {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to delete:"));
                String sql = "DELETE FROM Car WHERE car_id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                int rows = pst.executeUpdate();
                textArea.setText(rows > 0 ? "Car deleted successfully." : "Car not found.");
            } catch (Exception ex) {
                textArea.setText("Error deleting car: " + ex.getMessage());
            }
        } else if (source == Rent) {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to rent:"));
                // Rent logic to be implemented
                textArea.setText("Rent functionality coming soon for car ID: " + id);
            } catch (Exception ex) {
                textArea.setText("Error renting car: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {}
}
