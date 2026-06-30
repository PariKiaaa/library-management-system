package src.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * Provides methods for validating email, password, student ID, etc.
 */
public final class ValidationUtil {

    // Prevent instantiation
    private ValidationUtil() {
    }

    // Email validation pattern - standard email format
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Student ID pattern - exactly 9 digits
    private static final Pattern STUDENT_ID_PATTERN =
            Pattern.compile("^\\d{9}$");

    // Password pattern - at least 8 characters, must contain:
    // - at least one lowercase letter
    // - at least one uppercase letter
    // - at least one digit
    // - at least one special character
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    // Validates an email address format
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // Validates password strength
    // Requirements: at least 8 characters, includes uppercase, lowercase, digit, and special character
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    // Validates a student ID format - exactly 9 digits
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }
}