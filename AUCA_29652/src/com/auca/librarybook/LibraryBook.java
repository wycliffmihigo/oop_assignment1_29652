package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Validator;

public class LibraryBook implements Identifiable, Registrable {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author,
                       int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override public String getId() { return bookId; }
    @Override public String getDisplayName() { return title; }
    @Override public boolean isActive() { return availableCopies > 0; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(bookId, "Book ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(isbn, "ISBN", 10, 17);
        Validator.requireLength(title, "Title", 1, 150);
        Validator.requireLength(author, "Author", 2, 100);
        if (totalCopies < 0)
            throw new ValidationException("Total copies must not be negative.");
        if (availableCopies < 0)
            throw new ValidationException("Available copies must not be negative.");
        if (availableCopies > totalCopies)
            throw new ValidationException("Available copies cannot exceed total copies.");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + bookId + "] " + title + " by " + author
             + " (" + availableCopies + "/" + totalCopies + " available)";
    }

    public String getBookId() { return bookId; }
    public void setBookId(String v) { this.bookId = v; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String v) { this.isbn = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getAuthor() { return author; }
    public void setAuthor(String v) { this.author = v; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int v) { this.totalCopies = v; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int v) { this.availableCopies = v; }

    @Override
    public String toString() {
        return "LibraryBook{id=" + bookId + ", isbn=" + isbn + ", title=" + title
             + ", author=" + author + ", total=" + totalCopies
             + ", available=" + availableCopies + "}";
    }
}