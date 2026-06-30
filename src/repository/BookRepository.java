package src.repository;

import src.model.Book;
import src.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing books.
 */
public class BookRepository {

    private static final String FILE_NAME = "books.dat";

    private List<Book> books;

    // Constructor - initializes the book list and loads data from file
    public BookRepository() {
        books = new ArrayList<>();
        load();
    }

    // Adds a new book to the repository and saves to file
    public void add(Book book) {
        books.add(book);

        save();
    }

    // Removes a book by ISBN and saves to file
    public boolean remove(String isbn) {

        // Find and remove the book with matching ISBN
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                books.remove(book);
                save();
                return true;
            }
        }
        save();
        return false;
    }

    // Searches for books by title (case-insensitive partial match)
    public List<Book> searchByTitle(String title) {

        List<Book> result = new ArrayList<>();

        for (Book book : books) {

            if (book.getTitle().toLowerCase()
                    .contains(title.toLowerCase())) {

                result.add(book);
            }
        }

        return result;
    }

    // Searches for books by author (case-insensitive partial match)
    public List<Book> searchByAuthor(String author) {

        List<Book> result = new ArrayList<>();

        for (Book book : books) {

            if (book.getAuthor().toLowerCase()
                    .contains(author.toLowerCase())) {

                result.add(book);
            }
        }

        return result;
    }

    // Searches for books by category (case-insensitive partial match)
    public List<Book> searchByCategory(String category) {

        List<Book> result = new ArrayList<>();

        for (Book book : books) {

            if (book.getCategory().toLowerCase()
                    .contains(category.toLowerCase())) {

                result.add(book);
            }
        }

        return result;
    }

    // Saves the current book list to file using serialization
    public void save() {

        try {
            FileUtil.save(FILE_NAME, books);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Loads the book list from file using deserialization
    @SuppressWarnings("unchecked")
    public void load() {

        File file = new File(FILE_NAME);

        // If file doesn't exist, return with empty list
        if (!file.exists()) {
            return;
        }

        try {
            books = (List<Book>) FileUtil.load(FILE_NAME);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            books = new ArrayList<>();
        }

    }

    // Returns all books in the repository
    public List<Book> getBooks() {
        return books;
    }

    // Finds a book by its ISBN
    public Book findByIsbn(String isbn) {

        for (Book book : books) {

            if (book.getIsbn().equals(isbn)) {
                return book;
            }

        }

        return null;
    }

}