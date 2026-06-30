package src.repository;

import src.model.Book;
import src.model.Reservation;
import src.model.Student;
import src.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing book reservations.
 */
public class ReservationRepository {

    private static final String FILE_NAME = "reservations.dat";

    private List<Reservation> reservations;

    public ReservationRepository() {
        reservations = new ArrayList<>();
        load();
    }

    public void add(Reservation reservation) {
        reservations.add(reservation);
        save();
    }
    /**
     * Saves reservations to file.
     */
    public void save() {

        try {
            FileUtil.save(FILE_NAME, reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads reservations from file.
     */
    @SuppressWarnings("unchecked")
    public void load() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try {

            reservations = (List<Reservation>) FileUtil.load(FILE_NAME);

        } catch (IOException | ClassNotFoundException e) {

            reservations = new ArrayList<>();

        }

    }

    public Reservation findPendingReservation(Student student, Book book) {

        for (Reservation reservation : reservations) {

            if (reservation.getStudent().equals(student)
                    && reservation.getBook().equals(book)
                    && !reservation.isApproved()) {

                return reservation;
            }

        }

        return null;
    }

    public void remove(Reservation reservation) {
        reservations.remove(reservation);
        save();
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}