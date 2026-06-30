package src.model;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a book loan in the library system.
 */
public class Loan implements Serializable {
        
    // Constants for loan duration
    private static final int BASE_LOAN_DAYS = 7;
    private static final int EXTENSION_DAYS = 7;

    // Loan attributes
    private Student student;
    private Book book;
    private LocalDate loanDate, dueDate, actualReturnDate, extensionDate;
    private boolean extended;
    private boolean extensionRequested;

    // Constructor - creates a new loan with base due date
    public Loan(Student student, Book book, LocalDate loanDate) {
        this.student = student;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(BASE_LOAN_DAYS);
        this.actualReturnDate = null;
        this.extended = false;
        this.extensionRequested = false;
        this.extensionDate = null;
    }

    // Getters and Setters
    public Student getStudent() { return student; }
    public Book getBook() { return book; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public LocalDate getExtensionDate() { return extensionDate; }
    public boolean isExtended() { return extended; }
    public boolean isExtensionRequested() { return extensionRequested; }

    public void setStudent(Student student) { this.student = student; }
    public void setBook(Book book) { this.book = book; }
    
    // Updates loan date and recalculates due date
    public void setLoanDate(LocalDate loanDate) { 
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(BASE_LOAN_DAYS);
    }

    // Request extension for this loan
    public void extend() {
        if (extended) {
            throw new IllegalStateException("این امانت قبلاً تمدید شده است");
        }
        this.extensionRequested = true;
    }

    // Approve the extension request
    public void approveExtension() {
        if (!extensionRequested) {
            throw new IllegalStateException("درخواست تمدیدی در انتظار نیست");
        }
        this.extended = true;
        this.extensionRequested = false;
        this.extensionDate = LocalDate.now();
        this.dueDate = this.dueDate.plusDays(EXTENSION_DAYS);
    }

    // Mark the book as returned
    public void returnBook() {
        if (actualReturnDate != null) {
            throw new IllegalStateException("کتاب قبلاً بازگردانده شده است");
        }
        this.actualReturnDate = LocalDate.now();
        // Increase available copies in the book
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

    // Check if the book has been returned
    public boolean isReturned() {
        return actualReturnDate != null;
    }

    // Check if the loan is overdue
    public boolean isOverdue() {
        return !isReturned() && LocalDate.now().isAfter(dueDate);
    }

    // Calculate the number of late days
    public long getLateDays() {
        if (isReturned()) {
            return Math.max(0, ChronoUnit.DAYS.between(dueDate, actualReturnDate));
        }
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
    }

    // Calculate the fine amount based on daily fine rate
    public int calculateFine(int dailyFine) {
        if (dailyFine <= 0) {
            throw new IllegalArgumentException("جریمه روزانه باید مثبت باشد");
        }
        
        if (!isReturned()) {
            return 0;  // Not returned yet, fine will be calculated when returned
        }

        long lateDays = getLateDays();
        return (int) (lateDays * dailyFine);
    }

    // String representation of the loan
    @Override
    public String toString() {
        return "امانت{" +
                "دانشجو=" + Objects.toString(student != null ? student.getStudentId() : "null") +
                "، کتاب=" + Objects.toString(book != null ? book.getTitle() : "null") +
                "، تاریخ امانت=" + loanDate +
                "، تاریخ بازگشت=" + dueDate +
                "، تاریخ بازگشت واقعی=" + actualReturnDate +
                "، تمدید شده=" + extended +
                "، تاریخ تمدید=" + extensionDate +
                '}';
    }
}