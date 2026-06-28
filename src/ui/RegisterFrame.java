package src.ui;

import src.exception.DuplicateUserException;
import src.exception.ValidationException;
import src.model.Librarian;
import src.model.Student;
import src.service.AuthenticationService;
import src.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Registration window for students and librarians.
 */
public class RegisterFrame extends JFrame {

    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField emailField;
    private final JTextField idField;
    private final JPasswordField passwordField;

    private final JRadioButton studentRadio;
    private final JRadioButton librarianRadio;

    private final JButton registerButton;
    private final JButton backButton;

    private final JLabel idLabel;

    private final AuthenticationService authenticationService;

    // Colors
    private final Color PRIMARY_COLOR = new Color(255, 107, 107);
    // private final Color SECONDARY_COLOR = new Color(78, 205, 196);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(237, 180, 109);
    // private final Color BUTTON_COLOR = new Color(52, 152, 219);
    // private final Color TEXT_COLOR = Color.BLACK;

    public RegisterFrame() {

        authenticationService = new AuthenticationService();

        setTitle("ثبت‌نام در سیستم مدیریت کتابخانه");
        setSize(550, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

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
        
        JLabel headerLabel = new JLabel("ثبت‌نام در سیستم");
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        firstNameField = new JTextField(15);
        firstNameField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        firstNameField.setForeground(Color.BLACK);
        firstNameField.setBackground(Color.WHITE);
        firstNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        lastNameField = new JTextField(15);
        lastNameField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lastNameField.setForeground(Color.BLACK);
        lastNameField.setBackground(Color.WHITE);
        lastNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        emailField = new JTextField(15);
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        emailField.setForeground(Color.BLACK);
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
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
        
        idField = new JTextField(15);
        idField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        idField.setForeground(Color.BLACK);
        idField.setBackground(Color.WHITE);
        idField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        studentRadio = new JRadioButton("دانشجو", true);
        studentRadio.setFont(new Font("Tahoma", Font.PLAIN, 16));
        studentRadio.setForeground(Color.BLACK);
        studentRadio.setOpaque(false);
        studentRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        librarianRadio = new JRadioButton("کتابدار");
        librarianRadio.setFont(new Font("Tahoma", Font.PLAIN, 16));
        librarianRadio.setForeground(Color.BLACK);
        librarianRadio.setOpaque(false);
        librarianRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio);
        group.add(librarianRadio);

        idLabel = new JLabel("شماره دانشجویی:");
        idLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        idLabel.setForeground(Color.BLACK);

        studentRadio.addActionListener(e -> idLabel.setText("شماره دانشجویی:"));
        librarianRadio.addActionListener(e -> idLabel.setText("کد پرسنلی:"));

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rolePanel.setOpaque(false);
        rolePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rolePanel.add(studentRadio);
        rolePanel.add(librarianRadio);

        int row = 0;

        JLabel firstNameLabel = new JLabel("نام:");
        firstNameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        firstNameLabel.setForeground(Color.BLACK);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(firstNameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lastNameLabel = new JLabel("نام خانوادگی:");
        lastNameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        lastNameLabel.setForeground(Color.BLACK);
        formPanel.add(lastNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(lastNameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel emailLabel = new JLabel("ایمیل:");
        emailLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        emailLabel.setForeground(Color.BLACK);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("رمز عبور:");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        passwordLabel.setForeground(Color.BLACK);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(idField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel roleLabel = new JLabel("نقش:");
        roleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        roleLabel.setForeground(Color.BLACK);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(rolePanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(HEADER_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        registerButton = createStyledButton("ثبت‌نام", new Color(46, 204, 113));
        backButton = createStyledButton("بازگشت", new Color(231, 76, 60));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> register());

        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

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

    private void register() {

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String id = idField.getText().trim();

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || id.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً تمام فیلدها را پر کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(
                    this,
                    "ایمیل نامعتبر است.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            JOptionPane.showMessageDialog(
                    this,
                    "رمز عبور باید شامل حروف بزرگ، کوچک، عدد و کاراکتر خاص باشد.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            if (studentRadio.isSelected()) {

                if (!ValidationUtil.isValidStudentId(id)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "شماره دانشجویی باید دقیقاً ۹ رقم باشد.",
                            "خطا",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Student student = new Student(
                        firstName,
                        lastName,
                        email,
                        password,
                        id
                );

                authenticationService.registerStudent(student);

            } else {

                Librarian librarian = new Librarian(
                        firstName,
                        lastName,
                        email,
                        password,
                        id
                );

                authenticationService.registerLibrarian(librarian);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "ثبت‌نام با موفقیت انجام شد.");

            new LoginFrame().setVisible(true);
            dispose();

        } catch (ValidationException | DuplicateUserException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "ثبت‌نام ناموفق",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}