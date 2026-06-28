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

    public LoanRepository() {
        loans = new ArrayList<>();
        load();
    }

    public void add(Loan loan) {
        loans.add(loan);
        save();
    }
    /**
     * Saves all loans.
     */
    public void save() {

        try {
            FileUtil.save(FILE_NAME, loans);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads loans from file.
     */
    @SuppressWarnings("unchecked")
    public void load() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try {

            loans = (List<Loan>) FileUtil.load(FILE_NAME);

        } catch (IOException | ClassNotFoundException e) {

            loans = new ArrayList<>();

        }

    }

    /**
     * Returns all loans.
     */
    public List<Loan> getLoans() {
        return loans;
    }

    /**
     * Returns all active loans of a student.
     */
    public List<Loan> getLoansByStudent(Student student) {

        List<Loan> result = new ArrayList<>();

        for (Loan loan : loans) {

            if (loan.getStudent().equals(student) && !loan.isReturned()) {
                result.add(loan);
            }

        }

        return result;
    }

    /**
     * Returns all active loans of a book.
     */
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