package src.ui;

import src.exception.ValidationException;
import src.model.Book;
import src.repository.BookRepository;
import src.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDialog extends JDialog {

    private static final String COVERS_DIR = "covers";

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField publisherField;
    private JSpinner yearSpinner;
    private JComboBox<String> categoryCombo;
    private JSpinner totalCopiesSpinner;
    private JSpinner availableCopiesSpinner;
    private JLabel imagePreviewLabel;
    private String imagePath = "";

    private final BookService bookService;
    private final Book existingBook;
    private boolean confirmed = false;

    // Colors
    private final Color PRIMARY_COLOR = new Color(255, 107, 107);
    // private final Color SECONDARY_COLOR = new Color(78, 205, 196);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(200, 200, 200);
    // private final Color BUTTON_COLOR = new Color(52, 152, 219);
    // private final Color TEXT_COLOR = Color.BLACK;

    public BookDialog(Frame parent) {
        super(parent, "افزودن کتاب", true);
        this.existingBook = null;
        this.bookService = new BookService(new BookRepository());
        initializeDialog();
    }

    public BookDialog(Frame parent, BookService bookService) {
        super(parent, "افزودن کتاب", true);
        this.existingBook = null;
        this.bookService = bookService;
        initializeDialog();
    }

    public BookDialog(Frame parent, Book book, BookService bookService) {
        super(parent, "ویرایش کتاب", true);
        this.existingBook = book;
        this.bookService = bookService;
        initializeDialog();
        loadBook();
    }

    private void initializeDialog() {
        setSize(650, 800);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", PRIMARY_COLOR);
            UIManager.put("control", BACKGROUND_COLOR);
            
            Font defaultFont = new Font("Tahoma", Font.PLAIN, 18);
            // Font boldFont = new Font("Tahoma", Font.BOLD, 18);
            
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("Spinner.font", defaultFont);
            UIManager.put("OptionPane.font", defaultFont);
            
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("TextField.foreground", Color.BLACK);

        } catch (Exception e) {
            // Fallback to default
        }

        // Create a scroll pane for the form
        JScrollPane scrollPane = new JScrollPane(createForm());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        if (existingBook == null) {
            availableCopiesSpinner.setEnabled(false);
            totalCopiesSpinner.addChangeListener(e -> availableCopiesSpinner.setValue(totalCopiesSpinner.getValue()));
        }
    }

    private JPanel createForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("عنوان:");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel, gbc);

        titleField = new JTextField(20);
        titleField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        titleField.setForeground(Color.BLACK);
        titleField.setBackground(Color.WHITE);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel authorLabel = new JLabel("نویسنده:");
        authorLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        authorLabel.setForeground(Color.BLACK);
        panel.add(authorLabel, gbc);

        authorField = new JTextField(20);
        authorField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        authorField.setForeground(Color.BLACK);
        authorField.setBackground(Color.WHITE);
        authorField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        // ISBN
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel isbnLabel = new JLabel("شابک:");
        isbnLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        isbnLabel.setForeground(Color.BLACK);
        panel.add(isbnLabel, gbc);

        isbnField = new JTextField(20);
        isbnField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        isbnField.setForeground(Color.BLACK);
        isbnField.setBackground(Color.WHITE);
        isbnField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        // Publisher
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel publisherLabel = new JLabel("ناشر:");
        publisherLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        publisherLabel.setForeground(Color.BLACK);
        panel.add(publisherLabel, gbc);

        publisherField = new JTextField(20);
        publisherField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        publisherField.setForeground(Color.BLACK);
        publisherField.setBackground(Color.WHITE);
        publisherField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(publisherField, gbc);

        // Year
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel yearLabel = new JLabel("سال انتشار:");
        yearLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        yearLabel.setForeground(Color.BLACK);
        panel.add(yearLabel, gbc);

        yearSpinner = new JSpinner(new SpinnerNumberModel(1400, 1300, 1405, 5));
        yearSpinner.setFont(new Font("Tahoma", Font.PLAIN, 18));
        yearSpinner.setForeground(Color.BLACK);
        yearSpinner.setBackground(Color.WHITE);
        ((JSpinner.DefaultEditor) yearSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panel.add(yearSpinner, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel categoryLabel = new JLabel("دسته‌بندی:");
        categoryLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        categoryLabel.setForeground(Color.BLACK);
        panel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>(new String[]{"داستانی", "علمی", "فناوری", "تاریخی", "زندگینامه", "ریاضی", "ادبیات", "سایر"});
        categoryCombo.setFont(new Font("Tahoma", Font.PLAIN, 18));
        categoryCombo.setForeground(Color.BLACK);
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);

        // Total Copies
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel totalLabel = new JLabel("تعداد کل:");
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        totalLabel.setForeground(Color.BLACK);
        panel.add(totalLabel, gbc);

        totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        totalCopiesSpinner.setFont(new Font("Tahoma", Font.PLAIN, 18));
        totalCopiesSpinner.setForeground(Color.BLACK);
        totalCopiesSpinner.setBackground(Color.WHITE);
        ((JSpinner.DefaultEditor) totalCopiesSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panel.add(totalCopiesSpinner, gbc);

        // Available Copies
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel availableLabel = new JLabel("تعداد موجود:");
        availableLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        availableLabel.setForeground(Color.BLACK);
        panel.add(availableLabel, gbc);

        availableCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        availableCopiesSpinner.setFont(new Font("Tahoma", Font.PLAIN, 18));
        availableCopiesSpinner.setForeground(Color.BLACK);
        availableCopiesSpinner.setBackground(Color.WHITE);
        ((JSpinner.DefaultEditor) availableCopiesSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panel.add(availableCopiesSpinner, gbc);

        // Book Cover - با تنظیمات جدید
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel coverLabel = new JLabel("تصویر جلد:");
        coverLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        coverLabel.setForeground(Color.BLACK);
        coverLabel.setVerticalAlignment(SwingConstants.TOP);
        panel.add(coverLabel, gbc);

        JPanel imagePanel = new JPanel(new BorderLayout(10, 5));
        imagePanel.setBackground(BACKGROUND_COLOR);
        imagePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JButton browseButton = createStyledButton("انتخاب تصویر", new Color(52, 152, 219));
        browseButton.addActionListener(e -> chooseImage());
        buttonPanel.add(browseButton);
        
        imagePanel.add(buttonPanel, BorderLayout.NORTH);

        imagePreviewLabel = new JLabel("بدون تصویر", JLabel.CENTER);
        imagePreviewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        imagePreviewLabel.setForeground(Color.BLACK);
        imagePreviewLabel.setPreferredSize(new Dimension(150, 150));
        imagePreviewLabel.setMinimumSize(new Dimension(150, 150));
        imagePreviewLabel.setBackground(Color.WHITE);
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(imagePanel, gbc);

        return panel;
    }

    private JPanel createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton saveButton = createStyledButton("ذخیره", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("انصراف", new Color(231, 76, 60));
        JButton resetButton = createStyledButton("بازنشانی", new Color(241, 196, 15));

        saveButton.addActionListener(e -> saveBook());
        cancelButton.addActionListener(e -> onCancel());
        resetButton.addActionListener(e -> resetForm());

        panel.add(saveButton);
        panel.add(cancelButton);
        panel.add(resetButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 18));
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

    private void loadBook() {
        if (existingBook != null) {
            isbnField.setText(existingBook.getIsbn());
            isbnField.setEditable(false);
            titleField.setText(existingBook.getTitle());
            authorField.setText(existingBook.getAuthor());
            publisherField.setText(existingBook.getPublisher());
            yearSpinner.setValue(existingBook.getYear());

            boolean categoryFound = false;
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).equals(existingBook.getCategory())) {
                    categoryCombo.setSelectedIndex(i);
                    categoryFound = true;
                    break;
                }
            }
            if (!categoryFound) {
                categoryCombo.setSelectedIndex(categoryCombo.getItemCount() - 1);
            }

            totalCopiesSpinner.setValue(existingBook.getTotalCopies());
            availableCopiesSpinner.setValue(existingBook.getAvailableCopies());
            imagePath = existingBook.getImagePath();
            updateImagePreview();
        }
    }

    private void updateImagePreview() {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(COVERS_DIR, imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                icon = new ImageIcon(icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                imagePreviewLabel.setIcon(icon);
                imagePreviewLabel.setText("");
                imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
            } else {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("بدون تصویر");
                imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
            }
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("بدون تصویر");
            imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("انتخاب تصویر جلد کتاب");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "فایل‌های تصویری", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            String lowerPath = path.toLowerCase();

            if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".png")) {
                File coversDir = new File(COVERS_DIR);
                if (!coversDir.exists()) {
                    coversDir.mkdirs();
                }

                String extension = getFileExtension(selectedFile.getName());
                String uniqueName = System.currentTimeMillis() + extension;
                File destFile = new File(coversDir, uniqueName);

                try (FileInputStream fis = new FileInputStream(selectedFile);
                     FileOutputStream fos = new FileOutputStream(destFile)) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    imagePath = uniqueName;
                    updateImagePreview();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "خطا در کپی کردن تصویر: " + ex.getMessage(),
                            "خطا",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "لطفاً یک تصویر معتبر انتخاب کنید (jpg, jpeg, png).",
                        "فایل نامعتبر",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf);
    }

    private void saveBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String publisher = publisherField.getText().trim();
        int year = (Integer) yearSpinner.getValue();
        String category = (String) categoryCombo.getSelectedItem();
        int totalCopies = (Integer) totalCopiesSpinner.getValue();
        int availableCopies = (Integer) availableCopiesSpinner.getValue();

        if (!validateInput(title, author, isbn, publisher, year, category, totalCopies, availableCopies)) {
            return;
        }

        Book book = new Book(isbn, title, author, publisher, year, category, totalCopies, availableCopies, imagePath);

        try {
            if (existingBook == null) {
                bookService.addBook(book);
                JOptionPane.showMessageDialog(this, "کتاب با موفقیت اضافه شد.");
            } else {
                bookService.updateBook(isbn, book);
                JOptionPane.showMessageDialog(this, "کتاب با موفقیت به‌روزرسانی شد.");
            }
            confirmed = true;
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean validateInput(String title, String author, String isbn, String publisher,
                                 int year, String category, int totalCopies, int availableCopies) {
        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || publisher.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "تمامی فیلدها باید پر شوند.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (year < 1300 || year > 1405) {
            JOptionPane.showMessageDialog(
                    this,
                    "سال انتشار باید بین 1300 تا 1405 باشد.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (totalCopies < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "تعداد کل نمی‌تواند منفی باشد.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (availableCopies < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "تعداد موجود نمی‌تواند منفی باشد.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (availableCopies > totalCopies) {
            JOptionPane.showMessageDialog(
                    this,
                    "تعداد موجود نمی‌تواند از تعداد کل بیشتر باشد.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    private void resetForm() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        publisherField.setText("");
        yearSpinner.setValue(1400);
        categoryCombo.setSelectedIndex(0);
        totalCopiesSpinner.setValue(1);
        availableCopiesSpinner.setValue(1);
        imagePath = "";
        updateImagePreview();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Book getBook() {
        return existingBook != null ? existingBook : new Book(
                isbnField.getText().trim(),
                titleField.getText().trim(),
                authorField.getText().trim(),
                publisherField.getText().trim(),
                (Integer) yearSpinner.getValue(),
                (String) categoryCombo.getSelectedItem(),
                (Integer) totalCopiesSpinner.getValue(),
                (Integer) availableCopiesSpinner.getValue(),
                imagePath
        );
    }
}