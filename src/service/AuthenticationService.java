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

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Registers a new student.
     *
     * @param student student to register
     * @throws ValidationException if validation fails
     * @throws DuplicateUserException if student ID already exists
     */
    public void registerStudent(Student student)
            throws ValidationException, DuplicateUserException {

        if (!ValidationUtil.isValidStudentId(student.getStudentId())) {
            throw new ValidationException("Invalid student ID.");
        }

        if (!ValidationUtil.isValidEmail(student.getEmail())) {
            throw new ValidationException("Invalid email.");
        }

        if (!ValidationUtil.isValidPassword(student.getPassword())) {
            throw new ValidationException("Invalid password.");
        }

        if (userRepository.findStudent(student.getStudentId()) != null) {
            throw new DuplicateUserException("Student ID already exists.");
        }

        userRepository.addStudent(student);
    }

    /**
     * Registers a new librarian.
     *
     * @param librarian librarian to register
     * @throws ValidationException if validation fails
     * @throws DuplicateUserException if personnel code already exists
     */
    public void registerLibrarian(Librarian librarian)
            throws ValidationException, DuplicateUserException {

        if (!ValidationUtil.isValidEmail(librarian.getEmail())) {
            throw new ValidationException("Invalid email.");
        }

        if (!ValidationUtil.isValidPassword(librarian.getPassword())) {
            throw new ValidationException("Invalid password.");
        }

        if (userRepository.findLibrarian(librarian.getPersonnelCode()) != null) {
            throw new DuplicateUserException("Personnel code already exists.");
        }

        userRepository.addLibrarian(librarian);
    }

    /**
     * Logs in a student.
     *
     * @param studentId student ID
     * @param password password
     * @return logged-in student
     * @throws AuthenticationException if login fails
     */
    public Student loginStudent(String studentId, String password)
            throws AuthenticationException {

        Student student = userRepository.findStudent(studentId);

        if (student == null) {
            throw new AuthenticationException("Student not found.");
        }

        if (!student.getPassword().equals(password)) {
            throw new AuthenticationException("Incorrect password.");
        }

        return student;
    }

    /**
     * Logs in a librarian.
     *
     * @param personnelCode personnel code
     * @param password password
     * @return logged-in librarian
     * @throws AuthenticationException if login fails
     */
    public Librarian loginLibrarian(String personnelCode, String password)
            throws AuthenticationException {

        Librarian librarian = userRepository.findLibrarian(personnelCode);

        if (librarian == null) {
            throw new AuthenticationException("Librarian not found.");
        }

        if (!librarian.getPassword().equals(password)) {
            throw new AuthenticationException("Incorrect password.");
        }

        return librarian;
    }

    /**
     * Finds a student by student ID.
     *
     * @param studentId student ID
     * @return student or null if not found
     */
    public Student findStudent(String studentId) {
        return userRepository.findStudent(studentId);
    }

    /**
     * Finds a librarian by personnel code.
     *
     * @param personnelCode personnel code
     * @return librarian or null if not found
     */
    public Librarian findLibrarian(String personnelCode) {
        return userRepository.findLibrarian(personnelCode);
    }

    /**
     * Returns the user repository.
     *
     * @return user repository
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }
}