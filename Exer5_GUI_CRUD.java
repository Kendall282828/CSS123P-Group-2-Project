package pckExer;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Exer5_GUI_CRUD extends JFrame implements ActionListener {

    JButton C = new JButton("Create");
    JButton R = new JButton("Read");
    JButton U = new JButton("Update");
    JButton D = new JButton("Delete");
    JTextArea textArea;

    Connection conn;

    public Exer5_GUI_CRUD() {

        setTitle("Car Rental Database CRUD GUI");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        C.addActionListener(this);
        R.addActionListener(this);
        U.addActionListener(this);
        D.addActionListener(this);

        JPanel left = new JPanel();
        left.add(C);

        JPanel right = new JPanel();
        right.add(R);
        right.add(U);
        right.add(D);

        JPanel top = new JPanel(new BorderLayout());
        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        textArea = new JTextArea(15, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        connectDatabase();

        setVisible(true);
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/car_rental_agency",
                "root",
                ""   // XAMPP default password is empty
            );
            textArea.setText("Connected to database successfully.\n");
        } catch (Exception e) {
            textArea.setText("Database connection failed.");
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == C) {
            createCar();
        } else if (e.getSource() == R) {
            readCar();
        } else if (e.getSource() == U) {
            updateCar();
        } else if (e.getSource() == D) {
            deleteCar();
        }
    }

    private void createCar() {
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
            textArea.setText("Car added successfully!\n\n");
            listAllCars();

        } catch (Exception ex) {
            textArea.setText("Error creating car.");
        }
    }

    private void readCar() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID or 0 for all:"));

            if (id == 0) {
                listAllCars();
                return;
            }

            String sql = "SELECT * FROM Car WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                textArea.setText(formatCar(rs));
            } else {
                textArea.setText("Car not found.");
            }

        } catch (Exception ex) {
            textArea.setText("Invalid input.");
        }
    }

    private void updateCar() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID to update:"));

            String make = JOptionPane.showInputDialog("Enter new Make:");
            String model = JOptionPane.showInputDialog("Enter new Model:");
            int year = Integer.parseInt(JOptionPane.showInputDialog("Enter new Year:"));
            double rate = Double.parseDouble(JOptionPane.showInputDialog("Enter new Rate:"));
            boolean isAvailable = JOptionPane.showConfirmDialog(null,
                    "Is the car available?", "Availability",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

            String sql = "UPDATE Car SET make=?, model=?, year=?, rate=?, isAvailable=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, make);
            pst.setString(2, model);
            pst.setInt(3, year);
            pst.setDouble(4, rate);
            pst.setBoolean(5, isAvailable);
            pst.setInt(6, id);

            int rows = pst.executeUpdate();

            if (rows > 0)
                textArea.setText("Car updated successfully.");
            else
                textArea.setText("Car not found.");

        } catch (Exception ex) {
            textArea.setText("Error updating car.");
        }
    }

    private void deleteCar() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID to delete:"));

            String sql = "DELETE FROM Car WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                textArea.setText("Car deleted successfully.\n\n");
                listAllCars();
            } else {
                textArea.setText("Car not found.");
            }

        } catch (Exception ex) {
            textArea.setText("Invalid input.");
        }
    }

    private void listAllCars() {
        try {
            String sql = "SELECT * FROM Car";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append(formatCar(rs)).append("\n");
            }

            textArea.setText(sb.toString());

        } catch (Exception e) {
            textArea.setText("Error fetching data.");
        }
    }

    private String formatCar(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("id") +
                " | Make: " + rs.getString("make") +
                " | Model: " + rs.getString("model") +
                " | Year: " + rs.getInt("year") +
                " | Rate: $" + rs.getDouble("rate") +
                " | Available: " + rs.getBoolean("isAvailable");
    }

    public static void main(String[] args) {
        new Exer5_GUI_CRUD();
    }
}    JButton U = new JButton("Update");
    JButton D = new JButton("Delete");
    JTextArea textArea;

    // Database as an ArrayList
    ArrayList<Car> cars = new ArrayList<>();

    public Exer5_GUI_CRUD() {
        // Setup frame
        setTitle("Car Rental Database CRUD GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add listeners
        C.addActionListener(this);
        R.addActionListener(this);
        U.addActionListener(this);
        D.addActionListener(this);

        // Top panel with buttons
        JPanel left = new JPanel();
        left.add(C);
        JPanel right = new JPanel();
        right.add(R);
        right.add(U);
        right.add(D);
        JPanel top = new JPanel(new BorderLayout());
        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        // Text area
        textArea = new JTextArea("Database output will appear here.", 15, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Add panels to frame
        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize fake database with some cars
        initializeDatabase();

        setVisible(true);
    }

    private void initializeDatabase() {
        cars.add(new Car(1, "Toyota", "Corolla", 2020, 35.0, true));
        cars.add(new Car(2, "Honda", "Civic", 2019, 30.0, true));
        cars.add(new Car(3, "Ford", "Mustang", 2021, 50.0, false));
        cars.add(new Car(4, "Chevrolet", "Camaro", 2022, 55.0, true));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == C) {
            // Create a new car
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID:"));
                Car found = findCarById(id);
                
                if (found != null){
                    textArea.setText("Car already exists.\n\n" + found.toString());
                }
                else if (id<=0){
                    textArea.setText("ID value is invalid.");
                }
                else {
                    String make = JOptionPane.showInputDialog("Enter Make:");
                    String model = JOptionPane.showInputDialog("Enter Model:");
                    int year = Integer.parseInt(JOptionPane.showInputDialog("Enter Year:"));
                    double rate = Double.parseDouble(JOptionPane.showInputDialog("Enter Rate:"));
                    boolean isAvailable = JOptionPane.showConfirmDialog(null, "Is the car available?", "Availability", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

                    cars.add(new Car(id, make, model, year, rate, isAvailable));
                    textArea.setText("Car added successfully!\n\n" + listAllCars());
                }
            } catch (Exception ex) {
                textArea.setText("Error creating car. Please enter valid data.");
            }
        } else if (source == R) {
            // Read a car
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to read or '0' to show all:"));
                if(id == 0){
                    textArea.setText(listAllCars());
                }
                else{
                    Car found = findCarById(id);
                    textArea.setText(found != null ? found.toString() : "Car not found.");
                }
            } catch (Exception ex) {
                textArea.setText("Invalid input.");
            }
        } else if (source == U) {
            // Update a car
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to update:"));
                Car car = findCarById(id);
                if (car != null) {
                    car.make = JOptionPane.showInputDialog("Enter new Make:", car.make);
                    car.model = JOptionPane.showInputDialog("Enter new Model:", car.model);
                    car.year = Integer.parseInt(JOptionPane.showInputDialog("Enter new Year:", car.year));
                    car.rate = Double.parseDouble(JOptionPane.showInputDialog("Enter new Rate:", car.rate));
                    car.isAvailable = JOptionPane.showConfirmDialog(null, "Is the car available?", "Availability", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                    textArea.setText("Car updated successfully!\n\n" + car.toString());
                } else {
                    textArea.setText("Car not found.");
                }
            } catch (Exception ex) {
                textArea.setText("Error updating car.");
            }
        } else if (source == D) {
            // Delete a car
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Car ID to delete:"));
                Car car = findCarById(id);
                if (car != null) {
                    cars.remove(car);
                    textArea.setText("Car deleted successfully!\n\n" + listAllCars());
                } else {
                    textArea.setText("Car not found.");
                }
            } catch (Exception ex) {
                textArea.setText("Invalid input.");
            }
        }
    }

    private Car findCarById(int id) {
        for (Car c : cars) {
            if (c.id == id) return c;
        }
        return null;
    }

    private String listAllCars() {
        if (cars.isEmpty()) return "No cars in the database.";
        StringBuilder sb = new StringBuilder();
        for (Car c : cars) {
            sb.append(c).append("\n");
        }
        return sb.toString();
    }
}

