package src.ui;

import src.model.Book;
import src.model.Student;
import src.repository.BookRepository;
import src.repository.LoanRepository;
import src.repository.ReservationRepository;
import src.service.BookService;
import src.service.LoanService;
import src.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Student dashboard.
 */
public class StudentPanel extends JFrame {

    private final Student student;

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    private final BookService bookService;
    private final LoanService loanService;
    private final ReservationService reservationService;

    private JTable bookTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private JLabel searchResultLabel;

    private JButton loanButton;
    private JButton returnBookButton;
    private JButton searchButton;
    private JButton reserveButton;
    private JButton extendButton;
    private JButton myLoansButton;
    private JButton refreshButton;

    // Colors
    private final Color PRIMARY_COLOR = new Color(255, 107, 107);
    private final Color SECONDARY_COLOR = new Color(78, 205, 196);
    private final Color BACKGROUND_COLOR = new Color(247, 218, 181);
    private final Color HEADER_COLOR = new Color(247, 218, 181);
    private final Color TABLE_ALTERNATE_COLOR = new Color(236, 240, 241);
    private final Color SELECTION_COLOR = new Color(52, 152, 219);
    private final Color SELECTION_TEXT_COLOR = Color.WHITE;

    public StudentPanel(Student student) {

        this.student = student;

        // Initialize repositories
        bookRepository = new BookRepository();
        loanRepository = new LoanRepository();
        reservationRepository = new ReservationRepository();

        // Initialize services
        bookService = new BookService(bookRepository);
        loanService = new LoanService(bookRepository, loanRepository);
        reservationService = new ReservationService(
                reservationRepository,
                bookRepository,
                loanService
        );

        // Setup UI components
        initializeFrame();
        initializeTable();
        initializeTopPanel();
        initializeBottomPanel();
        loadBooks(bookService.getAllBooks());
    }

