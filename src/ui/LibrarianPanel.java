package src.ui;

import src.exception.ValidationException;
import src.model.Book;
import src.model.Loan;
import src.model.Reservation;
import src.model.Student;
import src.repository.BookRepository;
import src.repository.LoanRepository;
import src.repository.ReservationRepository;
import src.repository.UserRepository;
import src.service.BookService;
import src.service.LoanService;
import src.service.ReservationService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// import java.io.File;
// import java.util.List;
// import java.util.ArrayList;

public class LibrarianPanel extends JFrame {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    private final BookService bookService;
    private final LoanService loanService;
    private final ReservationService reservationService;

    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTextField booksSearchField;
    private JComboBox<String> booksSearchCombo;

    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;

    private JTable extensionsTable;
    private DefaultTableModel extensionsTableModel;

    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;

    private JTable mostBorrowedTable;
    private DefaultTableModel mostBorrowedTableModel;
    private JTable overdueStudentsTable;
    private DefaultTableModel overdueStudentsTableModel;
    private JLabel totalBooksLabel;
    private JLabel availableBooksLabel;
    private JLabel borrowedBooksLabel;
    private int dailyFine = 5000;

    // Colors
    private final Color PRIMARY_COLOR = new Color(255, 107, 107);
    private final Color SECONDARY_COLOR = new Color(78, 205, 196);
    // private final Color ACCENT_COLOR = new Color(255, 159, 67);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(237, 180, 109);
    private final Color BUTTON_COLOR = new Color(52, 152, 219);
    private final Color BUTTON_SAVE_MODE = new Color(92, 191, 102);
    private final Color BUTTON_DRAFT_MODE = new Color(217, 102, 110);
    private final Color TABLE_ALTERNATE_COLOR = new Color(236, 240, 241);
    private final Color SELECTION_COLOR = new Color(52, 152, 219);
    private final Color SELECTION_TEXT_COLOR = Color.WHITE;

    public LibrarianPanel() {
        bookRepository = new BookRepository();
        loanRepository = new LoanRepository();
        reservationRepository = new ReservationRepository();
        userRepository = new UserRepository();

        bookService = new BookService(bookRepository);
        loanService = new LoanService(bookRepository, loanRepository);
        reservationService = new ReservationService(reservationRepository, bookRepository, loanService);

        initializePanel();
    }

