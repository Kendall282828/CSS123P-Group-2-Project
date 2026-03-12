package pckExer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Car {
    int id; String make; String model; int year; double rate; boolean isAvailable;
    String separator = "   |   ";
    public Car(int id, String make, String model, int year, double rate, boolean isAvailable) {
        this.id = id; this.make = make; this.model = model;
        this.year = year; this.rate = rate; this.isAvailable = isAvailable;
    }
    @Override public String toString() {
        return "ID: " + id + separator + "Make: " + make + separator + "Model: " + model
             + separator + "Year: " + year + separator + "Rate: $" + rate
             + separator + "Available: " + isAvailable;
    }
}

public class GUI extends JFrame implements ActionListener {

    static final Color BLACK      = new Color(10, 10, 10);
    static final Color PANEL_DARK = new Color(20, 18, 16);
    static final Color PANEL_MID  = new Color(28, 26, 23);
    static final Color RED        = new Color(200, 57, 43);
    static final Color RED_DARK   = new Color(160, 45, 34);
    static final Color WHITE      = new Color(245, 240, 232);
    static final Color GRAY       = new Color(138, 130, 120);
    static final Color BORDER_COL = new Color(46, 43, 39);

    JButton C       = new JButton("Create");
    JButton R       = new JButton("Read");
    JButton U       = new JButton("Update");
    JButton D       = new JButton("Delete");
    JButton Rent    = new JButton("Rent");
    JButton Payment = new JButton("Payment");
    JButton Logout  = new JButton("Logout");
    JTextArea textArea;

    Connection conn;
    String role;
    String username;

    public GUI(String role, String username) {
        this.role     = role;
        this.username = username;

        setTitle("Car Rental Agency — " + role);
        setSize(820, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BLACK);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildTopBar(), BorderLayout.NORTH);
        getContentPane().add(buildCenter(), BorderLayout.CENTER);