    // Initialize main frame
    private void initializeFrame() {

        setTitle("پنل دانشجو - " + student.getFirstName());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Apply Nimbus look and feel with custom colors
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", PRIMARY_COLOR);
            UIManager.put("nimbusBlueGrey", SECONDARY_COLOR);
            UIManager.put("control", BACKGROUND_COLOR);
            
            Font defaultFont = new Font("Tahoma", Font.PLAIN, 16);
            Font boldFont = new Font("Tahoma", Font.BOLD, 16);
            
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("OptionPane.font", defaultFont);
            
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("Table.foreground", Color.BLACK);
            UIManager.put("TableHeader.foreground", Color.BLACK);

        } catch (Exception e) {
            // Fallback to default
        }

        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JLabel headerLabel = new JLabel("پنل دانشجو - " + student.getFirstName() + " " + student.getLastName());
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        // Today's date
        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", new java.util.Locale("fa"));
        String today = sdf.format(new java.util.Date());
        dateLabel.setText("📅 " + today);
        dateLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(dateLabel, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("👤 " + student.getFirstName());
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        userLabel.setForeground(Color.BLACK);
        userLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    // Initialize the book table
    private void initializeTable() {

        // Table structure similar to librarian panel
        tableModel = new DefaultTableModel(
                new Object[]{"تصویر", "شابک", "عنوان", "نویسنده", "ناشر", "سال", "موضوع", "تعداد کل", "موجود"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(65);
        bookTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
        bookTable.setBackground(Color.WHITE);
        bookTable.setForeground(Color.BLACK);
        bookTable.setGridColor(new Color(236, 240, 241));
        bookTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        // Custom cell renderer for alternating row colors
        bookTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (isSelected) {
                    c.setBackground(SELECTION_COLOR);
                    c.setForeground(SELECTION_TEXT_COLOR);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALTERNATE_COLOR);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        
        // Set custom renderer for image column
        bookTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
        
        // Add sorter with custom configurations
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        // Disable sorting for specific columns
        sorter.setSortable(0, false); // تصویر
        sorter.setSortable(6, false); // موضوع
        sorter.setSortable(7, false); // تعداد کل
        sorter.setSortable(8, false); // موجود
        
        // Custom comparator for year column
        sorter.setComparator(5, (o1, o2) -> {
            try {
                Integer year1 = Integer.parseInt(o1.toString());
                Integer year2 = Integer.parseInt(o2.toString());
                return year1.compareTo(year2);
            } catch (NumberFormatException e) {
                return 0;
            }
        });
        
        // Style table header
        JTableHeader header = bookTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Tahoma", Font.BOLD, 17));
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Set column widths
        setColumnSizes(bookTable, new int[]{60, 80, 150, 120, 100, 70, 100, 90, 110});

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    // Initialize top panel with search components
    private void initializeTopPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel searchLabel = new JLabel("جستجو:");
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        searchLabel.setForeground(Color.BLACK);
        panel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        searchField.setForeground(Color.BLACK);
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        // Search on Enter key
        searchField.addActionListener(e -> searchBook());
        panel.add(searchField);

        searchCombo = new JComboBox<>(new String[]{"عنوان", "نویسنده", "موضوع", "سال"});
        searchCombo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        searchCombo.setBackground(Color.WHITE);
        searchCombo.setForeground(Color.BLACK);
        searchCombo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        panel.add(searchCombo);

        searchButton = createStyledButton("جستجو", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchBook());
        panel.add(searchButton);

        // Result count label
        searchResultLabel = new JLabel("تعداد نتایج: ۰");
        searchResultLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        searchResultLabel.setForeground(new Color(143, 143, 143));
        searchResultLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        panel.add(searchResultLabel);

        add(panel, BorderLayout.NORTH);
    }

    // Initialize bottom panel with action buttons
    private void initializeBottomPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        reserveButton = createStyledButton("رزرو", new Color(255, 159, 67));
        extendButton = createStyledButton("تمدید", new Color(220, 230, 83));
        loanButton = createStyledButton("امانت", new Color(46, 204, 113));
        returnBookButton = createStyledButton("بازگشت کتاب", new Color(231, 76, 60));
        myLoansButton = createStyledButton("کتاب‌های من", new Color(155, 89, 182));
        refreshButton = createStyledButton("بروزرسانی", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            bookRepository.load();
            loanRepository.load();
            reservationRepository.load();
            loadBooks(bookService.getAllBooks());
        });
        reserveButton.addActionListener(e -> reserveBook());
        extendButton.addActionListener(e -> extendLoan());
        loanButton.addActionListener(e -> loanBook());
        returnBookButton.addActionListener(e -> returnBook());
        myLoansButton.addActionListener(e -> showMyLoans());

        panel.add(reserveButton);
        panel.add(extendButton);
        panel.add(loanButton);
        panel.add(returnBookButton);
        panel.add(myLoansButton);
        panel.add(refreshButton);

        add(panel, BorderLayout.SOUTH);
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
            new EmptyBorder(8, 18, 8, 18)
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

    // Set preferred column widths
    private void setColumnSizes(JTable table, int[] widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    // Custom cell renderer for image column
    private class ImageRenderer extends DefaultTableCellRenderer {
        private static final int IMAGE_WIDTH = 40;
        private static final int IMAGE_HEIGHT = 55;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);
            
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setText("");
            
            if (isSelected) {
                label.setBackground(SELECTION_COLOR);
                label.setForeground(SELECTION_TEXT_COLOR);
            } else {
                label.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALTERNATE_COLOR);
                label.setForeground(Color.BLACK);
            }
            
            if (value instanceof ImageIcon) {
                ImageIcon icon = (ImageIcon) value;
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else {
                label.setIcon(null);
            }
            
            return label;
        }
    }

    // Load books into table
    private void loadBooks(List<Book> books) {

        tableModel.setRowCount(0);

        for (Book book : books) {

            tableModel.addRow(new Object[]{
                    loadCoverImage(book.getImagePath()),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getYear(),
                    book.getCategory(),
                    book.getTotalCopies(),
                    book.getAvailableCopies()
            });

        }
        
        // Update result count
        if (searchResultLabel != null) {
            searchResultLabel.setText("تعداد نتایج: " + tableModel.getRowCount());
        }
        
        // Reset sorter after loading
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) bookTable.getRowSorter();
        if (sorter != null) {
            sorter.setRowFilter(null);
        }
    }

    // Load cover image from file
    private ImageIcon loadCoverImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        File imageFile = new File("covers", imagePath);
        if (!imageFile.exists()) {
            return null;
        }

