package pckExer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    static Connection conn;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                login window = new login();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/car_rental_agency", "root", "root"
            );
        } catch (Exception e) {
            System.err.println("DB connection failed: " + e.getMessage());
            conn = null; // explicitly null so we know to use mock mode
        }
    }

    public login() {
        dbConnect(); // always attempt DB connection on startup
        initialize();
    }

    private void initialize() {
        setBounds(100, 100, 389, 290);
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Dialog", Font.PLAIN, 14));
        lblUsername.setBounds(78, 79, 100, 17);
        getContentPane().add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Dialog", Font.PLAIN, 14));
        txtUsername.setColumns(10);
        txtUsername.setBounds(167, 67, 126, 29);
        getContentPane().add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Dialog", Font.PLAIN, 14));
        lblPassword.setBounds(78, 110, 100, 17); // fixed: aligned with password field
        getContentPane().add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Dialog", Font.PLAIN, 14));
        txtPassword.setBounds(167, 98, 126, 29);
        getContentPane().add(txtPassword);

        JButton btnClear = new JButton("CLEAR");
        btnClear.setBounds(188, 169, 105, 41);
        getContentPane().add(btnClear);
        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
        });

        JButton btnSubmit = new JButton("LOGIN"); btnSubmit.setBounds(73, 169, 105, 41);
        getContentPane().add(btnSubmit);
        btnSubmit.addActionListener(e -> handleLogin());
        // Key binding: press Enter to trigger the button
        btnSubmit.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW) 
                 .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressLogin");
        btnSubmit.getActionMap().put("pressLogin", new AbstractAction() {
        	@Override 
        	public void actionPerformed(ActionEvent e) {
        		btnSubmit.doClick();
        		} 
        	});
        }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (conn == null) {
            JOptionPane.showMessageDialog(null, "No database connection.");
            return;
        }

        try {
            String sql = "SELECT user_type FROM User WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("user_type");
                JOptionPane.showMessageDialog(null, "Login Complete (" + role + ")");
                new Exer5_GUI_CRUD();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Username and/or password does not match.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Login error: " + ex.getMessage());
        }
    }
}

