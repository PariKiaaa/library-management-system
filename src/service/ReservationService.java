package src.service;

import src.exception.BookNotAvailableException;
import src.exception.ValidationException;
import src.model.Book;
import src.model.Loan;
import src.model.Reservation;
import src.model.Student;
import src.repository.BookRepository;
import src.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing reservations.
 */
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final LoanService loanService;

    // Default constructor
    public ReservationService() {
        this(new ReservationRepository(), new BookRepository(), new LoanService());
    }

    // Constructor with dependency injection
    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              LoanService loanService) {

        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.loanService = loanService;
    }

    // Creates a new reservation for a student
    public Reservation createReservation(Student student,
                                         Book book,
                                         LocalDate requestDate)
            throws ValidationException, BookNotAvailableException {

        // Validate input parameters
        validateReservationInput(student, book, requestDate);

        // Find the book in the repository
        Book managedBook = bookRepository.findByIsbn(book.getIsbn());

        if (managedBook == null) {
            throw new ValidationException("کتاب یافت نشد.");
        }

        // Check if book is available for borrowing instead of reserving
        if (managedBook.getAvailableCopies() > 0) {
            throw new BookNotAvailableException(
                    "کتاب موجود است. به جای رزرو می‌توانید امانت بگیرید."
            );
        }

        // Check if student already has a pending reservation for this book
        try {
            findPendingReservation(student, managedBook);
        } catch (ValidationException e) {
            // No pending reservation found - create a new one
            Reservation reservation =
                    new Reservation(student, managedBook, requestDate, false);

            reservationRepository.add(reservation);
            return reservation;
        }

        // Student already has a pending reservation
        throw new ValidationException(
                "شما قبلاً برای این کتاب درخواست رزرو ثبت کرده‌اید."
        );
    }

    // Approves a pending reservation
    public void approveReservation(Student student, Book book)
            throws ValidationException {

        // Find the pending reservation
        Reservation reservation = findPendingReservation(student, book);

        // Mark as approved
        reservation.setApproved(true);

        reservationRepository.save();
    }

    // Rejects a pending reservation
    public void rejectReservation(Student student, Book book)
            throws ValidationException {

        // Find the pending reservation
        Reservation reservation = findPendingReservation(student, book);

        // Remove the reservation
        reservationRepository.remove(reservation);
    }

    // Converts an approved reservation into a loan
    public Loan convertToLoan(Student student,
                              Book book,
                              LocalDate loanDate)
            throws ValidationException, BookNotAvailableException {

        // Find the approved reservation
        Reservation reservation =
                findApprovedReservation(student, book);

        // Create a loan from the reservation
        Loan loan =
                loanService.borrowBook(student, book.getIsbn(), loanDate);

        // Remove the reservation after conversion
        reservationRepository.remove(reservation);

        return loan;
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        return reservationRepository.getReservations();
    }

    // Finds a pending reservation for a student and book
    private Reservation findPendingReservation(Student student,
                                               Book book)
            throws ValidationException {

        Reservation reservation =
                reservationRepository.findPendingReservation(student, book);

        if (reservation == null) {
            throw new ValidationException(
                    "درخواست رزرو در انتظار یافت نشد."
            );
        }

        return reservation;
    }

    // Finds an approved reservation for a student and book
    private Reservation findApprovedReservation(Student student,
                                                Book book)
            throws ValidationException {

        // Search through all reservations
        for (Reservation reservation :
                reservationRepository.getReservations()) {

            if (reservation.getStudent().getStudentId()
                    .equals(student.getStudentId())
                    &&
                    reservation.getBook().getIsbn()
                            .equals(book.getIsbn())
                    &&
                    reservation.isApproved()) {

                return reservation;
            }

        }

        throw new ValidationException(
                "درخواست رزرو تأیید شده یافت نشد."
        );
    }

    // Validates reservation input parameters
    private void validateReservationInput(Student student,
                                          Book book,
                                          LocalDate requestDate)
            throws ValidationException {

        if (student == null) {
            throw new ValidationException(
                    "دانشجو نمی‌تواند خالی باشد."
            );
        }

        if (book == null) {
            throw new ValidationException(
                    "کتاب نمی‌تواند خالی باشد."
            );
        }

        if (requestDate == null) {
            throw new ValidationException(
                    "تاریخ درخواست نمی‌تواند خالی باشد."
            );
        }

    }

    // Process approved reservations for a book when it becomes available
    public void processApprovedReservation(Book book, boolean is_returned)
            throws ValidationException, BookNotAvailableException {

        // If no copies available and this is not a return, skip processing
        if (book.getAvailableCopies() <= 0 && !is_returned) {
            return;
        }

        // Find and process the first approved reservation for this book
        for (Reservation reservation : reservationRepository.getReservations()) {

            if (reservation.getBook().getIsbn().equals(book.getIsbn())
                    && reservation.isApproved()) {

                // Convert reservation to loan
                loanService.borrowBook(
                        reservation.getStudent(),
                        book.getIsbn(),
                        LocalDate.now()
                );

                // Remove the processed reservation
                reservationRepository.remove(reservation);

                return;
            }
        }
    }

}