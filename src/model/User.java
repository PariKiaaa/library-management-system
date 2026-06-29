package src.model;

import java.io.Serializable;

/**
 * Abstract base class for all users of the library system.
 */
public abstract class User implements Serializable {
    private String firstName, lastName, email, password;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "کاربر{" +
                "نام='" + firstName + '\'' +
                "، نام خانوادگی='" + lastName + '\'' +
                "، ایمیل='" + email + '\'' +
                '}';
    }
}