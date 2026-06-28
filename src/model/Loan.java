package src.model;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a book loan in the library system.
 */
public class Loan implements Serializable {
        
    private static final int BASE_LOAN_DAYS = 7;
    private static final int EXTENSION_DAYS = 7;

    private Student student;
    private Book book;
    private LocalDate loanDate, dueDate, actualReturnDate, extensionDate;
    private boolean extended;
    private boolean extensionRequested;

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
    public void setLoanDate(LocalDate loanDate) { 
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(BASE_LOAN_DAYS);
    }

/**
      * Requests an extension for the loan (librarian must approve).
      * @throws IllegalStateException if already extended
      */
    public void extend() {
        if (extended) {
            throw new IllegalStateException("This loan has already been extended");
        }
        this.extensionRequested = true;
    }

    /**
      * Approves a pending extension request.
      * @throws IllegalStateException if no extension was requested
      */
    public void approveExtension() {
        if (!extensionRequested) {
            throw new IllegalStateException("No extension request pending");
        }
        this.extended = true;
        this.extensionRequested = false;
        this.extensionDate = LocalDate.now();
        this.dueDate = this.dueDate.plusDays(EXTENSION_DAYS);
    }

    /**
     * Marks the book as returned.
     */
    public void returnBook() {
        if (actualReturnDate != null) {
            throw new IllegalStateException("Book already returned");
        }
        this.actualReturnDate = LocalDate.now();
        // Increase available copies
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

    public boolean isReturned() {
        return actualReturnDate != null;
    }

    public boolean isOverdue() {
        return !isReturned() && LocalDate.now().isAfter(dueDate);
    }

    public long getLateDays() {
        if (isReturned()) {
            return Math.max(0, ChronoUnit.DAYS.between(dueDate, actualReturnDate));
        }
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
    }

    /**
     * Calculates the late fine.
     * @param dailyFine fine per day
     * @return total fine in Rials
     */
    public int calculateFine(int dailyFine) {
        if (dailyFine <= 0) {
            throw new IllegalArgumentException("Daily fine must be positive");
        }
        
        if (!isReturned()) {
            return 0;  // Not returned yet, fine will be calculated when returned
        }

        long lateDays = getLateDays();
        return (int) (lateDays * dailyFine);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "student=" + Objects.toString(student != null ? student.getStudentId() : "null") +
                ", book=" + Objects.toString(book != null ? book.getTitle() : "null") +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", actualReturnDate=" + actualReturnDate +
                ", extended=" + extended +
                ", extensionDate=" + extensionDate +
                '}';
    }
}