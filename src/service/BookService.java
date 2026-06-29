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
            throw new ValidationException("کتاب نمی‌تواند خالی باشد.");
        }

        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }

        if (bookRepository.findByIsbn(book.getIsbn()) != null) {
            throw new ValidationException("کتابی با این شابک قبلاً وجود دارد.");
        }

        validateBookFields(book);
        bookRepository.add(book);
    }

    public void updateBook(String isbn, Book updatedBook) throws ValidationException {
        editBook(isbn, updatedBook);
    }

    public void editBook(String isbn, Book updatedBook) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }

        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook == null) {
            throw new ValidationException("کتاب یافت نشد.");
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
        ReservationService reservationService =
                new ReservationService();
        try{
        reservationService.processApprovedReservation(updatedBook,false);
    }
    catch (Exception e){

    }
} 

    public void deleteBook(String isbn) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("شابک نمی‌تواند خالی باشد.");
        }

        boolean removed = bookRepository.remove(isbn);
        if (!removed) {
            throw new ValidationException("کتاب یافت نشد.");
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
            return "شابک نامعتبر است.";
        }

        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            return "کتاب یافت نشد.";
        }

        StringBuilder status = new StringBuilder();
        status.append("عنوان: ").append(book.getTitle()).append("\n");
        status.append("نویسنده: ").append(book.getAuthor()).append("\n");
        status.append("ناشر: ").append(book.getPublisher()).append("\n");
        status.append("سال انتشار: ").append(book.getYear()).append("\n");
        status.append("دسته‌بندی: ").append(book.getCategory()).append("\n");
        status.append("تعداد کل: ").append(book.getTotalCopies()).append("\n");
        status.append("تعداد موجود: ").append(book.getAvailableCopies()).append("\n");

        if (book.getAvailableCopies() > 0) {
            status.append("وضعیت: موجود");
        } else {
            status.append("وضعیت: ناموجود");
        }

        return status.toString();
    }

    private void validateBookFields(Book book) throws ValidationException {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("عنوان نمی‌تواند خالی باشد.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new ValidationException("نویسنده نمی‌تواند خالی باشد.");
        }
        if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
            throw new ValidationException("ناشر نمی‌تواند خالی باشد.");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new ValidationException("دسته‌بندی نمی‌تواند خالی باشد.");
        }
        if (book.getYear() <= 0) {
            throw new ValidationException("سال انتشار نامعتبر است.");
        }
        if (book.getTotalCopies() < 0) {
            throw new ValidationException("تعداد کل نمی‌تواند منفی باشد.");
        }
        if (book.getAvailableCopies() < 0) {
            throw new ValidationException("تعداد موجود نمی‌تواند منفی باشد.");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new ValidationException("تعداد موجود نمی‌تواند از تعداد کل بیشتر باشد.");
        }
    }
}
