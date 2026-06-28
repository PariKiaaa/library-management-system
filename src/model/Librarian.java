package src.model;

// import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a librarian in the library system.
 */
public class Librarian extends User {

    private String personnelCode;

    public Librarian(String firstName, String lastName, String email, String password, String personnelCode) {
        super(firstName, lastName, email, password);
        this.personnelCode = personnelCode;
    }

    public String getPersonnelCode() {
        return personnelCode;
    }

    public void setPersonnelCode(String personnelCode) {
        this.personnelCode = personnelCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Librarian)) return false;
        Librarian librarian = (Librarian) o;
        return Objects.equals(personnelCode, librarian.personnelCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personnelCode);
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", personnelCode='" + personnelCode + '\'' +
                '}';
    }
}