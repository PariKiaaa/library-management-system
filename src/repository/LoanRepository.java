package src.repository;

import src.model.Book;
import src.model.Loan;
import src.model.Student;
import src.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing book loans.
 */
public class LoanRepository {

    private static final String FILE_NAME = "loans.dat";

    private List<Loan> loans;

    // Constructor - initializes the loan list and loads data from file
    public LoanRepository() {
        loans = new ArrayList<>();
        load();
    }

    // Adds a new loan to the repository and saves to file
    public void add(Loan loan) {
        loans.add(loan);
        save();
    }

    // Saves the current loan list to file using serialization
    public void save() {

        try {
            FileUtil.save(FILE_NAME, loans);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Loads the loan list from file using deserialization
    @SuppressWarnings("unchecked")
    public void load() {

        File file = new File(FILE_NAME);

        // If file doesn't exist, return with empty list
        if (!file.exists()) {
            return;
        }

        try {

            loans = (List<Loan>) FileUtil.load(FILE_NAME);

        } catch (IOException | ClassNotFoundException e) {

            loans = new ArrayList<>();

        }

    }

    // Returns all loans in the repository
    public List<Loan> getLoans() {
        return loans;
    }

    // Returns all active loans (not returned) for a specific student
    public List<Loan> getLoansByStudent(Student student) {

        List<Loan> result = new ArrayList<>();

        for (Loan loan : loans) {

            if (loan.getStudent().equals(student) && !loan.isReturned()) {
                result.add(loan);
            }

        }

        return result;
    }

    // Returns all active loans (not returned) for a specific book
    public List<Loan> getLoansByBook(Book book) {

        List<Loan> result = new ArrayList<>();

        for (Loan loan : loans) {

            if (loan.getBook().equals(book) && !loan.isReturned()) {
                result.add(loan);
            }

        }

        return result;
    }

}