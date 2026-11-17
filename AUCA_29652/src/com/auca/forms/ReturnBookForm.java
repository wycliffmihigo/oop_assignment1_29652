package com.auca.forms;

import com.auca.bookloan.BookLoan;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.time.LocalDate;

public class ReturnBookForm {
    private final Repository repo;

    public ReturnBookForm(Repository repo) { this.repo = repo; }

    public BookLoan run() {
        boolean anyActive = false;
        for (BookLoan l : repo.getLoans()) if (l.isActive()) anyActive = true;
        if (!anyActive) {
            System.out.println("There is no active loan to return. Borrow a book first (option 11).");
            return null;
        }
        try {
            System.out.println("\n+------------------------------------------------------------+");
            System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
            System.out.println("| RETURN LIBRARY BOOK");
            System.out.println("| BACK or CANCEL = discard | ENTER = keep value");
            System.out.println("+------------------------------------------------------------+");
            final BookLoan loan = Selectors.pickActiveLoan(repo);
            System.out.println("Selected: " + loan.getRegistrationSummary());

            LocalDate date = null;
            while (true) {
                date = InputHelper.askDate(2, "Return date", date, d -> {
                    Validator.requireNotFuture(d, "Return date");
                    if (d.isBefore(loan.getBorrowedDate()))
                        throw new ValidationException("Return date cannot be before the borrowed date.");
                });
                System.out.println("\n| Return \"" + loan.getBook().getTitle() + "\" on " + date);
                System.out.println("| [1] Confirm return   [2] Change date   [0] Cancel");
                int c = InputHelper.askMenu("Select option: ", 0, 2);
                if (c == 0) throw new FormNavigationException(true);
                if (c == 2) continue;
                try {
                    repo.returnBook(loan, date);
                    System.out.println("+ Book returned. Copies available now: "
                            + loan.getBook().getAvailableCopies());
                    return loan;
                } catch (ValidationException e) {
                    System.out.println("! Cannot save: " + e.getMessage());
                }
            }
        } catch (FormNavigationException e) {
            System.out.println("Return cancelled. Nothing was changed.");
            return null;
        }
    }
}