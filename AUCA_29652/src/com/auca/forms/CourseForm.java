package com.auca.forms;

import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.teacher.Teacher;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

public class CourseForm {
    private final Repository repo;

    private String courseId, courseCode, courseTitle;
    private Integer creditHours;
    private Department department;
    private Teacher teacher;

    public CourseForm(Repository repo) { this.repo = repo; }

    public Course run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "COURSE DETAILS"); f1(); f2(); f3(); f4(); step = 2; }
                    case 2 -> { header(2, "DEPARTMENT AND TEACHER"); f5(); f6(); step = 3; }
                    default -> {
                        Course c = review();
                        if (c != null) return c;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Course registration cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| COURSE REGISTRATION                           Step " + step + " of 3");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/3] " + title);
    }

    private void f1() {
        courseId = InputHelper.ask(1, "Course ID", "String | unique | 4-30 chars", courseId, v -> {
            Validator.requireId(v, "Course ID", 4, 30);
            repo.requireUniqueCourseId(v.trim());
        });
    }
    private void f2() {
        courseCode = InputHelper.ask(2, "Course code", "String | unique | 2-20 chars", courseCode, v -> {
            Validator.requireLength(v, "Course code", 2, 20);
            repo.requireUniqueCourseCode(v.trim());
        });
    }
    private void f3() {
        courseTitle = InputHelper.ask(3, "Course title", "String | 2-120 chars", courseTitle,
                v -> Validator.requireLength(v, "Course title", 2, 120));
    }
    private void f4() {
        creditHours = InputHelper.askInt(4, "Credit hours", "int | 1-6", creditHours, 1, 6);
    }
    private void f5() { department = Selectors.pickDepartment(repo); }
    private void f6() { teacher = Selectors.pickTeacher(repo); }

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

    private Course review() {
        System.out.println("\n+---------------- REVIEW COURSE ----------------------------+");
        row(1, "Course ID", courseId);
        row(2, "Course code", courseCode);
        row(3, "Course title", courseTitle);
        row(4, "Credit hours", creditHours);
        row(5, "Department", department == null ? null : department.getRegistrationSummary());
        row(6, "Teacher", teacher == null ? null : teacher.getRegistrationSummary());
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save course   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Course course = new Course(courseId, courseCode, courseTitle, creditHours, department, teacher);
        try {
            repo.addCourse(course);
            System.out.println("+ Course " + courseCode + " registered successfully.");
            return course;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}