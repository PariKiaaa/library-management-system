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

public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService() {
        this(new BookRepository(), new LoanRepository());
    }

    public LoanService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    public Loan borrowBook(Student student, String isbn, LocalDate loanDate)
            throws ValidationException, BookNotAvailableException {
        validateBorrowInput(student, isbn, loanDate);

        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new ValidationException("Book not found.");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No available copies for this book.");
        }

        List<Loan> activeLoans = loanRepository.getLoansByStudent(student);
        for (Loan loan : activeLoans) {
            if (loan.getBook().getIsbn().equals(isbn)) {
                throw new ValidationException("You already have an active loan for this book.");
            }
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan loan = new Loan(student, book, loanDate);
        loanRepository.add(loan);
        bookRepository.save();

        return loan;
    }

    public void returnBook(String isbn, String studentId) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty.");
        }

        Loan loan = findActiveLoan(isbn, studentId);
        loan.returnBook();
        loanRepository.save();
        bookRepository.save();
    }

    public void extendLoan(String isbn, String studentId) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty.");
        }

        Loan loan = findActiveLoan(isbn, studentId);
        loan.extend();
        loanRepository.save();
    }

    public void approveExtension(String isbn, String studentId) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty.");
        }

        Loan loan = findActiveLoan(isbn, studentId);
        loan.approveExtension();
        loanRepository.save();
    }

    public List<Loan> getExtensionRequests() {
        return loanRepository.getLoans().stream()
                .filter(loan -> !loan.isReturned() && loan.isExtensionRequested())
                .collect(java.util.stream.Collectors.toList());
    }

    public int calculateFine(String isbn, String studentId, int dailyFine) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty.");
        }
        if (dailyFine <= 0) {
            throw new ValidationException("Daily fine must be positive.");
        }

        Loan loan = findActiveLoan(isbn, studentId);
        return loan.calculateFine(dailyFine);
    }

    public List<Loan> getActiveLoansByStudent(Student student) {
        return loanRepository.getLoansByStudent(student);
    }

    public List<Loan> getActiveLoansByBook(Book book) {
        return loanRepository.getLoansByBook(book);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.getLoans();
    }

    private Loan findActiveLoan(String isbn, String studentId) throws ValidationException {
        for (Loan loan : loanRepository.getLoans()) {
            if (loan.getBook().getIsbn().equals(isbn)
                    && loan.getStudent().getStudentId().equals(studentId)
                    && !loan.isReturned()) {
                return loan;
            }
        }
        throw new ValidationException("Active loan not found.");
    }

    private void validateBorrowInput(Student student, String isbn, LocalDate loanDate) throws ValidationException {
        if (student == null) {
            throw new ValidationException("Student cannot be null.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }
        if (loanDate == null) {
            throw new ValidationException("Loan date cannot be null.");
        }
    }
}