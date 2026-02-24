package Exer6;
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
	
	public static Connection dbConnect() throws SQLException {
	    String url = "jdbc:mysql://localhost:3306/car_rental_agency";  //fix this later (fixed (?))
	    String user = "root";
	    String pass = "root";
	    return DriverManager.getConnection(url, user, pass);
	}


	public login() {
	    initialize();
	    try {
	        conn = dbConnect(); // assign the connection
	        stmt = conn.createStatement();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
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
				try {
					query = "select * from LOGIN where password ='" + txtPassword.getText() + "'" + "and username='" + txtUsername.getText() +"'";
					rs = stmt.executeQuery(query);
					if(rs.next()) {
						JOptionPane.showMessageDialog(null, "Login Complete");
						
					}
					else {
						JOptionPane.showConfirmDialog(null, "username and/or password does not match...");
					}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
			});
	}

}
