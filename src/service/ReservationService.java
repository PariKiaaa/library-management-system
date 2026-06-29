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

    public ReservationService() {
        this(new ReservationRepository(), new BookRepository(), new LoanService());
    }

    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              LoanService loanService) {

        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.loanService = loanService;
    }

    /**
     * Creates a new reservation.
     */
    public Reservation createReservation(Student student,
                                         Book book,
                                         LocalDate requestDate)
            throws ValidationException, BookNotAvailableException {

        validateReservationInput(student, book, requestDate);

        Book managedBook = bookRepository.findByIsbn(book.getIsbn());

        if (managedBook == null) {
            throw new ValidationException("کتاب یافت نشد.");
        }

        if (managedBook.getAvailableCopies() > 0) {
            throw new BookNotAvailableException(
                    "کتاب موجود است. به جای رزرو می‌توانید امانت بگیرید."
            );
        }

try {
    findPendingReservation(student, managedBook);
} catch (ValidationException e) {
    // reservation not found
    Reservation reservation =
            new Reservation(student, managedBook, requestDate, false);

    reservationRepository.add(reservation);
    return reservation;
}

throw new ValidationException(
        "شما قبلاً برای این کتاب درخواست رزرو ثبت کرده‌اید."
);
    }

    /**
     * Approves a reservation.
     */
    public void approveReservation(Student student, Book book)
            throws ValidationException {

        Reservation reservation = findPendingReservation(student, book);

        reservation.setApproved(true);

        reservationRepository.save();
    }

    /**
     * Rejects a reservation.
     */
    public void rejectReservation(Student student, Book book)
            throws ValidationException {

        Reservation reservation = findPendingReservation(student, book);

        reservationRepository.remove(reservation);
    }

    /**
     * Converts an approved reservation into a loan.
     */
    public Loan convertToLoan(Student student,
                              Book book,
                              LocalDate loanDate)
            throws ValidationException, BookNotAvailableException {

        Reservation reservation =
                findApprovedReservation(student, book);

        Loan loan =
                loanService.borrowBook(student, book.getIsbn(), loanDate);

        reservationRepository.remove(reservation);

        return loan;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.getReservations();
    }

    /**
     * Finds a pending reservation.
     */
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

    /**
     * Finds an approved reservation.
     */
    private Reservation findApprovedReservation(Student student,
                                                Book book)
            throws ValidationException {

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

    /**
     * Validates reservation input.
     */
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

    public void processApprovedReservation(Book book, boolean is_reurned)
            throws ValidationException, BookNotAvailableException {

        if (book.getAvailableCopies() <= 0 && !is_reurned) {
            return;
        }

        for (Reservation reservation : reservationRepository.getReservations()) {

            if (reservation.getBook().getIsbn().equals(book.getIsbn())
                    && reservation.isApproved()) {

                loanService.borrowBook(
                        reservation.getStudent(),
                        book.getIsbn(),
                        LocalDate.now()
                );

                reservationRepository.remove(reservation);

                return;
            }
        }
    }

}