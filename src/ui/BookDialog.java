package src.ui;

import src.exception.ValidationException;
import src.model.Book;
import src.repository.BookRepository;
import src.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

// Dialog for adding and editing books
public class BookDialog extends JDialog {

    private static final String COVERS_DIR = "covers";

    // Form fields
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

    // Services and state
    private final BookService bookService;
    private final Book existingBook;
    private boolean confirmed = false;

    // UI Colors
    private final Color PRIMARY_COLOR = new Color(255, 107, 107);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(200, 200, 200);

    // Constructors
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

    // Initialize dialog UI
    private void initializeDialog() {
        setSize(650, 850);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));

        // Apply Nimbus look and feel
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

        // For new books, available copies follows total copies
        if (existingBook == null) {
            availableCopiesSpinner.setEnabled(false);
            totalCopiesSpinner.addChangeListener(e -> availableCopiesSpinner.setValue(totalCopiesSpinner.getValue()));
        }
    }

    // Create form panel with all fields
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

        // Title field
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
        // RTL for Persian text
        titleField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        titleField.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // Author field
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
        // RTL for Persian text
        authorField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        authorField.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        // ISBN field
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
        // ISBN is usually English, so LTR
        isbnField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        isbnField.setHorizontalAlignment(JTextField.LEFT);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        // Publisher field
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
        // RTL for Persian text
        publisherField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        publisherField.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panel.add(publisherField, gbc);

        // Year field
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

        // Category combo box
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel categoryLabel = new JLabel("دسته‌بندی:");
        categoryLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        categoryLabel.setForeground(Color.BLACK);
        panel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>(new String[]{"عاشقانه","جنایی","ترسناک","فانتزی","تاریخی","زندگینامه","علمی","سایر"});
        categoryCombo.setFont(new Font("Tahoma", Font.PLAIN, 18));
        categoryCombo.setForeground(Color.BLACK);
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);

        // Total copies spinner
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

        // Available copies spinner
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

        // Book cover image section
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
        
        // Button panel for image operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        // Browse image button
        JButton browseButton = createStyledButton("انتخاب تصویر", new Color(52, 152, 219));
        browseButton.addActionListener(e -> chooseImage());
        
        // Google image search button
        JButton googleSearchButton = createGoogleSearchButton();
        
        buttonPanel.add(googleSearchButton);
        buttonPanel.add(browseButton);
        
        imagePanel.add(buttonPanel, BorderLayout.NORTH);

        // Image preview label
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

    // Create button panel
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

    // Create styled button with hover effect
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

    // Create Google image search button
    private JButton createGoogleSearchButton() {
        JButton googleButton = new JButton("جستجوی خودکار");
        googleButton.setFont(new Font("Tahoma", Font.BOLD, 18)); 
        googleButton.setBackground(new Color(211, 108, 240));
        googleButton.setForeground(Color.BLACK);
        googleButton.setFocusPainted(false);
        googleButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(211, 108, 240).darker(), 2),
            new EmptyBorder(10, 25, 10, 25) 
        ));
        googleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        googleButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        googleButton.addActionListener(e -> searchImageOnGoogle());
        
        // Hover effect
        googleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                googleButton.setBackground(new Color(211, 108, 240).darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                googleButton.setBackground(new Color(211, 108, 240));
            }
        });
        
        return googleButton;
    }

    // Search for book image on Google
    private void searchImageOnGoogle() {
        String bookTitle = titleField.getText().trim();
        
        if (bookTitle.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "لطفاً ابتدا عنوان کتاب را وارد کنید.",
                "خطا",
                JOptionPane.WARNING_MESSAGE
            );
            titleField.requestFocus();
            return;
        }
        
        try {
            // Build search query
            String searchQuery = "کتاب " + bookTitle;
            String encodedQuery = URLEncoder.encode(searchQuery, "UTF-8");
            String googleImageUrl = "https://www.google.com/search?q=" + encodedQuery + "&tbm=isch";
            
            // Open in default browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(googleImageUrl));
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "سیستم شما از باز کردن مرورگر پشتیبانی نمی‌کند.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "خطا در باز کردن مرورگر: " + ex.getMessage(),
                "خطا",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Load existing book data into form
    private void loadBook() {
        if (existingBook != null) {
            isbnField.setText(existingBook.getIsbn());
            isbnField.setEditable(false);
            titleField.setText(existingBook.getTitle());
            authorField.setText(existingBook.getAuthor());
            publisherField.setText(existingBook.getPublisher());
            yearSpinner.setValue(existingBook.getYear());

            // Set category
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

    // Update image preview
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

    // Choose image file
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("انتخاب تصویر جلد کتاب");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "فایل‌های تصویری", "jpg", "jpeg", "png"));

        // Configure file chooser font
        UIManager.put("FileChooser.font", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.listFont", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.iconFont", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.fileNameLabelFont", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.filesOfTypeLabelFont", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.lookInLabelFont", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.saveInLabelFont", new Font("Tahoma", Font.PLAIN, 18));
        UIManager.put("FileChooser.folderNameLabelFont", new Font("Tahoma", Font.PLAIN, 18));
        
        SwingUtilities.updateComponentTreeUI(fileChooser);
        fileChooser.setPreferredSize(new Dimension(900, 600));

        // Add image preview accessory
        JLabel previewLabel = new JLabel("پیش نمایش تصویر", JLabel.CENTER);
        previewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        previewLabel.setPreferredSize(new Dimension(200, 200));
        previewLabel.setMinimumSize(new Dimension(200, 200));
        previewLabel.setBackground(Color.WHITE);
        previewLabel.setOpaque(true);
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        
        // Update preview when file is selected
        fileChooser.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, evt -> {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists() && selectedFile.isFile()) {
                String fileName = selectedFile.getName().toLowerCase();
                // Check if it's an image file
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                    fileName.endsWith(".png") || fileName.endsWith(".gif") || 
                    fileName.endsWith(".bmp")) {
                    try {
                        ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                        Image scaledImage = icon.getImage().getScaledInstance(
                            180, 180, Image.SCALE_SMOOTH
                        );
                        previewLabel.setIcon(new ImageIcon(scaledImage));
                        previewLabel.setText("");
                        previewLabel.setHorizontalAlignment(JLabel.CENTER);
                    } catch (Exception ex) {
                        previewLabel.setIcon(null);
                        previewLabel.setText("خطا در بارگذاری");
                    }
                } else {
                    previewLabel.setIcon(null);
                    previewLabel.setText("فایل تصویر نیست");
                }
            } else {
                previewLabel.setIcon(null);
                previewLabel.setText("پیش نمایش تصویر");
            }
        });
        
        fileChooser.setAccessory(previewLabel);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            String lowerPath = path.toLowerCase();

            // Validate image format
            if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".png")) {
                File coversDir = new File(COVERS_DIR);
                if (!coversDir.exists()) {
                    coversDir.mkdirs();
                }

                // Copy image to covers directory
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

    // Get file extension
    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf);
    }

    // Save book data
    private void saveBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String publisher = publisherField.getText().trim();
        int year = (Integer) yearSpinner.getValue();
        String category = (String) categoryCombo.getSelectedItem();
        int totalCopies = (Integer) totalCopiesSpinner.getValue();
        int availableCopies = (Integer) availableCopiesSpinner.getValue();

        // Validate input
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

    // Validate form input
    private boolean validateInput(String title, String author, String isbn, String publisher,
                                 int year, String category, int totalCopies, int availableCopies) {
        // Check required fields
        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || publisher.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "تمامی فیلدها باید پر شوند.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // Validate year range
        if (year < 1300 || year > 1405) {
            JOptionPane.showMessageDialog(
                    this,
                    "سال انتشار باید بین 1300 تا 1405 باشد.",
                    "خطای اعتبارسنجی",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // Validate copy counts
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

    // Reset form to default values
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

    // Cancel and close dialog
    private void onCancel() {
        confirmed = false;
        dispose();
    }

    // Check if dialog was confirmed
    public boolean isConfirmed() {
        return confirmed;
    }

    // Get the book object
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