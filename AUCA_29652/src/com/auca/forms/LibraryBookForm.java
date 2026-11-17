package com.auca.forms;

import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.librarybook.LibraryBook;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

public class LibraryBookForm {
    private final Repository repo;

    private String bookId, isbn, title, author;
    private Integer totalCopies, availableCopies;

    public LibraryBookForm(Repository repo) { this.repo = repo; }

    public LibraryBook run() {
        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    header();
                    f1(); f2(); f3(); f4(); f5(); f6();
                    step = 2;
                } else {
                    LibraryBook b = review();
                    if (b != null) return b;
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Book registration cancelled. Nothing was saved.");
                    return null;
                }
                step = 1;
            }
        }
    }

    private void header() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| LIBRARY BOOK REGISTRATION                     Step 1 of 2");
        System.out.println("| BACK = restart fields | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
    }

    private void f1() {
        bookId = InputHelper.ask(1, "Book ID", "String | unique | 4-30 chars", bookId, v -> {
            Validator.requireId(v, "Book ID", 4, 30);
            repo.requireUniqueBookId(v.trim());
        });
    }
    private void f2() {
        isbn = InputHelper.ask(2, "ISBN", "String | unique | 10-17 chars", isbn, v -> {
            Validator.requireLength(v, "ISBN", 10, 17);
            repo.requireUniqueIsbn(v.trim());
        });
    }
    private void f3() {
        title = InputHelper.ask(3, "Title", "String | 1-150 chars", title,
                v -> Validator.requireLength(v, "Title", 1, 150));
    }
    private void f4() {
        author = InputHelper.ask(4, "Author", "String | 2-100 chars", author,
                v -> Validator.requireLength(v, "Author", 2, 100));
    }
    private void f5() {
        totalCopies = InputHelper.askInt(5, "Total copies", "int | 0 or greater", totalCopies, 0, 100000);
    }
    private void f6() {
        availableCopies = InputHelper.askInt(6, "Available copies",
                "int | 0 to " + totalCopies, availableCopies, 0, totalCopies);
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (1-6): ", 1, 6);
        switch (n) {
            case 1 -> f1(); case 2 -> f2(); case 3 -> f3();
            case 4 -> f4(); case 5 -> f5(); default -> f6();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-18s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private LibraryBook review() {
        System.out.println("\n+---------------- REVIEW LIBRARY BOOK ----------------------+");
        row(1, "Book ID", bookId);
        row(2, "ISBN", isbn);
        row(3, "Title", title);
        row(4, "Author", author);
        row(5, "Total copies", totalCopies);
        row(6, "Available copies", availableCopies);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save book   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        LibraryBook b = new LibraryBook(bookId, isbn, title, author, totalCopies, availableCopies);
        try {
            repo.addBook(b);
            System.out.println("+ Book " + bookId + " registered successfully.");
            return b;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}