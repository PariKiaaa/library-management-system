package src.model;

// import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a librarian in the library system.
 */
public class Librarian extends User {

    private String personnelCode;

    // Constructor with all fields
    public Librarian(String firstName, String lastName, String email, String password, String personnelCode) {
        super(firstName, lastName, email, password);
        this.personnelCode = personnelCode;
    }

    // Getter and setter for personnel code
    public String getPersonnelCode() {
        return personnelCode;
    }

    public void setPersonnelCode(String personnelCode) {
        this.personnelCode = personnelCode;
    }

    // Equality check based on personnel code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Librarian)) return false;
        Librarian librarian = (Librarian) o;
        return Objects.equals(personnelCode, librarian.personnelCode);
    }

    // Hash code based on personnel code
    @Override
    public int hashCode() {
        return Objects.hash(personnelCode);
    }

    // String representation of the librarian
    @Override
    public String toString() {
        return "کتابدار{" +
                "نام='" + getFirstName() + '\'' +
                "، نام خانوادگی='" + getLastName() + '\'' +
                "، ایمیل='" + getEmail() + '\'' +
                "، کد پرسنلی='" + personnelCode + '\'' +
                '}';
    }
}