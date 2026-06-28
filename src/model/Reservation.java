package src.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a reservation request for a book.
 */
public class Reservation implements Serializable {
    private Student student;
    private Book book;
    private LocalDate requestDate;
    private boolean approved;

    public Reservation(Student student, Book book, LocalDate requestDate, boolean approved) {
        this.student = student;
        this.book = book;
        this.requestDate = requestDate;
        this.approved = approved;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "student=" + student.getStudentId() +
                ", book=" + book.getTitle() +
                ", requestDate=" + requestDate +
                ", approved=" + approved +
                '}';
    }
}