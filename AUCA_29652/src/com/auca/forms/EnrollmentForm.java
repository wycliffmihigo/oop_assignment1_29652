package com.auca.forms;

import com.auca.course.Course;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.student.Student;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.time.LocalDate;

public class EnrollmentForm {
    private final Repository repo;

    private String enrollmentId, semester, status;
    private Student student;
    private Course course;
    private LocalDate date;

    public EnrollmentForm(Repository repo) { this.repo = repo; }

    public Enrollment run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "STUDENT AND COURSE"); f1(); f2(); f3(); step = 2; }
                    case 2 -> { header(2, "ENROLLMENT DETAILS"); f4(); f5(); f6(); step = 3; }
                    default -> {
                        Enrollment e = review();
                        if (e != null) return e;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Enrollment cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| ENROLL STUDENT                                Step " + step + " of 3");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/3] " + title);
    }

    private void f1() {
        enrollmentId = InputHelper.ask(1, "Enrollment ID", "String | unique | 4-30 chars", enrollmentId, v -> {
            Validator.requireId(v, "Enrollment ID", 4, 30);
            repo.requireUniqueEnrollmentId(v.trim());
        });
    }
    private void f2() { student = Selectors.pickStudent(repo); }
    private void f3() { course = Selectors.pickCourse(repo); }
    private void f4() {
        date = InputHelper.askDate(4, "Enrollment date", date,
                d -> Validator.requireNotFuture(d, "Enrollment date"));
    }
    private void f5() {
        semester = InputHelper.askChoice(5, "Semester", semester, "FALL", "SPRING", "SUMMER");
    }
    private void f6() {
        status = InputHelper.askChoice(6, "Enrollment status", status, "ACTIVE", "COMPLETED", "DROPPED");
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

    private Enrollment review() {
        System.out.println("\n+---------------- REVIEW ENROLLMENT ------------------------+");
        row(1, "Enrollment ID", enrollmentId);
        row(2, "Student", student == null ? null : student.getRegistrationSummary());
        row(3, "Course", course == null ? null : course.getRegistrationSummary());
        row(4, "Enrollment date", date);
        row(5, "Semester", semester);
        row(6, "Status", status);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save enrollment   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Enrollment e = new Enrollment(enrollmentId, student, course, date, semester, status);
        try {
            repo.addEnrollment(e);
            System.out.println("+ Enrollment " + enrollmentId + " registered successfully.");
            return e;
        } catch (ValidationException | DuplicateRecordException ex) {
            System.out.println("! Cannot save: " + ex.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}