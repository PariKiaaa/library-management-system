package src.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a book in the library system.
 */
public class Book implements Serializable {
    // Book attributes
    private String isbn, title, author, publisher, category, imagePath;
    private int year, totalCopies, availableCopies;

    // Constructor with all fields
    public Book(String isbn, String title, String author, String publisher,
                int year, String category, int totalCopies,
                int availableCopies, String imagePath) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.imagePath = imagePath;
    }

    // Getters and setters for all attributes
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Equality check based on ISBN
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    // Hash code based on ISBN
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    // String representation of the book
    @Override
    public String toString() {
        return "کتاب{" +
                "شابک='" + isbn + '\'' +
                "، عنوان='" + title + '\'' +
                "، نویسنده='" + author + '\'' +
                "، ناشر='" + publisher + '\'' +
                "، سال=" + year +
                "، دسته‌بندی='" + category + '\'' +
                "، تعداد کل=" + totalCopies +
                "، تعداد موجود=" + availableCopies +
                "، مسیر تصویر='" + imagePath + '\'' +
                '}';
    }
}