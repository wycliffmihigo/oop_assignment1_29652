package com.auca.forms;

import com.auca.bookloan.BookLoan;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.time.LocalDate;

public class BorrowBookForm {
    private final Repository repo;

    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowed, due;

    public BorrowBookForm(Repository repo) { this.repo = repo; }

    public BookLoan run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "LOAN, STUDENT AND BOOK"); f1(); f2(); f3(); step = 2; }
                    case 2 -> { header(2, "DATES"); f4(); f5(); step = 3; }
                    default -> {
                        BookLoan l = review();
                        if (l != null) return l;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Borrowing cancelled. Nothing was saved and stock is unchanged.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| BORROW LIBRARY BOOK                           Step " + step + " of 3");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/3] " + title);
    }

    private void f1() {
        loanId = InputHelper.ask(1, "Loan ID", "String | unique | 4-30 chars", loanId, v -> {
            Validator.requireId(v, "Loan ID", 4, 30);
            repo.requireUniqueLoanId(v.trim());
        });
    }
    private void f2() { student = Selectors.pickStudent(repo); }
    private void f3() { book = Selectors.pickBook(repo); }
    private void f4() {
        borrowed = InputHelper.askDate(4, "Borrowed date", borrowed,
                d -> Validator.requireNotFuture(d, "Borrowed date"));
    }
    private void f5() {
        due = InputHelper.askDate(5, "Due date", due, d -> {
            if (borrowed != null && !d.isAfter(borrowed))
                throw new ValidationException("Due date must be after the borrowed date.");
        });
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (1-5): ", 1, 5);
        switch (n) {
            case 1 -> f1(); case 2 -> f2(); case 3 -> f3();
            case 4 -> { f4(); f5(); }        // the due date depends on the borrowed date
            default -> f5();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-16s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private BookLoan review() {
        System.out.println("\n+---------------- REVIEW LOAN ------------------------------+");
        row(1, "Loan ID", loanId);
        row(2, "Student", student == null ? null : student.getRegistrationSummary());
        row(3, "Book", book == null ? null : book.getRegistrationSummary());
        row(4, "Borrowed date", borrowed);
        row(5, "Due date", due);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Confirm loan   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        BookLoan loan = new BookLoan(loanId, student, book, borrowed, due, null);
        try {
            repo.borrowBook(loan);
            System.out.println("+ Loan " + loanId + " registered. Copies left: " + book.getAvailableCopies());
            return loan;
        } catch (ValidationException | DuplicateRecordException | BookUnavailableException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field (for example pick another book) or [0] Cancel.");
            return null;
        }
    }
}