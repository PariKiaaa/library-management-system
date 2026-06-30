package src.service;

import src.exception.AuthenticationException;
import src.exception.DuplicateUserException;
import src.exception.ValidationException;
import src.model.Librarian;
import src.model.Student;
import src.repository.UserRepository;
import src.util.ValidationUtil;

/**
 * Service class for user authentication and registration.
 */
public class AuthenticationService {

    private final UserRepository userRepository;

    // Constructor - initializes the user repository
    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    // Registers a new student after validating input and checking for duplicates
    public void registerStudent(Student student)
            throws ValidationException, DuplicateUserException {

        // Validate student ID format
        if (!ValidationUtil.isValidStudentId(student.getStudentId())) {
            throw new ValidationException("شماره دانشجویی نامعتبر است.");
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(student.getEmail())) {
            throw new ValidationException("ایمیل نامعتبر است.");
        }

        // Validate password strength
        if (!ValidationUtil.isValidPassword(student.getPassword())) {
            throw new ValidationException("رمز عبور نامعتبر است.");
        }

        // Check if student ID already exists
        if (userRepository.findStudent(student.getStudentId()) != null) {
            throw new DuplicateUserException("شماره دانشجویی قبلاً ثبت شده است.");
        }

        // Add student to repository
        userRepository.addStudent(student);
    }

    // Registers a new librarian after validating input and checking for duplicates
    public void registerLibrarian(Librarian librarian)
            throws ValidationException, DuplicateUserException {

        // Validate email format
        if (!ValidationUtil.isValidEmail(librarian.getEmail())) {
            throw new ValidationException("ایمیل نامعتبر است.");
        }

        // Validate password strength
        if (!ValidationUtil.isValidPassword(librarian.getPassword())) {
            throw new ValidationException("رمز عبور نامعتبر است.");
        }

        // Check if personnel code already exists
        if (userRepository.findLibrarian(librarian.getPersonnelCode()) != null) {
            throw new DuplicateUserException("کد پرسنلی قبلاً ثبت شده است.");
        }

        // Add librarian to repository
        userRepository.addLibrarian(librarian);
    }

    // Authenticates a student by verifying credentials
    public Student loginStudent(String studentId, String password)
            throws AuthenticationException {

        // Find student by ID
        Student student = userRepository.findStudent(studentId);

        // Check if student exists
        if (student == null) {
            throw new AuthenticationException("دانشجو یافت نشد.");
        }

        // Verify password
        if (!student.getPassword().equals(password)) {
            throw new AuthenticationException("رمز عبور اشتباه است.");
        }

        return student;
    }

    // Authenticates a librarian by verifying credentials
    public Librarian loginLibrarian(String personnelCode, String password)
            throws AuthenticationException {

        // Find librarian by personnel code
        Librarian librarian = userRepository.findLibrarian(personnelCode);

        // Check if librarian exists
        if (librarian == null) {
            throw new AuthenticationException("کتابدار یافت نشد.");
        }

        // Verify password
        if (!librarian.getPassword().equals(password)) {
            throw new AuthenticationException("رمز عبور اشتباه است.");
        }

        return librarian;
    }

    // Finds a student by their student ID
    public Student findStudent(String studentId) {
        return userRepository.findStudent(studentId);
    }

    // Finds a librarian by their personnel code
    public Librarian findLibrarian(String personnelCode) {
        return userRepository.findLibrarian(personnelCode);
    }

    // Returns the user repository instance
    public UserRepository getUserRepository() {
        return userRepository;
    }
}