package com.auca.forms;

import com.auca.administrator.Administrator;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.time.LocalDate;

public class AdministratorForm {
    private final Repository repo;

    private String personId, firstName, lastName, email, phone;
    private LocalDate dob;
    private String staffNumber, jobTitle, assignedOffice;
    private LocalDate employmentDate;
    private Integer accessLevel;
    private Boolean active;

    public AdministratorForm(Repository repo) { this.repo = repo; }

    public Administrator run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "PERSONAL DETAILS");
                                f01(); f02(); f03(); f04(); f05(); f06(); step = 2; }
                    case 2 -> { header(2, "ADMINISTRATOR DETAILS");
                                f07(); f08(); f09(); f10(); f11(); f12(); step = 3; }
                    default -> {
                        Administrator a = review();
                        if (a != null) return a;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Administrator registration cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| ADMINISTRATOR REGISTRATION                    Step " + step + " of 3");
        System.out.println("| [1] Personal  [2] Administrator  [3] Review & confirm");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/3] " + title);
    }

    private void f01() {
        personId = InputHelper.ask(1, "Person ID", "String | unique | 4-30 chars", personId, v -> {
            Validator.requireId(v, "Person ID", 4, 30);
            repo.requireUniquePersonId(v.trim());
        });
    }
    private void f02() {
        firstName = InputHelper.ask(2, "First name", "String | letters, 2-60 chars", firstName,
                v -> Validator.requireName(v, "First name", 2, 60));
    }
    private void f03() {
        lastName = InputHelper.ask(3, "Last name", "String | letters, 2-60 chars", lastName,
                v -> Validator.requireName(v, "Last name", 2, 60));
    }
    private void f04() {
        email = InputHelper.ask(4, "Email", "String | unique | email", email, v -> {
            Validator.requireEmail(v, "Email");
            repo.requireUniqueEmail(v.trim());
        });
    }
    private void f05() {
        phone = InputHelper.ask(5, "Phone", "String | 10-15 digits", phone,
                v -> Validator.requirePhone(v, "Phone"));
    }
    private void f06() {
        dob = InputHelper.askDate(6, "Date of birth", dob,
                d -> Validator.requirePast(d, "Date of birth"));
    }
    private void f07() {
        staffNumber = InputHelper.ask(7, "Staff number", "String | unique | 4-30", staffNumber, v -> {
            Validator.requireId(v, "Staff number", 4, 30);
            repo.requireUniqueStaffNumber(v.trim());
        });
    }
    private void f08() {
        jobTitle = InputHelper.ask(8, "Job title", "String | 2-100 chars", jobTitle,
                v -> Validator.requireLength(v, "Job title", 2, 100));
    }
    private void f09() {
        assignedOffice = InputHelper.ask(9, "Assigned office", "String | 1-100 chars", assignedOffice,
                v -> Validator.requireLength(v, "Assigned office", 1, 100));
    }
    private void f10() {
        employmentDate = InputHelper.askDate(10, "Employment date", employmentDate,
                d -> {
                    Validator.requireNotBefore(d, dob, "Employment date", "date of birth");
                    Validator.requireNotFuture(d, "Employment date");
                });
    }
    private void f11() {
        accessLevel = InputHelper.askInt(11, "Access level", "int | 1-5", accessLevel, 1, 5);
    }
    private void f12() {
        String cur = active == null ? null : (active ? "YES" : "NO");
        active = InputHelper.askChoice(12, "Active", cur, "YES", "NO").equals("YES");
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (01-12): ", 1, 12);
        switch (n) {
            case 1 -> f01();  case 2 -> f02();  case 3 -> f03();
            case 4 -> f04();  case 5 -> f05();  case 6 -> f06();
            case 7 -> f07();  case 8 -> f08();  case 9 -> f09();
            case 10 -> f10(); case 11 -> f11(); default -> f12();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-18s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private Administrator review() {
        System.out.println("\n+---------------- REVIEW ADMINISTRATOR ---------------------+");
        System.out.println("| PERSON");
        row(1, "Person ID", personId);
        row(2, "First name", firstName);
        row(3, "Last name", lastName);
        row(4, "Email", email);
        row(5, "Phone", phone);
        row(6, "Date of birth", dob);
        System.out.println("| ADMINISTRATOR");
        row(7, "Staff number", staffNumber);
        row(8, "Job title", jobTitle);
        row(9, "Assigned office", assignedOffice);
        row(10, "Employment date", employmentDate);
        row(11, "Access level", accessLevel);
        row(12, "Active", active == null ? null : (active ? "YES" : "NO"));
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save administrator   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Administrator a = new Administrator(personId, firstName, lastName, email, phone, dob,
                staffNumber, jobTitle, assignedOffice, employmentDate, accessLevel, active);
        try {
            repo.addAdministrator(a);
            System.out.println("+ Administrator " + staffNumber + " registered successfully.");
            return a;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}