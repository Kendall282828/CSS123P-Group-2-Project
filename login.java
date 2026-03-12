package pckExer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Login JFrame for Car Rental Agency
 * Handles GUI for user login, connects to MySQL DB, 
 * and opens main GUI after successful authentication.
 */
public class login extends JFrame {

    static final Color BLACK      = new Color(10, 10, 10);
    static final Color PANEL_DARK = new Color(20, 18, 16);
    static final Color RED        = new Color(200, 57, 43);
    static final Color RED_DARK   = new Color(160, 45, 34);
    static final Color WHITE      = new Color(245, 240, 232);
    static final Color GRAY       = new Color(138, 130, 120);
    static final Color BORDER_COL = new Color(46, 43, 39);
    static final Color INPUT_BG   = new Color(28, 26, 23);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;

    // Database connection
    static  Connection     conn;

    // ====== MAIN METHOD ======
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            // Use cross-platform look and feel
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new login().setVisible(true); // Open login window
        });
    }

    // ====== DATABASE CONNECTION ======
    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/car_rental_agency", "root", "Apr@2024102110");
        } catch (Exception e) {
            System.err.println("DB connection failed: " + e.getMessage());
            conn = null; // Null if failed
        }
    }

     // ====== CONSTRUCTOR ======
    public login() {
        dbConnect(); // Connect to database
        setTitle("Car Rental Agency — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(780, 460);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BLACK);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildLeft(),  BorderLayout.WEST);
        getContentPane().add(buildRight(), BorderLayout.CENTER);
    }

    // ====== LEFT PANEL (INFO + DESIGN) ======
    private JPanel buildLeft() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillOval(getWidth() - 80, getHeight() - 80, 180, 180);
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, -60, 160, 160);
                g2.dispose();
            }
        };
        p.setBackground(RED);
        p.setPreferredSize(new Dimension(280, 460));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(44, 36, 36, 36));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("🚗");
        icon.setFont(new Font("Dialog", Font.PLAIN, 36));
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("<html>Car Rental<br>Agency</html>");
        title.setFont(new Font("Dialog", Font.BOLD, 38));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("FLEET MANAGEMENT SYSTEM");
        sub.setFont(new Font("Dialog", Font.PLAIN, 10));
        sub.setForeground(new Color(255, 255, 255, 150));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(icon);
        top.add(Box.createVerticalStrut(12));
        top.add(title);
        top.add(Box.createVerticalStrut(8));
        top.add(sub);

        JLabel footer = new JLabel("© 2024 Car Rental Agency");
        footer.setFont(new Font("Dialog", Font.PLAIN, 10));
        footer.setForeground(new Color(255, 255, 255, 80));

        p.add(top,    BorderLayout.NORTH);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    // ====== RIGHT PANEL (LOGIN FORM) ======
    private JPanel buildRight() {
        JPanel p = new JPanel();
        p.setBackground(PANEL_DARK);
        p.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 1, BORDER_COL),
            new EmptyBorder(50, 50, 50, 50)
        ));
        p.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;

        // Heading
        JLabel heading = new JLabel("WELCOME BACK");
        heading.setFont(new Font("Dialog", Font.BOLD, 26));
        heading.setForeground(WHITE);
        g.gridy = 0; g.insets = new Insets(0, 0, 0, 0);
        p.add(heading, g);

        JLabel sub = new JLabel("Sign in to your account to continue");
        sub.setFont(new Font("Dialog", Font.PLAIN, 12));
        sub.setForeground(GRAY);
        g.gridy = 1; g.insets = new Insets(4, 0, 28, 0);
        p.add(sub, g);

        // Username field
        g.gridy = 2; g.insets = new Insets(0, 0, 4, 0);
        p.add(fieldLabel("USERNAME"), g);
        txtUsername = styledTextField();
        g.gridy = 3; g.insets = new Insets(0, 0, 18, 0);
        p.add(txtUsername, g);

        // Password field
        g.gridy = 4; g.insets = new Insets(0, 0, 4, 0);
        p.add(fieldLabel("PASSWORD"), g);
        txtPassword = styledPasswordField();
        g.gridy = 5; g.insets = new Insets(0, 0, 28, 0);
        p.add(txtPassword, g);

        // Buttons row
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        JButton btnLogin = styledButton("LOGIN", RED,        RED_DARK, WHITE);
        JButton btnClear = styledButton("CLEAR", PANEL_DARK, INPUT_BG, GRAY);
        btnRow.add(btnLogin);
        btnRow.add(btnClear);
        g.gridy = 6; g.insets = new Insets(0, 0, 0, 0);
        p.add(btnRow, g);

        // ====== EVENT HANDLERS ======
        btnLogin.addActionListener(e -> handleLogin());
        btnClear.addActionListener(e -> { txtUsername.setText(""); txtPassword.setText(""); });
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> handleLogin());

        return p;
    }

    // ====== HANDLE LOGIN ======
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (conn == null) {
            styledMessage("Error", "No database connection.");
            return;
        }
        try {
            PreparedStatement pst = conn.prepareStatement(
                "SELECT user_type FROM User WHERE username=? AND password=?");
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Successful login
                String role = rs.getString("user_type");
                styledMessage("Success", "Login complete — Welcome, " + username + "!");
                new GUI(role, username); // Open main GUI
                dispose(); // Close login
            } else {
                styledMessage("Login Failed", "Username and/or password does not match.");
            }
        } catch (Exception ex) {
            styledMessage("Error", "Login error: " + ex.getMessage());
        }
    }

    // ====== HELPER METHODS ======

    // Label for input fields
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(GRAY);
        return l;
    }

    // Styled text input
    private JTextField styledTextField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    // Styled password input
    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField();
        styleInput(f);
        return f;
    }

    // Apply consistent styling to input fields
    private void styleInput(JComponent f) {
        if (f instanceof JTextField) {
            ((JTextField) f).setBackground(INPUT_BG);
            ((JTextField) f).setForeground(WHITE);
            ((JTextField) f).setCaretColor(WHITE);
            ((JTextField) f).setFont(new Font("Dialog", Font.PLAIN, 14));
        }

        // Normal and focused border
        Border normal = new CompoundBorder(
            new LineBorder(BORDER_COL, 1), new EmptyBorder(10, 12, 10, 12));
        Border focused = new CompoundBorder(
            new LineBorder(RED, 1), new EmptyBorder(10, 12, 10, 12));
        f.setBorder(normal);
        f.setPreferredSize(new Dimension(0, 42));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(focused); }
            public void focusLost (FocusEvent e) { f.setBorder(normal);  }
        });
    }

    // Styled button with hover effect
    private JButton styledButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton b = new JButton(text) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hoverBg : bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(new Font("Dialog", Font.BOLD, 13));
        b.setBorder(new LineBorder(bg.equals(PANEL_DARK) ? BORDER_COL : bg, 1));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(0, 44));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Custom styled message dialog
    private void styledMessage(String title, String message) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(340, 160);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL_DARK);
        root.setBorder(new LineBorder(BORDER_COL, 1));

        // Top colored strip
        JPanel strip = new JPanel();
        strip.setBackground(RED);
        strip.setPreferredSize(new Dimension(0, 4));
        root.add(strip, BorderLayout.NORTH);

        // Content panel with message
        JPanel content = new JPanel();
        content.setBackground(PANEL_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel lTitle = new JLabel(title.toUpperCase());
        lTitle.setFont(new Font("Dialog", Font.BOLD, 10));
        lTitle.setForeground(RED);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lMsg = new JLabel("<html>" + message + "</html>");
        lMsg.setFont(new Font("Dialog", Font.PLAIN, 13));
        lMsg.setForeground(WHITE);
        lMsg.setAlignmentX(Component.LEFT_ALIGNMENT);

        // OK button
        JButton ok = new JButton("OK") {
            boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(hov ? RED_DARK : RED);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        ok.setFont(new Font("Dialog", Font.BOLD, 12));
        ok.setBorder(new LineBorder(RED, 1));
        ok.setFocusPainted(false);
        ok.setContentAreaFilled(false);
        ok.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        ok.setAlignmentX(Component.LEFT_ALIGNMENT);
        ok.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ok.addActionListener(e -> dialog.dispose());

        content.add(lTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(lMsg);
        content.add(Box.createVerticalStrut(16));
        content.add(ok);

        root.add(content, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setVisible(true);
    }
}

