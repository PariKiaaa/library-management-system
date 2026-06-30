package src.ui;

import src.exception.AuthenticationException;
import src.model.Student;
import src.service.AuthenticationService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Login window of the library management system.
 */
public class LoginFrame extends JFrame {

    // Login form fields
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    // Role selection radio buttons
    private final JRadioButton studentRadio;
    private final JRadioButton librarianRadio;

    // Action buttons
    private final JButton loginButton;
    private final JButton registerButton;

    // Service
    private final AuthenticationService authenticationService;

    // UI Colors
    private final Color PRIMARY_COLOR = new Color(255, 107, 107);
    // private final Color SECONDARY_COLOR = new Color(78, 205, 196);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(237, 122, 109);
    // private final Color BUTTON_COLOR = new Color(52, 152, 219);
    // private final Color TEXT_COLOR = Color.BLACK;

    // Constructor - Initialize login UI
    public LoginFrame() {
        authenticationService = new AuthenticationService();

        setTitle("ورود به حساب کاربری");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Apply Nimbus look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", PRIMARY_COLOR);
            UIManager.put("control", BACKGROUND_COLOR);
            
            Font defaultFont = new Font("Tahoma", Font.PLAIN, 16);
            // Font boldFont = new Font("Tahoma", Font.BOLD, 16);
            
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("PasswordField.font", defaultFont);
            UIManager.put("RadioButton.font", defaultFont);
            UIManager.put("OptionPane.font", defaultFont);
            
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("RadioButton.foreground", Color.BLACK);

        } catch (Exception e) {
            // Fallback to default
        }

        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JLabel headerLabel = new JLabel("خوش آمدید!");
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with form fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        centerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        // Create form labels
        JLabel usernameLabel = new JLabel("شناسه کاربری:");
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        usernameLabel.setForeground(Color.BLACK);
        JLabel passwordLabel = new JLabel("رمز عبور:");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        passwordLabel.setForeground(Color.BLACK);

        // Create form fields
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        usernameField.setForeground(Color.BLACK);
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Role selection radio buttons
        studentRadio = new JRadioButton("دانشجو", true);
        studentRadio.setFont(new Font("Tahoma", Font.PLAIN, 16));
        studentRadio.setForeground(Color.BLACK);
        studentRadio.setBackground(BACKGROUND_COLOR);
        studentRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        librarianRadio = new JRadioButton("کتابدار");
        librarianRadio.setFont(new Font("Tahoma", Font.PLAIN, 16));
        librarianRadio.setForeground(Color.BLACK);
        librarianRadio.setBackground(BACKGROUND_COLOR);
        librarianRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Group radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio);
        group.add(librarianRadio);

        // Role panel containing radio buttons
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rolePanel.setBackground(BACKGROUND_COLOR);
        rolePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rolePanel.add(studentRadio);
        rolePanel.add(librarianRadio);

        // Add components to form using grid bag layout
        // Username row
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(usernameField, gbc);

        // Password row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(passwordField, gbc);

        // Role row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel roleLabel = new JLabel("نقش:");
        roleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        roleLabel.setForeground(Color.BLACK);
        centerPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(rolePanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(HEADER_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        loginButton = createStyledButton("ورود", new Color(46, 204, 113));
        registerButton = createStyledButton("ثبت‌نام", new Color(52, 152, 219));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    // Create styled button with hover effect
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            new EmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    // Handle user login
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            // Login as Student
            if (studentRadio.isSelected()) {
                Student student = authenticationService.loginStudent(username, password);
                new StudentPanel(student).setVisible(true);
            } else {
                // Login as Librarian
                authenticationService.loginLibrarian(username, password);
                new LibrarianPanel().setVisible(true);
            }

            // Close login window on successful login
            dispose();

        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "ورود ناموفق",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}