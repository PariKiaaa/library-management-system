package src.repository;

import src.model.Librarian;
import src.model.Student;
import src.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing users.
 */
public class UserRepository {

    private static final String STUDENTS_FILE = "students.dat";
    private static final String LIBRARIANS_FILE = "librarians.dat";

    private List<Student> students;
    private List<Librarian> librarians;

    // Constructor - initializes user lists and loads data from files
    public UserRepository() {
        students = new ArrayList<>();
        librarians = new ArrayList<>();
        load();
    }

    // Finds a student by their student ID
    public Student findStudent(String studentId) {

        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }

        return null;
    }

    // Finds a librarian by their personnel code
    public Librarian findLibrarian(String personnelCode) {

        for (Librarian librarian : librarians) {
            if (librarian.getPersonnelCode().equals(personnelCode)) {
                return librarian;
            }
        }

        return null;
    }

    // Adds a new student to the repository and saves to file
    public void addStudent(Student student) {
        students.add(student);
        save();
    }

    // Adds a new librarian to the repository and saves to file
    public void addLibrarian(Librarian librarian) {
        librarians.add(librarian);
        save();
    }

    // Saves both student and librarian lists to their respective files
    public void save() {

        try {
            FileUtil.save(STUDENTS_FILE, students);
            FileUtil.save(LIBRARIANS_FILE, librarians);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads both student and librarian lists from their respective files
    @SuppressWarnings("unchecked")
    public void load() {

        try {

            // Load students if file exists
            File studentFile = new File(STUDENTS_FILE);
            if (studentFile.exists()) {
                students = (List<Student>) FileUtil.load(STUDENTS_FILE);
            }

            // Load librarians if file exists
            File librarianFile = new File(LIBRARIANS_FILE);
            if (librarianFile.exists()) {
                librarians = (List<Librarian>) FileUtil.load(LIBRARIANS_FILE);
            }

        } catch (IOException | ClassNotFoundException e) {
            // Initialize with empty lists on error
            students = new ArrayList<>();
            librarians = new ArrayList<>();
        }
    }

    // Returns all students
    public List<Student> getStudents() {
        return students;
    }

    // Returns all librarians
    public List<Librarian> getLibrarians() {
        return librarians;
    }
}