package com.auca.forms;

import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.student.Student;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePaymentForm {
    private final Repository repo;

    private String paymentId, method, receipt;
    private Student student;
    private BigDecimal amount;
    private LocalDate date;

    public FeePaymentForm(Repository repo) { this.repo = repo; }

    public FeePayment run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "PAYER"); f1(); f2(); step = 2; }
                    case 2 -> { header(2, "PAYMENT DETAILS"); f3(); f4(); f5(); f6(); step = 3; }
                    default -> {
                        FeePayment p = review();
                        if (p != null) return p;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Payment cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| FEE PAYMENT                                   Step " + step + " of 3");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/3] " + title);
    }

    private void f1() {
        paymentId = InputHelper.ask(1, "Payment ID", "String | unique | 4-30 chars", paymentId, v -> {
            Validator.requireId(v, "Payment ID", 4, 30);
            repo.requireUniquePaymentId(v.trim());
        });
    }
    private void f2() { student = Selectors.pickStudent(repo); }
    private void f3() {
        String cur = amount == null ? null : amount.toPlainString();
        String text = InputHelper.ask(3, "Amount", "BigDecimal | greater than 0", cur, v -> {
            try {
                Validator.requirePositive(new BigDecimal(v.trim()), "Amount");
            } catch (NumberFormatException e) {
                throw new ValidationException("Amount must be a number, for example 150000.50");
            }
        });
        amount = new BigDecimal(text.trim());
    }
    private void f4() {
        date = InputHelper.askDate(4, "Payment date", date,
                d -> Validator.requireNotFuture(d, "Payment date"));
    }
    private void f5() {
        method = InputHelper.askChoice(5, "Payment method", method,
                "CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD");
    }
    private void f6() {
        receipt = InputHelper.ask(6, "Receipt number", "String | unique", receipt, v -> {
            Validator.requireText(v, "Receipt number");
            repo.requireUniqueReceipt(v.trim());
        });
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (1-6): ", 1, 6);
        switch (n) {
            case 1 -> f1(); case 2 -> f2(); case 3 -> f3();
            case 4 -> f4(); case 5 -> f5(); default -> f6();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-16s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private FeePayment review() {
        System.out.println("\n+---------------- REVIEW PAYMENT ---------------------------+");
        row(1, "Payment ID", paymentId);
        row(2, "Student", student == null ? null : student.getRegistrationSummary());
        row(3, "Amount", amount);
        row(4, "Payment date", date);
        row(5, "Payment method", method);
        row(6, "Receipt number", receipt);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save payment   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        FeePayment p = new FeePayment(paymentId, student, amount, date, method, receipt);
        try {
            repo.addPayment(p);
            System.out.println("+ Payment " + paymentId + " recorded successfully.");
            return p;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}