    private void initializePanel() {
        setTitle("پنل کتابدار - مدیریت کتابخانه");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

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
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("TabbedPane.font", boldFont);
            UIManager.put("OptionPane.font", defaultFont);
            
            // رنگ متن‌ها مشکی
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("Table.foreground", Color.BLACK);
            UIManager.put("TabbedPane.foreground", Color.BLACK);
            UIManager.put("TableHeader.foreground", Color.BLACK);

        } catch (Exception e) {
            // Fallback to default
        }

        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 18));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(Color.BLACK);
        tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        tabbedPane.addTab("مدیریت کتاب‌ها", createBooksTab());
        tabbedPane.addTab("درخواست‌های رزرو", createReservationsTab());
        tabbedPane.addTab("درخواست‌های تمدید", createExtensionsTab());
        tabbedPane.addTab("دانشجویان", createStudentsTab());
        tabbedPane.addTab("گزارش‌ها", createReportsTab());

        add(tabbedPane, BorderLayout.CENTER);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JLabel headerLabel = new JLabel("سیستم مدیریت کتابخانه - پنل کتابدار");
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        booksTableModel = new DefaultTableModel(
                new Object[]{"تصویر", "شابک", "عنوان", "نویسنده", "ناشر", "سال انتشار", "موضوع", "تعداد کل", "موجود"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = new JTable(booksTableModel);
        booksTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setRowHeight(65);
        booksTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
        booksTable.setBackground(Color.WHITE);
        booksTable.setForeground(Color.BLACK);
        booksTable.setGridColor(new Color(236, 240, 241));
        booksTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        // Custom renderer for better selection visibility
        booksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        
        // Header styling
        JTableHeader header = booksTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Tahoma", Font.BOLD, 17));
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        booksTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
        
        setColumnSizes(booksTable, new int[]{60, 80, 150, 120, 100, 70, 100, 90, 110});

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        topPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JLabel searchLabel = new JLabel("جستجو:");
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        searchLabel.setForeground(Color.BLACK);
        topPanel.add(searchLabel);
        
        booksSearchField = new JTextField(15);
        booksSearchField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        booksSearchField.setBackground(Color.WHITE);
        booksSearchField.setForeground(Color.BLACK);
        booksSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        topPanel.add(booksSearchField);
        
        booksSearchCombo = new JComboBox<>(new String[]{"عنوان", "نویسنده", "موضوع", "سال"});
        booksSearchCombo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        booksSearchCombo.setBackground(Color.WHITE);
        booksSearchCombo.setForeground(Color.BLACK);
        booksSearchCombo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        topPanel.add(booksSearchCombo);
        
        JButton searchButton = createStyledButton("جستجو", BUTTON_COLOR);
        topPanel.add(searchButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        bottomPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JButton addButton = createStyledButton("افزودن کتاب", new Color(46, 204, 113));
        JButton editButton = createStyledButton("ویرایش کتاب", new Color(241, 196, 15));
        JButton deleteButton = createStyledButton("حذف کتاب", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("بروزرسانی", new Color(52, 152, 219));
        
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchBooks());
        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        refreshButton.addActionListener(e -> {
            refreshAllData();
            refreshBooksTable();
            refreshReservationsTable();
            refreshExtensionsTable();
            refreshStudentsTable();
            refreshReports();
        });

        refreshBooksTable();

        return panel;
    }

    private JPanel createReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        reservationsTableModel = new DefaultTableModel(
                new Object[]{"نام دانشجو", "شماره دانشجویی", "عنوان کتاب", "تاریخ درخواست", "وضعیت"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setRowHeight(35);
        reservationsTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
        reservationsTable.setBackground(Color.WHITE);
        reservationsTable.setForeground(Color.BLACK);
        reservationsTable.setGridColor(new Color(236, 240, 241));
        reservationsTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        reservationsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        
        JTableHeader header = reservationsTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Tahoma", Font.BOLD, 17));
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        setColumnSizes(reservationsTable, new int[]{120, 100, 150, 120, 80});

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JButton approveButton = createStyledButton("تأیید", new Color(46, 204, 113));
        JButton rejectButton = createStyledButton("رد", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("بروزرسانی", new Color(52, 152, 219));
        
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> approveReservation());
        rejectButton.addActionListener(e -> rejectReservation());
        refreshButton.addActionListener(e -> {
            refreshAllData();
            refreshReservationsTable();
        });

        refreshReservationsTable();

        return panel;
    }

    private JPanel createExtensionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        extensionsTableModel = new DefaultTableModel(
                new Object[]{"دانشجو", "کتاب", "تاریخ امانت", "تاریخ بازگشت", "وضعیت تمدید"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        extensionsTable = new JTable(extensionsTableModel);
        extensionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        extensionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        extensionsTable.setRowHeight(35);
        extensionsTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
        extensionsTable.setBackground(Color.WHITE);
        extensionsTable.setForeground(Color.BLACK);
        extensionsTable.setGridColor(new Color(236, 240, 241));
        extensionsTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        extensionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        
        JTableHeader header = extensionsTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Tahoma", Font.BOLD, 17));
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        setColumnSizes(extensionsTable, new int[]{120, 100, 120, 120, 100});

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JButton approveExtensionButton = createStyledButton("تأیید تمدید", new Color(46, 204, 113));
        JButton refreshButton = createStyledButton("بروزرسانی", new Color(52, 152, 219));
        
        buttonPanel.add(approveExtensionButton);
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(extensionsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        approveExtensionButton.addActionListener(e -> approveExtension());
        refreshButton.addActionListener(e -> {
            refreshAllData();
            refreshExtensionsTable();
        });

        refreshExtensionsTable();

        return panel;
    }

    private JPanel createStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        studentsTableModel = new DefaultTableModel(
                new Object[]{"نام", "نام خانوادگی", "شماره دانشجویی", "ایمیل", "تعداد امانت", "بدهی فعلی"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsTable = new JTable(studentsTableModel);
        studentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.setRowHeight(35);
        studentsTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
        studentsTable.setBackground(Color.WHITE);
        studentsTable.setForeground(Color.BLACK);
        studentsTable.setGridColor(new Color(236, 240, 241));
        studentsTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        studentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        
        JTableHeader header = studentsTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Tahoma", Font.BOLD, 17));
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        setColumnSizes(studentsTable, new int[]{100, 100, 100, 150, 120, 80});

        JButton refreshButton = createStyledButton("بروزرسانی", new Color(52, 152, 219));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> {
            refreshAllData();
            refreshStudentsTable();
        });

        refreshStudentsTable();

        return panel;
    }

private JPanel createReportsTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(BACKGROUND_COLOR);
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    summaryPanel.setBackground(new Color(255, 255, 255, 200));
    summaryPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
        new EmptyBorder(10, 10, 10, 10)
    ));
    summaryPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    
    totalBooksLabel = createStyledLabel("تعداد کل کتاب‌ها: 0");
    availableBooksLabel = createStyledLabel("کتاب‌های موجود: 0");
    borrowedBooksLabel = createStyledLabel("کتاب‌های امانت داده شده: 0");
    JLabel dailyFineLabel = createStyledLabel("جریمه روزانه: ");
    JTextField dailyFineField = new JTextField(String.valueOf(dailyFine), 3);
    dailyFineField.setFont(new Font("Tahoma", Font.PLAIN, 16));
    dailyFineField.setBackground(Color.WHITE);
    dailyFineField.setForeground(Color.BLACK);
    JButton updateFineButton = new JButton("ثبت");
    updateFineButton.setFont(new Font("Tahoma", Font.BOLD, 16));
    updateFineButton.setBackground(BUTTON_SAVE_MODE);
    updateFineButton.setForeground(Color.BLACK);
    updateFineButton.setFocusPainted(false);
    updateFineButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BUTTON_SAVE_MODE.darker(), 2),
        new EmptyBorder(8, 18, 8, 18)
    ));
    updateFineButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    updateFineButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    updateFineButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            if (!dailyFineField.getText().equals(String.valueOf(dailyFine))) {
                updateFineButton.setBackground(BUTTON_DRAFT_MODE.darker());
            } else {
                updateFineButton.setBackground(BUTTON_SAVE_MODE.darker());
            }
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            if (!dailyFineField.getText().equals(String.valueOf(dailyFine))) {
                updateFineButton.setBackground(BUTTON_DRAFT_MODE);
            } else {
                updateFineButton.setBackground(BUTTON_SAVE_MODE);
            }
        }
    });
    dailyFineField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        private void updateButtonState() {
            String currentText = dailyFineField.getText();
            if (!currentText.equals(String.valueOf(dailyFine))) {
                updateFineButton.setBackground(BUTTON_DRAFT_MODE);
                updateFineButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BUTTON_DRAFT_MODE.darker(), 2),
                    new EmptyBorder(8, 18, 8, 18)
                ));
            } else {
                updateFineButton.setBackground(BUTTON_SAVE_MODE);
                updateFineButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BUTTON_SAVE_MODE.darker(), 2),
                    new EmptyBorder(8, 18, 8, 18)
                ));
            }
        }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState(); }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState(); }
    });
    updateFineButton.addActionListener(e -> {
        try {
            int newFine = Integer.parseInt(dailyFineField.getText().trim());
            if (newFine <= 0) {
                JOptionPane.showMessageDialog(
                        LibrarianPanel.this,
                        "جریمه روزانه باید عددی مثبت باشد.",
                        "خطا",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            dailyFine = newFine;
            dailyFineField.setText(String.valueOf(dailyFine));
            refreshReports();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    LibrarianPanel.this,
                    "لطفاً یک عدد معتبر وارد کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    });
    
    summaryPanel.add(totalBooksLabel);
    summaryPanel.add(availableBooksLabel);
    summaryPanel.add(borrowedBooksLabel);
    summaryPanel.add(dailyFineLabel);
    summaryPanel.add(dailyFineField);
    summaryPanel.add(updateFineButton);

    JButton refreshButton = createStyledButton("بروزرسانی", new Color(52, 152, 219));
    refreshButton.addActionListener(e -> {
        refreshAllData();
        refreshReports();
    });
    summaryPanel.add(refreshButton);

    JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
    tablesPanel.setBackground(BACKGROUND_COLOR);
    tablesPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    // ===== پنل کتاب‌های پرطرفدار =====
    JPanel borrowedPanel = new JPanel(new BorderLayout(5, 5));
    borrowedPanel.setBackground(Color.WHITE);
    borrowedPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
        new EmptyBorder(10, 10, 10, 10)
    ));
    borrowedPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    
    JPanel borrowedHeaderPanel = new JPanel(new BorderLayout(5, 5));
    borrowedHeaderPanel.setBackground(Color.WHITE);
    borrowedHeaderPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    
    JLabel borrowedTitle = new JLabel("پرطرفدارترین کتاب‌ها", JLabel.CENTER);
    borrowedTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
    borrowedTitle.setForeground(Color.BLACK);
    borrowedHeaderPanel.add(borrowedTitle, BorderLayout.CENTER);
    
    JButton exportBorrowedCsvButton = createStyledButton("خروجی CSV", new Color(52, 152, 219));
    exportBorrowedCsvButton.setFont(new Font("Tahoma", Font.BOLD, 14));
    exportBorrowedCsvButton.addActionListener(e -> exportMostBorrowedToCsv());
    borrowedHeaderPanel.add(exportBorrowedCsvButton, BorderLayout.EAST);
    
    borrowedPanel.add(borrowedHeaderPanel, BorderLayout.NORTH);
    
    mostBorrowedTableModel = new DefaultTableModel(
            new Object[]{"عنوان", "نویسنده", "تعداد امانت"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    mostBorrowedTable = new JTable(mostBorrowedTableModel);
    mostBorrowedTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    mostBorrowedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mostBorrowedTable.setRowHeight(35);
    mostBorrowedTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
    mostBorrowedTable.setBackground(Color.WHITE);
    mostBorrowedTable.setForeground(Color.BLACK);
    mostBorrowedTable.setGridColor(new Color(236, 240, 241));
    mostBorrowedTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    mostBorrowedTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
    JTableHeader mHeader = mostBorrowedTable.getTableHeader();
    mHeader.setBackground(HEADER_COLOR);
    mHeader.setForeground(Color.BLACK);
    mHeader.setFont(new Font("Tahoma", Font.BOLD, 15));
    mHeader.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    ((DefaultTableCellRenderer)mHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    setColumnSizes(mostBorrowedTable, new int[]{150, 120, 80});
    borrowedPanel.add(new JScrollPane(mostBorrowedTable), BorderLayout.CENTER);

    // ===== پنل دانشجویان دارای دیرکرد =====
    JPanel overduePanel = new JPanel(new BorderLayout(5, 5));
    overduePanel.setBackground(Color.WHITE);
    overduePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
        new EmptyBorder(10, 10, 10, 10)
    ));
    overduePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    
    JPanel overdueHeaderPanel = new JPanel(new BorderLayout(5, 5));
    overdueHeaderPanel.setBackground(Color.WHITE);
    overdueHeaderPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    
    JLabel overdueTitle = new JLabel("دانشجویان دارای دیرکرد", JLabel.CENTER);
    overdueTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
    overdueTitle.setForeground(Color.BLACK);
    overdueHeaderPanel.add(overdueTitle, BorderLayout.CENTER);
    
    JButton exportOverdueCsvButton = createStyledButton("خروجی CSV", new Color(52, 152, 219));
    exportOverdueCsvButton.setFont(new Font("Tahoma", Font.BOLD, 14));
    exportOverdueCsvButton.addActionListener(e -> exportOverdueStudentsToCsv());
    overdueHeaderPanel.add(exportOverdueCsvButton, BorderLayout.EAST);
    
    overduePanel.add(overdueHeaderPanel, BorderLayout.NORTH);
    
    overdueStudentsTableModel = new DefaultTableModel(
            new Object[]{"نام دانشجو", "شماره دانشجویی", "تعداد دیرکردها", "مجموع بدهی"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    overdueStudentsTable = new JTable(overdueStudentsTableModel);
    overdueStudentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    overdueStudentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    overdueStudentsTable.setRowHeight(35);
    overdueStudentsTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
    overdueStudentsTable.setBackground(Color.WHITE);
    overdueStudentsTable.setForeground(Color.BLACK);
    overdueStudentsTable.setGridColor(new Color(236, 240, 241));
    overdueStudentsTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    overdueStudentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
    JTableHeader oHeader = overdueStudentsTable.getTableHeader();
    oHeader.setBackground(HEADER_COLOR);
    oHeader.setForeground(Color.BLACK);
    oHeader.setFont(new Font("Tahoma", Font.BOLD, 15));
    oHeader.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    ((DefaultTableCellRenderer)oHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    setColumnSizes(overdueStudentsTable, new int[]{120, 100, 90, 80});
    overduePanel.add(new JScrollPane(overdueStudentsTable), BorderLayout.CENTER);

    tablesPanel.add(borrowedPanel);
    tablesPanel.add(overduePanel);

    panel.add(summaryPanel, BorderLayout.NORTH);
    panel.add(tablesPanel, BorderLayout.CENTER);

    refreshReports();

    return panel;
}

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

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.BOLD, 16));
        label.setForeground(Color.BLACK);
        label.setBorder(new EmptyBorder(5, 15, 5, 15));
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return label;
    }

    private void setColumnSizes(JTable table, int[] widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

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

    private void refreshAllData() {
        bookRepository.load();
        loanRepository.load();
        reservationRepository.load();
        userRepository.load();
    }

    private void refreshBooksTable() {
        List<Book> books = bookService.getAllBooks();
        booksTableModel.setRowCount(0);
        for (Book book : books) {
            booksTableModel.addRow(new Object[]{
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
    }

    private void searchBooks() {
        String keyword = booksSearchField.getText().trim();
        String criteria = (String) booksSearchCombo.getSelectedItem();

        if (keyword.isEmpty()) {
            refreshBooksTable();
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
                            .collect(java.util.stream.Collectors.toList());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "لطفاً یک سال معتبر وارد کنید.",
                            "خطا",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                break;
        }

        booksTableModel.setRowCount(0);
        for (Book book : results) {
            booksTableModel.addRow(new Object[]{
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
    }

    private void addBook() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        BookDialog dialog = new BookDialog(parent, bookService);
        dialog.setVisible(true);

        // if (dialog.isConfirmed()) {
        //     refreshBooksTable();
        //     JOptionPane.showMessageDialog(
        //             this,
        //             "کتاب با موفقیت اضافه شد."
        //     );
        // }
    }

    private void editBook() {
        int row = booksTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً یک کتاب را برای ویرایش انتخاب کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String isbn = booksTableModel.getValueAt(row, 1).toString();
        Book book = bookService.findByIsbn(isbn);

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        BookDialog dialog = new BookDialog(parent, book, bookService);
        dialog.setVisible(true);

        // if (dialog.isConfirmed()) {
        //     refreshBooksTable();
        //     JOptionPane.showMessageDialog(
        //             this,
        //             "کتاب با موفقیت به‌روزرسانی شد."
        //     );
        // }
    }

    private void deleteBook() {
        int row = booksTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً یک کتاب را برای حذف انتخاب کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String isbn = booksTableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "آیا از حذف این کتاب مطمئن هستید؟",
                "تأیید حذف",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookService.deleteBook(isbn);
                refreshBooksTable();
                JOptionPane.showMessageDialog(
                        this,
                        "کتاب با موفقیت حذف شد."
                );
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "خطا",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void refreshReservationsTable() {
        List<Reservation> reservations = reservationService.getAllReservations();
        reservationsTableModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            reservationsTableModel.addRow(new Object[]{
                    reservation.getStudent().getFirstName() + " " + reservation.getStudent().getLastName(),
                    reservation.getStudent().getStudentId(),
                    reservation.getBook().getTitle(),
                    reservation.getRequestDate(),
                    reservation.isApproved() ? "تأیید شده" : "در انتظار"
            });
        }
    }

    private void approveReservation() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً یک درخواست رزرو را برای تأیید انتخاب کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE            );
            return;
        }

        String studentId = reservationsTableModel.getValueAt(row, 1).toString();
        String bookTitle = reservationsTableModel.getValueAt(row, 2).toString();

        try {
            Student student = userRepository.findStudent(studentId);
            Book book = bookService.getAllBooks().stream()
                    .filter(b -> b.getTitle().equals(bookTitle))
                    .findFirst()
                    .orElse(null);

            if (student != null && book != null) {
                reservationService.approveReservation(student, book);
                refreshReservationsTable();
                JOptionPane.showMessageDialog(
                        this,
                        "درخواست رزرو با موفقیت تأیید شد."
                );
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void rejectReservation() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً یک درخواست رزرو را برای رد انتخاب کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String studentId = reservationsTableModel.getValueAt(row, 1).toString();
        String bookTitle = reservationsTableModel.getValueAt(row, 2).toString();

        try {
            Student student = userRepository.findStudent(studentId);
            Book book = bookService.getAllBooks().stream()
                    .filter(b -> b.getTitle().equals(bookTitle))
                    .findFirst()
                    .orElse(null);

            if (student != null && book != null) {
                reservationService.rejectReservation(student, book);
                refreshReservationsTable();
                JOptionPane.showMessageDialog(
                        this,
                        "درخواست رزرو با موفقیت رد شد."
                );
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshExtensionsTable() {
        List<Loan> extensionRequests = loanService.getExtensionRequests();
        extensionsTableModel.setRowCount(0);

        for (Loan loan : extensionRequests) {
            extensionsTableModel.addRow(new Object[]{
                    loan.getStudent().getFirstName() + " " + loan.getStudent().getLastName(),
                    loan.getBook().getTitle(),
                    loan.getLoanDate(),
                    loan.getDueDate(),
                    "در انتظار تأیید"
            });
        }
    }

    private void approveExtension() {
        int row = extensionsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "لطفاً یک درخواست تمدید را انتخاب کنید.",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "آیا از تأیید این درخواست تمدید مطمئن هستید؟",
                "تأیید",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Loan loan = loanService.getExtensionRequests().stream()
                        .filter(l -> l.getBook().getTitle().equals(extensionsTableModel.getValueAt(row, 1)))
                        .findFirst()
                        .orElse(null);

                if (loan != null) {
                    loanService.approveExtension(
                            loan.getBook().getIsbn(),
                            loan.getStudent().getStudentId()
                    );
                    refreshExtensionsTable();
                    JOptionPane.showMessageDialog(
                            this,
                            "درخواست تمدید با موفقیت تأیید شد."
                    );
                }
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "خطا",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void refreshStudentsTable() {
        List<Student> students = userRepository.getStudents();
        studentsTableModel.setRowCount(0);

        for (Student student : students) {
            int borrowedCount = loanService.getActiveLoansByStudent(student).size();
            int debt = calculateDebt(student);
            studentsTableModel.addRow(new Object[]{
                    student.getFirstName(),
                    student.getLastName(),
                    student.getStudentId(),
                    student.getEmail(),
                    borrowedCount,
                    debt
            });
        }
    }

    private int calculateDebt(Student student) {
        return (int) loanService.getActiveLoansByStudent(student).stream()
                .filter(loan -> loan.isReturned() || loan.isOverdue())
                .mapToInt(loan -> loan.calculateFine(dailyFine))
                .sum();
    }

    private void refreshReports() {
        List<Book> allBooks = bookService.getAllBooks();
        int totalBooks = allBooks.stream().mapToInt(Book::getTotalCopies).sum();
        int availableBooks = allBooks.stream().mapToInt(Book::getAvailableCopies).sum();
        int borrowedBooks = allBooks.stream().mapToInt(b -> b.getTotalCopies() - b.getAvailableCopies()).sum();

        totalBooksLabel.setText("تعداد کل کتاب‌ها: " + totalBooks);
        availableBooksLabel.setText("کتاب‌های موجود: " + availableBooks);
        borrowedBooksLabel.setText("کتاب‌های امانت داده شده: " + borrowedBooks);

        mostBorrowedTableModel.setRowCount(0);
        overdueStudentsTableModel.setRowCount(0);

        List<Loan> allLoans = loanService.getAllLoans();
        java.util.Map<String, Long> borrowCount = allLoans.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        loan -> loan.getBook().getIsbn(),
                        java.util.stream.Collectors.counting()
                ));

        allBooks.stream()
                .filter(book -> borrowCount.containsKey(book.getIsbn()))
                .sorted((b1, b2) -> Long.compare(
                        borrowCount.get(b2.getIsbn()),
                        borrowCount.get(b1.getIsbn())
                ))
                .forEach(book -> mostBorrowedTableModel.addRow(new Object[]{
                        book.getTitle(),
                        book.getAuthor(),
                        borrowCount.get(book.getIsbn())
                }));

        userRepository.getStudents().stream()
                .filter(student -> {
                    long overdueCount = loanService.getActiveLoansByStudent(student).stream()
                                .filter(loan -> loan.isOverdue() || loan.isReturned() && loan.calculateFine(dailyFine) > 0)
                            .count();
                    return overdueCount > 0;
                })
                .forEach(student -> overdueStudentsTableModel.addRow(new Object[]{
                        student.getFirstName() + " " + student.getLastName(),
                        student.getStudentId(),
                        loanService.getActiveLoansByStudent(student).stream()
                            .filter(loan -> loan.isOverdue() || loan.isReturned() && loan.calculateFine(dailyFine) > 0)
                                .count(),
                        calculateDebt(student)
                }));
    }

// ===== متدهای خروجی CSV =====

private void exportMostBorrowedToCsv() {
    try {
        // انتخاب مسیر ذخیره فایل
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("ذخیره گزارش کتاب‌های پرطرفدار");
        fileChooser.setSelectedFile(new File("پرطرفدارترین_کتاب‌ها.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".csv")) {
            filePath += ".csv";
        }
        
        // دریافت داده‌ها از جدول
        List<String[]> data = new ArrayList<>();
        
        // اضافه کردن سرستون‌ها
        data.add(new String[]{"عنوان کتاب", "نویسنده", "تعداد امانت"});
        
        // اضافه کردن ردیف‌های داده
        for (int i = 0; i < mostBorrowedTableModel.getRowCount(); i++) {
            String title = mostBorrowedTableModel.getValueAt(i, 0).toString();
            String author = mostBorrowedTableModel.getValueAt(i, 1).toString();
            String borrowCount = mostBorrowedTableModel.getValueAt(i, 2).toString();
            data.add(new String[]{title, author, borrowCount});
        }
        
        // نوشتن فایل CSV
        writeCsvFile(filePath, data);
        
        JOptionPane.showMessageDialog(
            this,
            "گزارش با موفقیت در مسیر زیر ذخیره شد:\n" + filePath,
            "موفقیت",
            JOptionPane.INFORMATION_MESSAGE
        );
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "خطا در ذخیره فایل: " + ex.getMessage(),
            "خطا",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

private void exportOverdueStudentsToCsv() {
    try {
        // انتخاب مسیر ذخیره فایل
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("ذخیره گزارش دانشجویان دارای دیرکرد");
        fileChooser.setSelectedFile(new File("دانشجویان_دارای_دیرکرد.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".csv")) {
            filePath += ".csv";
        }
        
        // دریافت داده‌ها از جدول
        List<String[]> data = new ArrayList<>();
        
        // اضافه کردن سرستون‌ها
        data.add(new String[]{"نام دانشجو", "شماره دانشجویی", "تعداد دیرکردها", "مجموع بدهی (ریال)"});
        
        // اضافه کردن ردیف‌های داده
        for (int i = 0; i < overdueStudentsTableModel.getRowCount(); i++) {
            String name = overdueStudentsTableModel.getValueAt(i, 0).toString();
            String studentId = overdueStudentsTableModel.getValueAt(i, 1).toString();
            String overdueCount = overdueStudentsTableModel.getValueAt(i, 2).toString();
            String debt = overdueStudentsTableModel.getValueAt(i, 3).toString();
            data.add(new String[]{name, studentId, overdueCount, debt});
        }
        
        // نوشتن فایل CSV
        writeCsvFile(filePath, data);
        
        JOptionPane.showMessageDialog(
            this,
            "گزارش با موفقیت در مسیر زیر ذخیره شد:\n" + filePath,
            "موفقیت",
            JOptionPane.INFORMATION_MESSAGE
        );
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "خطا در ذخیره فایل: " + ex.getMessage(),
            "خطا",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

private void writeCsvFile(String filePath, List<String[]> data) throws IOException {
    try (java.io.PrintWriter writer = new java.io.PrintWriter(
            new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath), "UTF-8"))) {
        
        for (String[] row : data) {
            // جایگزینی کاما با ویرگول برای جلوگیری از مشکلات CSV
            String[] escapedRow = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                // اگر مقدار شامل کاما یا نقل قول باشه، داخل نقل قول قرار می‌دیم
                String value = row[i];
                if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                    value = value.replace("\"", "\"\"");
                    value = "\"" + value + "\"";
                }
                escapedRow[i] = value;
            }
            writer.println(String.join(",", escapedRow));
        }
        
        writer.flush();
    }
}
}