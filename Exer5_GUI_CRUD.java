package pckExer;

import javax.swing.*;
import javax.swing.event.*;    
import java.awt.event.*;
import java.awt.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Exer5_GUI_CRUD extends JFrame implements ActionListener {

	//components that are seen by all methods
	JButton C = new JButton("Create");
	JButton R = new JButton("Read");
	JButton U = new JButton("Update");
	JButton D = new JButton("Delete");
	JTextArea textArea;
	
	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	static String query;
	
	public static void dbConnect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Practice Exercise1","root","root");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}
		catch(Exception e) {
			e.printStackTrace();  
		}
	}
	
	//The blueprint of a CRUD GUI
	public Exer5_GUI_CRUD() {
		//Set properties
		setTitle("CRUD");
		setSize(480,340);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		//add listener to buttons
		C.addActionListener(this);
		R.addActionListener(this);
		U.addActionListener(this);
		D.addActionListener(this);
		
		//create a left and right panel then add it to top panel
		//add the components to the left and right panel
		JPanel left = new JPanel();
		left.add(C);
		JPanel right = new JPanel();
		right.add(R);
		right.add(U);
		right.add(D);
		JPanel top = new JPanel();
		top.add(left, BorderLayout.WEST);
		top.add(right, BorderLayout.EAST);
		
		//set text area properties
		textArea = new JTextArea("Blank", 15, 40);
		textArea.setEditable(false);
		
		//add text area to bottom panel
		JPanel bottom = new JPanel();
		bottom.add(new JScrollPane(textArea));
		
		//add top and bottom panels to the frame
		add(top, BorderLayout.NORTH);
		add(bottom, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	//listens for when any of the buttons are pressed then acts accordingly to the source of the press
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		String userItem = "";
	
		if (source == C) {
			userItem = JOptionPane.showInputDialog("Please enter item to create");
		}
		else if (source == R) {
			userItem = JOptionPane.showInputDialog("Please enter item to read");
			if(userItem == "") {
				
			}
		}
		else if (source == U) {
			userItem = JOptionPane.showInputDialog("Please enter item to update");
		}
		else if (source == D) {
			userItem = JOptionPane.showInputDialog("Please enter item to delete");
		}
		
		textArea.setText(userItem);
	}
	
	public static void main(String[] args) {
		new Exer5_GUI_CRUD();	//Create new CRUD object
	}
}
