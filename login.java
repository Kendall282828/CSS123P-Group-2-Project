package pckExer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class login extends JFrame{
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	static String query;
	
	String[][] mockUsers = {
		    {"admin", "admin123", "Admin"},
		    {"customer", "password123", "Customer"}
		    
		    //MOCK USER DB for if mySQL fails (format {username, password, user type})
		};
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login window = new login();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public static void dbConnect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/car_rental_system","root","root");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}
		catch(Exception e) {
			e.printStackTrace();  
		}
	}

	public login() {
	    initialize();
	    //dbConnect() || removing this for now bc SQL cannot be tested;

        }
        
	private void initialize() {
		setBounds(100,100,389,290);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Username:");
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		lblNewLabel.setBounds(78,79,100,17);
		getContentPane().add(lblNewLabel);
		
		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtUsername.setColumns(10);
		txtUsername.setBounds(167,67,126,29);
		getContentPane().add(txtUsername);
		
		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtPassword.setBounds(167,98,126,29);
		getContentPane().add(txtPassword);
		
		JLabel lblNewLabel_1 = new JLabel("Password:");
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(78,113,100,17);
		getContentPane().add(lblNewLabel_1);
		
		JButton btnClear = new JButton("CLEAR");
		btnClear.setBounds(188,169,105,41);
		getContentPane().add(btnClear);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtUsername.setText("");
				txtPassword.setText("");
			}
		});
		
		JButton btnSubmit = new JButton("LOGIN");
		btnSubmit.setBounds(73,169,105,41);
		getContentPane().add(btnSubmit);
		btnSubmit.addActionListener(new ActionListener() {
		    @SuppressWarnings("deprecation")
		    public void actionPerformed(ActionEvent e) {

		    	String username = txtUsername.getText().trim();
		    	String password = txtPassword.getText().trim();

		        try {
		            // ==============================
		            // DATABASE LOGIN ATTEMPT
		            // ==============================
		            query = "SELECT user_type FROM User WHERE username='"
		                    + username + "' AND password='" + password + "'";

		            rs = stmt.executeQuery(query);

		            if (rs.next()) {
		                String role = rs.getString("user_type");

		                JOptionPane.showMessageDialog(
		                        null, "Login Complete (" + role + ")");

		                new Exer5_GUI_CRUD();
		                dispose();
		                return;
		            }

		            JOptionPane.showMessageDialog(
		                    null, "Username and/or password does not match");

		        } catch (Exception ex) {

		            // ==============================
		            // FALLBACK LOGIN (MOCK MODE)
		            // ==============================
		            boolean matched = false;

		            for (String[] user : mockUsers) {
		                if (user[0].equals(username)
		                        && user[1].equals(password)) {

		                    matched = true;
		                    String role = user[2];

		                    JOptionPane.showMessageDialog(
		                            null,
		                            "Login Complete (" + role + ")\n[Mock Mode]");

		                    new Exer5_GUI_CRUD();
		                    dispose();
		                    break;
		                }
		            }

		            if (!matched) {
		                JOptionPane.showMessageDialog(
		                        null,
		                        "Invalid login [Mock Mode]");
		            }
		        }
		    }
		});

	}

}