        try {
            ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(40, 55, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
    }

    // Search for books based on selected criteria
    private void searchBook() {

        String keyword = searchField.getText().trim();
        String criteria = (String) searchCombo.getSelectedItem();

        if (keyword.isEmpty()) {
            loadBooks(bookService.getAllBooks());
            return;
        }

        List<Book> results = new ArrayList<>();
        
        switch (criteria) {
            case "عنوان":
                results = bookService.searchByTitle(keyword);
                break;
            case "نویسنده":
                results = bookService.searchByAuthor(keyword);
                break;
            case "موضوع":
                results = bookService.searchByCategory(keyword);
                break;
            case "سال":
                try {
                    int year = Integer.parseInt(keyword);
                    results = bookService.getAllBooks().stream()
                            .filter(b -> b.getYear() == year)
                            .collect(Collectors.toList());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "لطفاً یک سال معتبر وارد کنید (مثال: 1400).",
                            "خطا",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                break;
            default:
                results = bookService.searchByTitle(keyword);
                break;
        }

        loadBooks(results);
        
        // Reset sorter after search
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) bookTable.getRowSorter();
        if (sorter != null) {
            sorter.setRowFilter(null);
        }
    }
    
    // Reserve selected book
    private void reserveBook() {

        Book book = getSelectedBook();

        if (book == null) {
            return;
        }
        bookRepository.load();
        loanRepository.load();
        reservationRepository.load();
        loadBooks(bookService.getAllBooks());
        try {

            reservationService.createReservation(
                    student,
                    book,
                    java.time.LocalDate.now()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "درخواست رزرو با موفقیت ثبت شد."
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا در رزرو",
                    JOptionPane.ERROR_MESSAGE
            );

        }

    }

    // Extend loan period for selected book
    private void extendLoan() {

        Book book = getSelectedBook();

        if (book == null) {
            return;
        }
        bookRepository.load();
        loanRepository.load();
        reservationRepository.load();
        loadBooks(bookService.getAllBooks());
        try {

            loanService.extendLoan(
                    book.getIsbn(),
                    student.getStudentId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "درخواست تمدید با موفقیت ثبت شد. در انتظار تأیید کتابدار."
            );

        } catch (IllegalStateException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا در تمدید",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا در تمدید",
                    JOptionPane.ERROR_MESSAGE
            );

        }

    }

    // Borrow selected book
    private void loanBook() {

        Book book = getSelectedBook();

        if (book == null) {
            return;
        }
        bookRepository.load();
        loanRepository.load();
        reservationRepository.load();
        loadBooks(bookService.getAllBooks());
        try {

            loanService.borrowBook(
                    student,
                    book.getIsbn(),
                    java.time.LocalDate.now()
            );

            loadBooks(bookService.getAllBooks());

            JOptionPane.showMessageDialog(
                    this,
                    "کتاب با موفقیت امانت گرفته شد."
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا در امانت",
                    JOptionPane.ERROR_MESSAGE
            );

        }

    }

    // Return selected book
    private void returnBook() {

        Book book = getSelectedBook();

        if (book == null) {
            return;
        }
        bookRepository.load();
        loanRepository.load();
        reservationRepository.load();
        loadBooks(bookService.getAllBooks());
        try {

            loanService.returnBook(
                    book.getIsbn(),
                    student.getStudentId()
            );

            loadBooks(bookService.getAllBooks());

            JOptionPane.showMessageDialog(
                    this,
                    "کتاب با موفقیت بازگردانده شد."
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا در بازگشت کتاب",
                    JOptionPane.ERROR_MESSAGE
            );

        }

    }

    // Display current loans for the student
    private void showMyLoans() {
        bookRepository.load();
        loanRepository.load();
        reservationRepository.load();
        loadBooks(bookService.getAllBooks());
        java.util.List<src.model.Loan> loans =
                loanService.getActiveLoansByStudent(student);

        if (loans.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "شما هیچ کتاب امانتی ندارید."
            );

            return;
        }

        StringBuilder builder = new StringBuilder();

        for (src.model.Loan loan : loans) {

            builder.append("عنوان: ")
                    .append(loan.getBook().getTitle())
                    .append("\n");

            builder.append("شابک: ")
                    .append(loan.getBook().getIsbn())
                    .append("\n");

            builder.append("تاریخ امانت: ")
                    .append(loan.getLoanDate())
                    .append("\n");

            builder.append("تاریخ بازگشت: ")
                    .append(loan.getDueDate())
                    .append("\n");

            builder.append("تمدید شده: ")
                    .append(loan.isExtended() ? "بله" : "خیر")
                    .append("\n");

            builder.append("----------------------------------\n");

        }

        JTextArea area = new JTextArea(builder.toString());
        area.setEditable(false);
        area.setFont(new Font("Tahoma", Font.PLAIN, 16));
        area.setForeground(Color.BLACK);
        area.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "کتاب‌های امانتی من",
                JOptionPane.INFORMATION_MESSAGE
        );

    }

    // Get the currently selected book from table
    private Book getSelectedBook() {

        int row = bookTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً یک کتاب را انتخاب کنید."
            );

            return null;
        }

        String isbn = tableModel.getValueAt(row, 1).toString();

        return bookService.findByIsbn(isbn);

    }

}