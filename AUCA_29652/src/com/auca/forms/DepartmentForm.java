package com.auca.forms;

import com.auca.department.Department;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.teacher.Teacher;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.util.List;

public class DepartmentForm {
    private final Repository repo;
    private String id, name, code, office, email;
    private Teacher head;                      // optional

    public DepartmentForm(Repository repo) { this.repo = repo; }

    /** Returns the saved Department, or null if cancelled. */
    public Department run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1); f1(); f2(); f3(); f4(); f5(); step = 2; }
                    case 2 -> { header(2); f6(); step = 3; }
                    default -> {
                        Department d = review();
                        if (d != null) return d;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Department registration cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| DEPARTMENT REGISTRATION                       Step " + step + " of 3");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
    }

    private void f1() {
        id = InputHelper.ask(1, "Department ID", "String | unique | 4-30 chars", id, v -> {
            Validator.requireId(v, "Department ID", 4, 30);
            repo.requireUniqueDepartmentId(v.trim());
        });
    }

        private void f2() {
        name = InputHelper.ask(2, "Department name", "String | letters, 2-60 chars", name,
                v -> Validator.requireName(v, "Department name", 2, 60));
    }

    private void f3() {
        code = InputHelper.ask(3, "Department code", "String | unique | 2-10 chars", code, v -> {
            Validator.requireLength(v, "Department code", 2, 10);
            repo.requireUniqueDepartmentCode(v.trim());
        });
    }

    private void f4() {
        office = InputHelper.ask(4, "Office location", "String | 2-100 chars", office,
                v -> Validator.requireLength(v, "Office location", 2, 100));
    }

    private void f5() {
        email = InputHelper.ask(5, "Contact email", "String | email", email,
                v -> Validator.requireEmail(v, "Contact email"));
    }

    private void f6() {
        List<Teacher> teachers = repo.getTeachers();
        if (teachers.isEmpty()) {
            head = null;
            System.out.println("06 Head teacher [optional]: no Teacher registered yet.");
            System.out.println("   It can be assigned later, after a Teacher exists.");
            return;
        }
        System.out.println("06 Head teacher [optional] - choose one:");
        for (int i = 0; i < teachers.size(); i++)
            System.out.println("   " + (i + 1) + " " + teachers.get(i).getRegistrationSummary());
        System.out.println("   0 No head teacher for now");
        int c = InputHelper.askMenu("Select: ", 0, teachers.size());
        head = (c == 0) ? null : teachers.get(c - 1);
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (1-6): ", 1, 6);
        switch (n) {
            case 1 -> f1();
            case 2 -> f2();
            case 3 -> f3();
            case 4 -> f4();
            case 5 -> f5();
            default -> f6();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-18s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private Department review() {
        System.out.println("\n+---------------- REVIEW DEPARTMENT ------------------+");
        row(1, "Department ID", id);
        row(2, "Department name", name);
        row(3, "Department code", code);
        row(4, "Office location", office);
        row(5, "Contact email", email);
        row(6, "Head teacher", head == null ? "(none yet)" : head.getRegistrationSummary());
        System.out.println("+-----------------------------------------------------+");
        System.out.println("| [1] Save department   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Department d = new Department(id, name, code, office, email, head);
        try {
            repo.addDepartment(d);                       // re-validates and re-checks duplicates
            System.out.println("+ Department " + id + " registered successfully.");
            return d;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}