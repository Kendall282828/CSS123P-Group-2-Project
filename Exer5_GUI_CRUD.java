package pckExer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

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

public class Exer5_GUI_CRUD extends JFrame implements ActionListener {
    // GUI components
    JButton C = new JButton("Create");
    JButton R = new JButton("Read");
    JButton U = new JButton("Update");
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

    public static void main(String[] args) {
        new Exer5_GUI_CRUD();
    }
}
