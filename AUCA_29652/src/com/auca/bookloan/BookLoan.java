package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.util.Validator;

import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {
    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;     // null until the book is returned

    public BookLoan(String loanId, Student student, LibraryBook book,
                    LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    @Override public String getId() { return loanId; }

    @Override
    public String getDisplayName() {
        return (book == null ? "?" : book.getTitle()) + " -> "
             + (student == null ? "?" : student.getDisplayName());
    }

    @Override public boolean isActive() { return returnedDate == null; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(loanId, "Loan ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireNotNull(student, "Student");
        Validator.requireNotNull(book, "Book");
        Validator.requireNotFuture(borrowedDate, "Borrowed date");
        Validator.requireNotNull(dueDate, "Due date");
        if (!dueDate.isAfter(borrowedDate))
            throw new ValidationException("Due date must be after the borrowed date.");
        if (returnedDate != null && returnedDate.isBefore(borrowedDate))
            throw new ValidationException("Returned date cannot be before the borrowed date.");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + loanId + "] " + getDisplayName() + " due " + dueDate
             + (returnedDate == null ? " (on loan)" : " (returned " + returnedDate + ")");
    }

    public String getLoanId() { return loanId; }
    public void setLoanId(String v) { this.loanId = v; }
    public Student getStudent() { return student; }
    public void setStudent(Student v) { this.student = v; }
    public LibraryBook getBook() { return book; }
    public void setBook(LibraryBook v) { this.book = v; }
    public LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDate v) { this.borrowedDate = v; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate v) { this.dueDate = v; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate v) { this.returnedDate = v; }

    @Override
    public String toString() {
        return "BookLoan{id=" + loanId
             + ", student=" + (student == null ? "none" : student.getDisplayName())
             + ", book=" + (book == null ? "none" : book.getTitle())
             + ", borrowed=" + borrowedDate + ", due=" + dueDate
             + ", returned=" + (returnedDate == null ? "not yet" : returnedDate.toString()) + "}";
    }
}