package src.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input.
 */
public final class ValidationUtil {

    // Prevent instantiation
    private ValidationUtil() {
    }

    // Simple email validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Exactly 9 digits
    private static final Pattern STUDENT_ID_PATTERN =
            Pattern.compile("^\\d{9}$");

    /*
     * Password must contain:
     * - At least 8 characters
     * - One uppercase letter
     * - One lowercase letter
     * - One digit
     * - One special character
     */
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    /**
     * Checks whether an email address is valid.
     *
     * @param email email address
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks whether a password is valid.
     *
     * @param password password
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Checks whether a student ID is valid.
     *
     * @param studentId student ID
     * @return true if valid
     */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }
}