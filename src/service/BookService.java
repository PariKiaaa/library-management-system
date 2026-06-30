package src.service;

import src.exception.ValidationException;
import src.model.Book;
import src.repository.BookRepository;

import java.util.List;

// Service class for book management operations
public class BookService {

    private final BookRepository bookRepository;

    // Constructor - initializes the book repository
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Adds a new book after validation
    public void addBook(Book book) throws ValidationException {
        // Check if book is null
        if (book == null) {
            throw new ValidationException("کتاب نمی‌تواند خالی باشد.");
        }

        // Check if ISBN is empty
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }

        // Check if book with same ISBN already exists
        if (bookRepository.findByIsbn(book.getIsbn()) != null) {
            throw new ValidationException("کتابی با این شابک قبلاً وجود دارد.");
        }

        // Validate all book fields
        validateBookFields(book);
        bookRepository.add(book);
    }

    // Updates an existing book - alias for editBook
    public void updateBook(String isbn, Book updatedBook) throws ValidationException {
        editBook(isbn, updatedBook);
    }

    // Edits an existing book
    public void editBook(String isbn, Book updatedBook) throws ValidationException {
        // Validate ISBN
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }

        // Find existing book
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook == null) {
            throw new ValidationException("کتاب یافت نشد.");
        }

        // Validate updated book fields
        validateBookFields(updatedBook);

        // Update book fields with new values
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setYear(updatedBook.getYear());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setTotalCopies(updatedBook.getTotalCopies());
        existingBook.setAvailableCopies(updatedBook.getAvailableCopies());
        existingBook.setImagePath(updatedBook.getImagePath());

        // Save changes to repository
        bookRepository.save();
        
        // Process pending reservations for this book
        ReservationService reservationService = new ReservationService();
        try {
            reservationService.processApprovedReservation(updatedBook, false);
        } catch (Exception e) {
            // Silently handle any errors during reservation processing
        }
    }

    // Deletes a book by ISBN
    public void deleteBook(String isbn) throws ValidationException {
        // Validate ISBN
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }

        // Remove book from repository
        boolean removed = bookRepository.remove(isbn);
        if (!removed) {
            throw new ValidationException("کتاب یافت نشد.");
        }
    }

    // Search books by title
    public List<Book> searchByTitle(String title) {
        return bookRepository.searchByTitle(title);
    }

    // Search books by author
    public List<Book> searchByAuthor(String author) {
        return bookRepository.searchByAuthor(author);
    }

    // Search books by category
    public List<Book> searchByCategory(String category) {
        return bookRepository.searchByCategory(category);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.getBooks();
    }

    // Find a book by ISBN
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    // Validates all book fields for consistency and completeness
    private void validateBookFields(Book book) throws ValidationException {
        // Validate title
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("عنوان نمی‌تواند خالی باشد.");
        }
        // Validate author
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new ValidationException("نویسنده نمی‌تواند خالی باشد.");
        }
        // Validate publisher
        if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
            throw new ValidationException("ناشر نمی‌تواند خالی باشد.");
        }
        // Validate category
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new ValidationException("دسته‌بندی نمی‌تواند خالی باشد.");
        }
        // Validate year
        if (book.getYear() <= 0) {
            throw new ValidationException("سال انتشار نامعتبر است.");
        }
        // Validate total copies
        if (book.getTotalCopies() < 0) {
            throw new ValidationException("تعداد کل نمی‌تواند منفی باشد.");
        }
        // Validate available copies
        if (book.getAvailableCopies() < 0) {
            throw new ValidationException("تعداد موجود نمی‌تواند منفی باشد.");
        }
        // Validate that available copies don't exceed total copies
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new ValidationException("تعداد موجود نمی‌تواند از تعداد کل بیشتر باشد.");
        }
    }
}