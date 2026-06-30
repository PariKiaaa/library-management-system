package src.service;

import src.exception.BookNotAvailableException;
import src.exception.ValidationException;
import src.model.Book;
import src.model.Loan;
import src.model.Student;
import src.repository.BookRepository;
import src.repository.LoanRepository;

import java.time.LocalDate;
import java.util.List;

// Service class for managing book loans (borrowing, returning, extending)
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    // Default constructor
    public LoanService() {
        this(new BookRepository(), new LoanRepository());
    }

    // Constructor with dependency injection
    public LoanService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    // Borrow a book for a student
    public Loan borrowBook(Student student, String isbn, LocalDate loanDate)
            throws ValidationException, BookNotAvailableException {
        // Validate input parameters
        validateBorrowInput(student, isbn, loanDate);

        // Find the book by ISBN
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new ValidationException("کتاب یافت نشد.");
        }

        // Check if any copies are available
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("نسخه‌ای از این کتاب موجود نیست.");
        }

        // Check if student already has this book borrowed
        List<Loan> activeLoans = loanRepository.getLoansByStudent(student);
        for (Loan loan : activeLoans) {
            if (loan.getBook().getIsbn().equals(isbn)) {
                throw new ValidationException("شما قبلاً این کتاب را امانت گرفته‌اید.");
            }
        }

        // Decrease available copies and create loan
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan loan = new Loan(student, book, loanDate);
        loanRepository.add(loan);
        bookRepository.save();

        return loan;
    }

    // Return a borrowed book
    public void returnBook(String isbn, String studentId) throws ValidationException {
        // Validate input parameters
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("شماره دانشجویی نمی‌تواند خالی باشد.");
        }

        // Find the active loan
        Loan loan = findActiveLoan(isbn, studentId);
        loan.returnBook();

        // Increase available copies
        Book book = bookRepository.findByIsbn(isbn);
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        // Save changes
        loanRepository.save();
        bookRepository.save();

        // Process any pending reservations for this book
        ReservationService reservationService = new ReservationService();
        try {
            reservationService.processApprovedReservation(loan.getBook(), true);
        } catch (Exception e) {
            // Silently handle any errors during reservation processing
        }
    }

    // Request extension for a loan
    public void extendLoan(String isbn, String studentId) throws ValidationException {
        // Validate input parameters
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("شماره دانشجویی نمی‌تواند خالی باشد.");
        }

        // Find active loan and request extension
        Loan loan = findActiveLoan(isbn, studentId);
        loan.extend();
        loanRepository.save();
    }

    // Approve an extension request
    public void approveExtension(String isbn, String studentId) throws ValidationException {
        // Validate input parameters
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("شماره دانشجویی نمی‌تواند خالی باشد.");
        }

        // Find active loan and approve extension
        Loan loan = findActiveLoan(isbn, studentId);
        loan.approveExtension();
        loanRepository.save();
    }

    // Get all extension requests
    public List<Loan> getExtensionRequests() {
        return loanRepository.getLoans().stream()
                .filter(loan -> !loan.isReturned() && loan.isExtensionRequested())
                .collect(java.util.stream.Collectors.toList());
    }

    // Calculate fine for an overdue loan
    public int calculateFine(String isbn, String studentId, int dailyFine) throws ValidationException {
        // Validate input parameters
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("شماره دانشجویی نمی‌تواند خالی باشد.");
        }
        if (dailyFine <= 0) {
            throw new ValidationException("جریمه روزانه باید مثبت باشد.");
        }

        // Find active loan and calculate fine
        Loan loan = findActiveLoan(isbn, studentId);
        return loan.calculateFine(dailyFine);
    }

    // Get active loans for a specific student
    public List<Loan> getActiveLoansByStudent(Student student) {
        return loanRepository.getLoansByStudent(student);
    }

    // Get active loans for a specific book
    public List<Loan> getActiveLoansByBook(Book book) {
        return loanRepository.getLoansByBook(book);
    }

    // Get all loans
    public List<Loan> getAllLoans() {
        return loanRepository.getLoans();
    }

    // Find an active loan by ISBN and student ID
    private Loan findActiveLoan(String isbn, String studentId) throws ValidationException {
        for (Loan loan : loanRepository.getLoans()) {
            if (loan.getBook().getIsbn().equals(isbn)
                    && loan.getStudent().getStudentId().equals(studentId)
                    && !loan.isReturned()) {
                return loan;
            }
        }
        throw new ValidationException("امانت فعال یافت نشد.");
    }

    // Validate borrowing input parameters
    private void validateBorrowInput(Student student, String isbn, LocalDate loanDate) throws ValidationException {
        if (student == null) {
            throw new ValidationException("دانشجو نمی‌تواند خالی باشد.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }
        if (loanDate == null) {
            throw new ValidationException("تاریخ امانت نمی‌تواند خالی باشد.");
        }
    }
}