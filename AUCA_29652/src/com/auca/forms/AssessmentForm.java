package com.auca.forms;

import com.auca.assessment.Assessment;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.time.LocalDate;

public class AssessmentForm {
    private final Repository repo;

    private String assessmentId, type;
    private Enrollment enrollment;
    private Double maxScore, obtained;
    private LocalDate date;

    public AssessmentForm(Repository repo) { this.repo = repo; }

    public Assessment run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "ENROLLMENT"); f1(); f2(); step = 2; }
                    case 2 -> { header(2, "SCORES"); f3(); f4(); f5(); f6(); step = 3; }
                    default -> {
                        Assessment a = review();
                        if (a != null) return a;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Assessment cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| RECORD ASSESSMENT                             Step " + step + " of 3");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/3] " + title);
    }

    private static double parseScore(String v, String label) throws ValidationException {
        try {
            double d = Double.parseDouble(v.trim());
            if (Double.isNaN(d) || Double.isInfinite(d)) throw new NumberFormatException();
            return d;
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a number, for example 85.5");
        }
    }

    private void f1() {
        assessmentId = InputHelper.ask(1, "Assessment ID", "String | unique | 4-30 chars", assessmentId, v -> {
            Validator.requireId(v, "Assessment ID", 4, 30);
            repo.requireUniqueAssessmentId(v.trim());
        });
    }
    private void f2() { enrollment = Selectors.pickActiveEnrollment(repo); }
    private void f3() {
        type = InputHelper.askChoice(3, "Assessment type", type,
                "QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM");
    }
    private void f4() {
        String cur = maxScore == null ? null : String.valueOf(maxScore);
        maxScore = Double.parseDouble(InputHelper.ask(4, "Maximum score", "double | greater than 0", cur, v -> {
            if (parseScore(v, "Maximum score") <= 0)
                throw new ValidationException("Maximum score must be greater than zero.");
        }).trim());
    }
    private void f5() {
        String cur = obtained == null ? null : String.valueOf(obtained);
        obtained = Double.parseDouble(InputHelper.ask(5, "Obtained score",
                "double | 0 to " + maxScore, cur, v -> {
            double s = parseScore(v, "Obtained score");
            if (s < 0 || s > maxScore)
                throw new ValidationException("Obtained score must be between 0 and " + maxScore + ".");
        }).trim());
    }
    private void f6() {
        date = InputHelper.askDate(6, "Assessment date", date,
                d -> {
                    Validator.requireNotBefore(d, enrollment.getEnrollmentDate(), "Assessment date", "enrollment date");
                    Validator.requireNotFuture(d, "Assessment date");
                });
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (1-6): ", 1, 6);
        switch (n) {
            case 1 -> f1(); case 2 -> f2(); case 3 -> f3();
            case 4 -> { f4(); f5(); }        // the obtained score depends on the maximum
            case 5 -> f5();
            default -> f6();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-16s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private Assessment review() {
        System.out.println("\n+---------------- REVIEW ASSESSMENT ------------------------+");
        row(1, "Assessment ID", assessmentId);
        row(2, "Enrollment", enrollment == null ? null : enrollment.getRegistrationSummary());
        row(3, "Type", type);
        row(4, "Maximum score", maxScore);
        row(5, "Obtained score", obtained);
        row(6, "Date", date);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save assessment   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Assessment a = new Assessment(assessmentId, enrollment, type, maxScore, obtained, date);
        try {
            repo.addAssessment(a);
            System.out.println("+ Assessment " + assessmentId + " recorded successfully.");
            return a;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}