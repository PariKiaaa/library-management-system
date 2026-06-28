package src.service;

import src.exception.ValidationException;
import src.model.Book;
import src.repository.BookRepository;

import java.util.List;

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) throws ValidationException {
        if (book == null) {
            throw new ValidationException("Book cannot be null.");
        }

        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }

        if (bookRepository.findByIsbn(book.getIsbn()) != null) {
            throw new ValidationException("A book with this ISBN already exists.");
        }

        validateBookFields(book);
        bookRepository.add(book);
    }

    public void updateBook(String isbn, Book updatedBook) throws ValidationException {
        editBook(isbn, updatedBook);
    }

    public void editBook(String isbn, Book updatedBook) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }

        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook == null) {
            throw new ValidationException("Book not found.");
        }

        validateBookFields(updatedBook);

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setYear(updatedBook.getYear());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setTotalCopies(updatedBook.getTotalCopies());
        existingBook.setAvailableCopies(updatedBook.getAvailableCopies());
        existingBook.setImagePath(updatedBook.getImagePath());

        bookRepository.save();
    }

    public void deleteBook(String isbn) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN cannot be empty.");
        }

        boolean removed = bookRepository.remove(isbn);
        if (!removed) {
            throw new ValidationException("Book not found.");
        }
    }

    public List<Book> searchByTitle(String title) {
        return bookRepository.searchByTitle(title);
    }

    public List<Book> searchByAuthor(String author) {
        return bookRepository.searchByAuthor(author);
    }

    public List<Book> searchByCategory(String category) {
        return bookRepository.searchByCategory(category);
    }

    public List<Book> getAllBooks() {
        return bookRepository.getBooks();
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public String getBookStatus(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return "Invalid ISBN.";
        }

        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            return "Book not found.";
        }

        StringBuilder status = new StringBuilder();
        status.append("Title: ").append(book.getTitle()).append("\n");
        status.append("Author: ").append(book.getAuthor()).append("\n");
        status.append("Publisher: ").append(book.getPublisher()).append("\n");
        status.append("Year: ").append(book.getYear()).append("\n");
        status.append("Category: ").append(book.getCategory()).append("\n");
        status.append("Total Copies: ").append(book.getTotalCopies()).append("\n");
        status.append("Available Copies: ").append(book.getAvailableCopies()).append("\n");

        if (book.getAvailableCopies() > 0) {
            status.append("Status: Available");
        } else {
            status.append("Status: Not Available");
        }

        return status.toString();
    }

    private void validateBookFields(Book book) throws ValidationException {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new ValidationException("Author cannot be empty.");
        }
        if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
            throw new ValidationException("Publisher cannot be empty.");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category cannot be empty.");
        }
        if (book.getYear() <= 0) {
            throw new ValidationException("Invalid year.");
        }
        if (book.getTotalCopies() < 0) {
            throw new ValidationException("Total copies cannot be negative.");
        }
        if (book.getAvailableCopies() < 0) {
            throw new ValidationException("Available copies cannot be negative.");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new ValidationException("Available copies cannot exceed total copies.");
        }
    }
}
