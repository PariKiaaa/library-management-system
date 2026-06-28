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

    public BookRepository() {
        books = new ArrayList<>();
        load();
    }

    /**
     * Adds a new book.
     */
    public void add(Book book) {
        books.add(book);

        save();
    }

    /**
     * Removes a book.
     */
    public boolean remove(String isbn) {

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

    /**
     * Searches books by title.
     */
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

    /**
     * Searches books by author.
     */
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

    /**
     * Searches books by category.
     */
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

    /**
     * Saves books to file.
     */
    public void save() {

        try {
            FileUtil.save(FILE_NAME, books);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads books from file.
     */
    @SuppressWarnings("unchecked")
    public void load() {

        File file = new File(FILE_NAME);

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

    /**
     * Returns all books.
     */
    public List<Book> getBooks() {
        return books;
    }

    public Book findByIsbn(String isbn) {

        for (Book book : books) {

            if (book.getIsbn().equals(isbn)) {
                return book;
            }

        }

        return null;
    }

}