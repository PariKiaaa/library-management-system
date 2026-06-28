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

    public UserRepository() {
        students = new ArrayList<>();
        librarians = new ArrayList<>();
        load();
    }

    /**
     * Finds a student by student ID.
     */
    public Student findStudent(String studentId) {

        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }

        return null;
    }

    /**
     * Finds a librarian by personnel code.
     */
    public Librarian findLibrarian(String personnelCode) {

        for (Librarian librarian : librarians) {
            if (librarian.getPersonnelCode().equals(personnelCode)) {
                return librarian;
            }
        }

        return null;
    }

    public void addStudent(Student student) {
        students.add(student);
        save();
    }

    public void addLibrarian(Librarian librarian) {
        librarians.add(librarian);
        save();
    }

    /**
     * Saves users.
     */
    public void save() {

        try {
            FileUtil.save(STUDENTS_FILE, students);
            FileUtil.save(LIBRARIANS_FILE, librarians);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads users.
     */
    @SuppressWarnings("unchecked")
    public void load() {

        try {

            File studentFile = new File(STUDENTS_FILE);

            if (studentFile.exists()) {
                students = (List<Student>) FileUtil.load(STUDENTS_FILE);
            }

            File librarianFile = new File(LIBRARIANS_FILE);

            if (librarianFile.exists()) {
                librarians = (List<Librarian>) FileUtil.load(LIBRARIANS_FILE);
            }

        } catch (IOException | ClassNotFoundException e) {
            students = new ArrayList<>();
            librarians = new ArrayList<>();
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Librarian> getLibrarians() {
        return librarians;
    }
}