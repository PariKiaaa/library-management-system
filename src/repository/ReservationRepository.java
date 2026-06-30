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

    // Constructor - initializes the reservation list and loads data from file
    public ReservationRepository() {
        reservations = new ArrayList<>();
        load();
    }

    // Adds a new reservation to the repository and saves to file
    public void add(Reservation reservation) {
        reservations.add(reservation);
        save();
    }

    // Saves the current reservation list to file using serialization
    public void save() {

        try {
            FileUtil.save(FILE_NAME, reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Loads the reservation list from file using deserialization
    @SuppressWarnings("unchecked")
    public void load() {

        File file = new File(FILE_NAME);

        // If file doesn't exist, return with empty list
        if (!file.exists()) {
            return;
        }

        try {

            reservations = (List<Reservation>) FileUtil.load(FILE_NAME);

        } catch (IOException | ClassNotFoundException e) {

            reservations = new ArrayList<>();

        }

    }

    // Finds a pending (not approved) reservation for a specific student and book
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

    // Removes a reservation from the repository and saves to file
    public void remove(Reservation reservation) {
        reservations.remove(reservation);
        save();
    }

    // Returns all reservations in the repository
    public List<Reservation> getReservations() {
        return reservations;
    }
}