        connectDatabase();
        setVisible(true);
    }

    private JPanel buildTopBar() {
        JPanel redStrip = new JPanel();
        redStrip.setBackground(RED);
        redStrip.setPreferredSize(new Dimension(6, 60));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(PANEL_DARK);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel appTitle = new JLabel("🚗  CAR RENTAL AGENCY");
        appTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        appTitle.setForeground(WHITE);

        JLabel roleBadge = new JLabel("  " + role.toUpperCase() + "  ");
        roleBadge.setFont(new Font("Dialog", Font.BOLD, 10));
        roleBadge.setForeground(role.equals("Admin") ? RED : new Color(100, 160, 255));
        roleBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(role.equals("Admin") ? new Color(200,57,43,80) : new Color(100,160,255,80), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        roleBadge.setBackground(role.equals("Admin") ? new Color(200,57,43,30) : new Color(100,160,255,20));
        roleBadge.setOpaque(true);

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        userLabel.setForeground(GRAY);

        titlePanel.add(appTitle);
        titlePanel.add(Box.createHorizontalStrut(12));
        titlePanel.add(roleBadge);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(userLabel);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PANEL_DARK);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.add(redStrip,   BorderLayout.WEST);
        topBar.add(titlePanel, BorderLayout.CENTER);
        return topBar;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BLACK);
        center.add(buildSidebar(), BorderLayout.WEST);
        center.add(buildOutput(),  BorderLayout.CENTER);
        return center;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(PANEL_DARK);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL),
            BorderFactory.createEmptyBorder(20, 16, 20, 16)));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(160, 0));

        JLabel sectionLabel = new JLabel("ACTIONS");
        sectionLabel.setFont(new Font("Dialog", Font.BOLD, 9));
        sectionLabel.setForeground(GRAY);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sectionLabel);
        sidebar.add(Box.createVerticalStrut(12));

        if (role.equals("Admin")) {
            sidebar.add(sidebarButton(C, true));
            sidebar.add(Box.createVerticalStrut(6));
            sidebar.add(sidebarButton(R, false));
            sidebar.add(Box.createVerticalStrut(6));
            sidebar.add(sidebarButton(U, false));
            sidebar.add(Box.createVerticalStrut(6));
            sidebar.add(sidebarButton(D, false));
            C.addActionListener(this); R.addActionListener(this);
            U.addActionListener(this); D.addActionListener(this);
        } else if (role.equals("Customer")) {
            sidebar.add(sidebarButton(Rent, true));
            sidebar.add(Box.createVerticalStrut(6));
            sidebar.add(sidebarButton(Payment, false));
            sidebar.add(Box.createVerticalStrut(6));
            sidebar.add(sidebarButton(R, false));
            Rent.addActionListener(this);
            Payment.addActionListener(this);
            R.addActionListener(this);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(sidebarButton(Logout, false));
        Logout.addActionListener(this);

        return sidebar;
    }

    private JPanel buildOutput() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(BLACK);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_MID);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        JLabel headerLabel = new JLabel("OUTPUT");
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 9));
        headerLabel.setForeground(GRAY);
        header.add(headerLabel, BorderLayout.WEST);

        textArea = new JTextArea("Database output will appear here.");
        textArea.setEditable(false);
        textArea.setBackground(PANEL_DARK);
        textArea.setForeground(WHITE);
        textArea.setCaretColor(WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COL));
        scrollPane.getViewport().setBackground(PANEL_DARK);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        outputPanel.add(header,     BorderLayout.NORTH);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        return outputPanel;
    }

    private JButton sidebarButton(JButton btn, boolean primary) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Dialog", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color bg    = primary ? RED      : PANEL_MID;
        Color hover = primary ? RED_DARK : new Color(38, 36, 33);
        Color fg    = primary ? WHITE    : GRAY;

        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary ? RED : BORDER_COL, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(b.getModel().isRollover() ? hover : bg);
                g2.fillRect(0, 0, c.getWidth(), c.getHeight());
                g2.setColor(fg);
                g2.setFont(b.getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(b.getText(), 14, (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        });
        return btn;
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

        if (source == C) {
            try {
                String idStr   = styledInput("Create Car", "Enter Car ID:");    if (idStr   == null) return;
                String make    = styledInput("Create Car", "Enter Make:");      if (make    == null) return;
                String model   = styledInput("Create Car", "Enter Model:");     if (model   == null) return;
                String yearStr = styledInput("Create Car", "Enter Year:");      if (yearStr == null) return;
                String rateStr = styledInput("Create Car", "Enter Rate:");      if (rateStr == null) return;
                boolean avail  = styledConfirm("Create Car", "Is the car available?");

                PreparedStatement pst = conn.prepareStatement("INSERT INTO Car (car_id, make, model, year, rate, is_available) VALUES (?, ?, ?, ?, ?, ?)");
                pst.setInt(1, Integer.parseInt(idStr));
                pst.setString(2, make);
                pst.setString(3, model);
                pst.setInt(4, Integer.parseInt(yearStr));
                pst.setDouble(5, Double.parseDouble(rateStr));
                pst.setString(6, avail ? "Yes" : "No");
                pst.executeUpdate();
                textArea.setText("Car added successfully!");
            } catch (Exception ex) {
                textArea.setText("Error inserting car: " + ex.getMessage());
            }

        } else if (source == R) {
            String[] options = role.equals("Admin")
                ? new String[]{"Cars", "All Rental Requests"}
                : new String[]{"Cars", "My Rental Requests"};

            int choice = styledOption("Read", "What would you like to view?", options);
            if (choice < 0) return;

            if (choice == 0) {
                try {
                    String idStr = styledInput("Read Cars", "Enter Car ID or 0 for all:");
                    if (idStr == null) return;
                    int id = Integer.parseInt(idStr);
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
                                " | Available: " + rs.getString("is_available"));
                        } else {
                            textArea.setText("Car not found.");
                        }
                    }
                } catch (Exception ex) {
                    textArea.setText("Error reading cars: " + ex.getMessage());
                }

            } else if (choice == 1) {
                try {
                    StringBuilder sb = new StringBuilder();
                    if (role.equals("Admin")) {
                        String sql =
                            "SELECT rr.request_id, rr.status, rr.total_cost, rr.car_make, rr.car_model, rr.car_year, " +
                            "       c.name AS customer_name, car.make, car.model, car.year, car.rate " +
                            "FROM RentalRequest rr " +
                            "JOIN Customer c ON rr.customer_id = c.customer_id " +
                            "JOIN Car car ON rr.car_id = car.car_id " +
                            "ORDER BY rr.request_date DESC";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        ResultSet rs = pst.executeQuery();
                        sb.append("=== ALL RENTAL REQUESTS ===\n\n");
                        while (rs.next()) {
                            boolean completed = rs.getString("status").equals("Completed");
                            String displayMake  = completed ? rs.getString("car_make")  : rs.getString("make");
                            String displayModel = completed ? rs.getString("car_model") : rs.getString("model");
                            int    displayYear  = completed ? rs.getInt("car_year")     : rs.getInt("year");
                            double displayRate  = completed ? rs.getDouble("total_cost"): rs.getDouble("rate");
                            sb.append("Request ID: ").append(rs.getString("request_id"))
                              .append(" | Customer: ").append(rs.getString("customer_name"))
                              .append(" | Car: ").append(displayMake).append(" ")
                                                 .append(displayModel).append(" (")
                                                 .append(displayYear).append(")")
                              .append(" | Status: ").append(rs.getString("status"))
                              .append(" | Daily Rate: $").append(displayRate)
                              .append("\n");
                        }
                    } else {
                        String sql =
                            "SELECT rr.request_id, rr.status, rr.total_cost, rr.car_make, rr.car_model, rr.car_year, " +
                            "       car.make, car.model, car.year, car.rate " +
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
                            boolean completed = rs.getString("status").equals("Completed");
                            String displayMake  = completed ? rs.getString("car_make")  : rs.getString("make");
                            String displayModel = completed ? rs.getString("car_model") : rs.getString("model");
                            int    displayYear  = completed ? rs.getInt("car_year")     : rs.getInt("year");
                            double displayRate  = completed ? rs.getDouble("total_cost"): rs.getDouble("rate");
                            sb.append("Request ID: ").append(rs.getString("request_id"))
                              .append(" | Car: ").append(displayMake).append(" ")
                                                 .append(displayModel).append(" (")
                                                 .append(displayYear).append(")")
                              .append(" | Status: ").append(rs.getString("status"))
                              .append(" | Daily Rate: $").append(displayRate)
                              .append("\n");
                        }
                    }
                    textArea.setText(sb.length() > 30 ? sb.toString() : sb.toString() + "No requests found.");
                } catch (Exception ex) {
                    textArea.setText("Error reading requests: " + ex.getMessage());
                }
            }

        } else if (source == U) {
            String target = styledInput("Update", "What would you like to update?\nType CAR or REQUEST:");
            if (target == null) return;

            if (target.equalsIgnoreCase("car")) {
                try {
                    String idStr   = styledInput("Update Car", "Enter Car ID to update:");  if (idStr   == null) return;
                    String make    = styledInput("Update Car", "Enter new Make:");           if (make    == null) return;
                    String model   = styledInput("Update Car", "Enter new Model:");          if (model   == null) return;
                    String yearStr = styledInput("Update Car", "Enter new Year:");           if (yearStr == null) return;
                    String rateStr = styledInput("Update Car", "Enter new Rate:");           if (rateStr == null) return;
                    boolean avail  = styledConfirm("Update Car", "Is the car available?");

                    int carId = Integer.parseInt(idStr);

                    PreparedStatement cancelPst = conn.prepareStatement(
                        "UPDATE RentalRequest SET status='Cancelled' WHERE car_id=? AND status IN ('Pending', 'Approved')");
                    cancelPst.setInt(1, carId);
                    int cancelled = cancelPst.executeUpdate();

                    PreparedStatement pst = conn.prepareStatement(
                        "UPDATE Car SET make=?, model=?, year=?, rate=?, is_available=? WHERE car_id=?");
                    pst.setString(1, make);
                    pst.setString(2, model);
                    pst.setInt(3, Integer.parseInt(yearStr));
                    pst.setDouble(4, Double.parseDouble(rateStr));
                    pst.setString(5, avail ? "Yes" : "No");
                    pst.setInt(6, carId);
                    int rows = pst.executeUpdate();

                    if (rows > 0) {
                        String msg = "Car updated successfully.";
                        if (cancelled > 0) msg += "\n" + cancelled + " rental request(s) cancelled due to car update.";
                        textArea.setText(msg);
                    } else {
                        textArea.setText("Car not found.");
                    }
                } catch (Exception ex) {
                    textArea.setText("Error updating car: " + ex.getMessage());
                }

            } else if (target.equalsIgnoreCase("request")) {
                try {
                    String requestId = styledInput("Update Request", "Enter Request ID to update:");
                    if (requestId == null) return;

                    PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT request_id, car_id FROM RentalRequest WHERE request_id=?");
                    checkStmt.setString(1, requestId);
                    ResultSet checkRS = checkStmt.executeQuery();
                    if (!checkRS.next()) {
                        textArea.setText("Request ID \"" + requestId + "\" not found. Please check and try again.");
                        return;
                    }
                    int carId = checkRS.getInt("car_id");

                    String newStatus = styledDropdown("Update Request",
                        "Select new status for Request " + requestId + ":",
                        new String[]{"Pending", "Approved", "Rejected", "Completed"});
                    if (newStatus == null) return;

                    PreparedStatement pst = conn.prepareStatement(
                        "UPDATE RentalRequest SET status=? WHERE request_id=?");
                    pst.setString(1, newStatus);
                    pst.setString(2, requestId);
                    int rows = pst.executeUpdate();

                    if (rows > 0 && newStatus.equals("Rejected")) {
                        PreparedStatement carPst = conn.prepareStatement(
                            "UPDATE Car SET is_available='Yes' WHERE car_id=?");
                        carPst.setInt(1, carId);
                        carPst.executeUpdate();
                    }

                    if (rows > 0 && newStatus.equals("Approved")) {
                        PreparedStatement carPst = conn.prepareStatement(
                            "UPDATE Car SET is_available='No' WHERE car_id=?");
                        carPst.setInt(1, carId);
                        carPst.executeUpdate();
                    }

                    textArea.setText(rows > 0
                        ? "Request " + requestId + " status updated to: " + newStatus
                        : "Request ID \"" + requestId + "\" not found.");
                } catch (Exception ex) {
                    textArea.setText("Error updating request: " + ex.getMessage());
                }

            } else {
                textArea.setText("Invalid input. Please type CAR or REQUEST.");
            }

        } else if (source == D) {
            try {
                String idStr = styledInput("Delete Car", "Enter Car ID to delete:");
                if (idStr == null) return;
                PreparedStatement pst = conn.prepareStatement("DELETE FROM Car WHERE car_id=?");
                pst.setInt(1, Integer.parseInt(idStr));
                int rows = pst.executeUpdate();
                textArea.setText(rows > 0 ? "Car deleted successfully." : "Car not found.");
            } catch (Exception ex) {
                textArea.setText("Error deleting car: " + ex.getMessage());
            }

        } else if (source == Rent) {
            try {
                PreparedStatement idStmt = conn.prepareStatement(
                    "SELECT c.customer_id FROM Customer c JOIN User u ON c.login_id = u.login_id WHERE u.username=?");
                idStmt.setString(1, username);
                ResultSet idRS = idStmt.executeQuery();
                if (!idRS.next()) { textArea.setText("Customer account not found."); return; }
                String customerId = idRS.getString("customer_id");

                PreparedStatement payStmt = conn.prepareStatement(
                    "SELECT payment_id FROM Payment WHERE customerId=?");
                payStmt.setString(1, customerId);
                if (!payStmt.executeQuery().next()) {
                    textArea.setText("You must add a payment method before renting.\nUse the Payment button to add one.");
                    return;
                }

                String action = styledInput("Rent", "Type RENT or UNRENT:");
                if (action == null) return;

                if (action.equalsIgnoreCase("rent")) {
                    String carIdStr = styledInput("Rent", "Enter Car ID:");
                    if (carIdStr == null) return;
                    int carId = Integer.parseInt(carIdStr);

                    PreparedStatement carStmt = conn.prepareStatement("SELECT * FROM Car WHERE car_id=?");
                    carStmt.setInt(1, carId);
                    ResultSet carRS = carStmt.executeQuery();

                    if (carRS.next()) {
                        if (carRS.getString("is_available").equals("No")) {
                            textArea.setText("Car is not available."); return;
                        }
                        PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) FROM RentalRequest");
                        ResultSet countRS = countStmt.executeQuery(); countRS.next();
                        String requestId = String.format("REQ%03d", countRS.getInt(1) + 1);

                        PreparedStatement reqStmt = conn.prepareStatement(
                            "INSERT INTO RentalRequest (request_id, customer_id, car_id, status, total_cost, car_make, car_model, car_year) VALUES (?, ?, ?, 'Pending', ?, ?, ?, ?)");
                        reqStmt.setString(1, requestId);
                        reqStmt.setString(2, customerId);
                        reqStmt.setInt(3, carId);
                        reqStmt.setDouble(4, carRS.getDouble("rate"));
                        reqStmt.setString(5, carRS.getString("make"));
                        reqStmt.setString(6, carRS.getString("model"));
                        reqStmt.setInt(7, carRS.getInt("year"));
                        reqStmt.executeUpdate();

                        PreparedStatement updStmt = conn.prepareStatement(
                            "UPDATE Car SET is_available='No' WHERE car_id=?");
                        updStmt.setInt(1, carId);
                        updStmt.executeUpdate();
                        textArea.setText("Rental request created. Request ID: " + requestId);
                    } else {
                        textArea.setText("Car not found.");
                    }

                } else if (action.equalsIgnoreCase("unrent")) {
                    String requestId = styledInput("Unrent", "Enter Rental Request ID:");
                    if (requestId == null) return;

                    PreparedStatement reqStmt = conn.prepareStatement(
                        "SELECT status, car_id FROM RentalRequest WHERE request_id=?");
                    reqStmt.setString(1, requestId);
                    ResultSet rs = reqStmt.executeQuery();

                    if (rs.next()) {
                        String status = rs.getString("status");
                        int carId     = rs.getInt("car_id");
                        if (status.equals("Pending")) {
                            PreparedStatement pst = conn.prepareStatement(
                                "UPDATE RentalRequest SET status='Cancelled' WHERE request_id=?");
                            pst.setString(1, requestId); pst.executeUpdate();
                            PreparedStatement carStmt = conn.prepareStatement(
                                "UPDATE Car SET is_available='Yes' WHERE car_id=?");
                            carStmt.setInt(1, carId); carStmt.executeUpdate();
                            textArea.setText("Rental request cancelled. Car is now available.");
                        } else if (status.equals("Approved")) {
                            PreparedStatement pst = conn.prepareStatement(
                                "UPDATE RentalRequest SET status='Completed' WHERE request_id=?");
                            pst.setString(1, requestId); pst.executeUpdate();
                            PreparedStatement carStmt = conn.prepareStatement(
                                "UPDATE Car SET is_available='Yes' WHERE car_id=?");
                            carStmt.setInt(1, carId); carStmt.executeUpdate();
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

        } else if (source == Payment) {
            try {
                PreparedStatement idStmt = conn.prepareStatement(
                    "SELECT c.customer_id FROM Customer c JOIN User u ON c.login_id = u.login_id WHERE u.username=?");
                idStmt.setString(1, username);
                ResultSet idRS = idStmt.executeQuery();
                if (!idRS.next()) { textArea.setText("Customer account not found."); return; }
                String customerId = idRS.getString("customer_id");

                String cardNum  = styledInput("Payment", "Enter Card Number:");  if (cardNum  == null) return;
                String cardName = styledInput("Payment", "Enter Card Name:");    if (cardName == null) return;
                String cvv      = styledInput("Payment", "Enter CVV:");          if (cvv      == null) return;

                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM Payment WHERE customerId=?");
                checkStmt.setString(1, customerId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    PreparedStatement pst = conn.prepareStatement(
                        "UPDATE Payment SET cardNum=?, cardName=?, cvv=? WHERE customerId=?");
                    pst.setString(1, cardNum); pst.setString(2, cardName);
                    pst.setString(3, cvv);     pst.setString(4, customerId);
                    pst.executeUpdate();
                    textArea.setText("Payment information updated.");
                } else {
                    PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO Payment (customerId, cardNum, cardName, cvv) VALUES (?, ?, ?, ?)");
                    pst.setString(1, customerId); pst.setString(2, cardNum);
                    pst.setString(3, cardName);   pst.setString(4, cvv);
                    pst.executeUpdate();
                    textArea.setText("Payment method added.");
                }
            } catch (Exception ex) {
                textArea.setText("Error processing payment: " + ex.getMessage());
            }

        } else if (source == Logout) {
            if (styledConfirm("Logout", "Are you sure you want to logout?")) {
                new login().setVisible(true);
                dispose();
            }
        }
    }


    /** Single-field text input dialog. Returns null if cancelled. */
    private String styledInput(String title, String prompt) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(360, 195);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL_DARK);
        root.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        JPanel strip = new JPanel();
        strip.setBackground(RED);
        strip.setPreferredSize(new Dimension(0, 4));
        root.add(strip, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(PANEL_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel lTitle = new JLabel(title.toUpperCase());
        lTitle.setFont(new Font("Dialog", Font.BOLD, 10));
        lTitle.setForeground(RED);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lPrompt = new JLabel("<html>" + prompt.replace("\n", "<br>") + "</html>");
        lPrompt.setFont(new Font("Dialog", Font.PLAIN, 13));
        lPrompt.setForeground(WHITE);
        lPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setBackground(PANEL_MID);
        field.setForeground(WHITE);
        field.setCaretColor(WHITE);
        field.setFont(new Font("Dialog", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(lPrompt);
        content.add(Box.createVerticalStrut(10));
        content.add(field);
        content.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        String[] result = {null};
        JButton ok     = makeDialogBtn("OK",     RED,      RED_DARK,              WHITE);
        JButton cancel = makeDialogBtn("Cancel", PANEL_MID, new Color(38,36,33), GRAY);
        ok.addActionListener(ev -> { result[0] = field.getText().trim(); dialog.dispose(); });
        cancel.addActionListener(ev -> dialog.dispose());
        field.addActionListener(ev -> { result[0] = field.getText().trim(); dialog.dispose(); });

        btnRow.add(ok); btnRow.add(cancel);
        content.add(btnRow);
        root.add(content, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setVisible(true);
        return result[0];
    }

    /** Yes/No confirm dialog. Returns true for Yes. */
    private boolean styledConfirm(String title, String prompt) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(340, 165);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL_DARK);
        root.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        JPanel strip = new JPanel();
        strip.setBackground(RED);
        strip.setPreferredSize(new Dimension(0, 4));
        root.add(strip, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(PANEL_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel lTitle = new JLabel(title.toUpperCase());
        lTitle.setFont(new Font("Dialog", Font.BOLD, 10));
        lTitle.setForeground(RED);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lPrompt = new JLabel("<html>" + prompt + "</html>");
        lPrompt.setFont(new Font("Dialog", Font.PLAIN, 13));
        lPrompt.setForeground(WHITE);
        lPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(lPrompt);
        content.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        boolean[] result = {false};
        JButton yes = makeDialogBtn("Yes", RED,      RED_DARK,              WHITE);
        JButton no  = makeDialogBtn("No",  PANEL_MID, new Color(38,36,33), GRAY);
        yes.addActionListener(ev -> { result[0] = true; dialog.dispose(); });
        no.addActionListener(ev  -> dialog.dispose());

        btnRow.add(yes); btnRow.add(no);
        content.add(btnRow);
        root.add(content, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setVisible(true);
        return result[0];
    }

    /** Option chooser (multiple buttons). Returns chosen index or -1 if closed. */
    private int styledOption(String title, String prompt, String[] options) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(340, 165);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL_DARK);
        root.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        JPanel strip = new JPanel();
        strip.setBackground(RED);
        strip.setPreferredSize(new Dimension(0, 4));
        root.add(strip, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(PANEL_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel lTitle = new JLabel(title.toUpperCase());
        lTitle.setFont(new Font("Dialog", Font.BOLD, 10));
        lTitle.setForeground(RED);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lPrompt = new JLabel("<html>" + prompt + "</html>");
        lPrompt.setFont(new Font("Dialog", Font.PLAIN, 13));
        lPrompt.setForeground(WHITE);
        lPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(lPrompt);
        content.add(Box.createVerticalStrut(14));

        int[] result = {-1};
        JPanel btnRow = new JPanel(new GridLayout(1, options.length, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            JButton btn = makeDialogBtn(options[i],
                i == 0 ? RED      : PANEL_MID,
                i == 0 ? RED_DARK : new Color(38,36,33),
                i == 0 ? WHITE    : GRAY);
            btn.addActionListener(ev -> { result[0] = idx; dialog.dispose(); });
            btnRow.add(btn);
        }

        content.add(btnRow);
        root.add(content, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setVisible(true);
        return result[0];
    }

    /** Dropdown (JComboBox) selection dialog. Returns selected string or null. */
    private String styledDropdown(String title, String prompt, String[] options) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(360, 205);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL_DARK);
        root.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        JPanel strip = new JPanel();
        strip.setBackground(RED);
        strip.setPreferredSize(new Dimension(0, 4));
        root.add(strip, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(PANEL_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel lTitle = new JLabel(title.toUpperCase());
        lTitle.setFont(new Font("Dialog", Font.BOLD, 10));
        lTitle.setForeground(RED);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lPrompt = new JLabel("<html>" + prompt + "</html>");
        lPrompt.setFont(new Font("Dialog", Font.PLAIN, 13));
        lPrompt.setForeground(WHITE);
        lPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> combo = new JComboBox<>(options);
        combo.setBackground(PANEL_MID);
        combo.setForeground(WHITE);
        combo.setFont(new Font("Dialog", Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(lPrompt);
        content.add(Box.createVerticalStrut(10));
        content.add(combo);
        content.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        String[] result = {null};
        JButton ok     = makeDialogBtn("OK",     RED,      RED_DARK,              WHITE);
        JButton cancel = makeDialogBtn("Cancel", PANEL_MID, new Color(38,36,33), GRAY);
        ok.addActionListener(ev -> { result[0] = (String) combo.getSelectedItem(); dialog.dispose(); });
        cancel.addActionListener(ev -> dialog.dispose());

        btnRow.add(ok); btnRow.add(cancel);
        content.add(btnRow);
        root.add(content, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setVisible(true);
        return result[0];
    }

    /** Small button used inside all dialogs. */
    private JButton makeDialogBtn(String text, Color bg, Color hoverBg, Color fg) {
        JButton b = new JButton(text) {
            boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent ev) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent ev) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(hov ? hoverBg : bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.setBorder(BorderFactory.createLineBorder(bg.equals(PANEL_MID) ? BORDER_COL : bg, 1));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(0, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static void main(String[] args) {}
}
