package com.auca.forms;

import com.auca.bookloan.BookLoan;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.FormNavigationException;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.exceptions.PrerequisiteMissingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Selectors {
    private Selectors() { }

        private static void requireAny(List<?> list, String name) throws PrerequisiteMissingException {
        if (list.isEmpty())
            throw new PrerequisiteMissingException("No " + name + " exists. Register one before continuing.");
    }

    private static <T> T pick(String title, Supplier<List<T>> source, Function<T, String> label,
                              Runnable registerNew) {
        while (true) {
            List<T> list = source.get();
            if (registerNew != null) {
                try {
                    requireAny(list, title.toLowerCase());
                } catch (PrerequisiteMissingException e) {
                    System.out.println("\n! " + e.getMessage());
                    System.out.println("  1 Register " + title.toLowerCase() + " now   2 Return to menu");
                    if (InputHelper.askMenu("Select: ", 1, 2) == 2)
                        throw new FormNavigationException(true);
                    registerNew.run();       // guided registration, then resume
                    continue;
                }
            }
            System.out.println("\n+----------------- CHOOSE " + title + " ----------------+");
            for (int i = 0; i < list.size(); i++)
                System.out.println("  " + (i + 1) + " " + label.apply(list.get(i)));
            int max = list.size();
            if (registerNew != null) {
                max++;
                System.out.println("  " + max + " [+] Register a new " + title.toLowerCase());
            }
            System.out.println("  0 Cancel");
            System.out.println("+------------------------------------------------------------+");
            int c = InputHelper.askMenu("Select option: ", 0, max);
            if (c == 0) throw new FormNavigationException(true);
            if (registerNew != null && c == max) { registerNew.run(); continue; }
            return list.get(c - 1);
        }
    }
    

    public static Department pickDepartment(Repository repo) {
        return pick("DEPARTMENT", repo::getDepartments, Department::getRegistrationSummary,
                () -> new DepartmentForm(repo).run());
    }

    public static Teacher pickTeacher(Repository repo) {
        return pick("TEACHER", repo::getTeachers, Teacher::getRegistrationSummary,
                () -> new TeacherForm(repo).run());
    }

    public static Student pickStudent(Repository repo) {
        return pick("STUDENT", repo::getStudents, Student::getRegistrationSummary,
                () -> new StudentForm(repo).run());
    }

    public static Course pickCourse(Repository repo) {
        return pick("COURSE", repo::getCourses, Course::getRegistrationSummary,
                () -> new CourseForm(repo).run());
    }

    /** Only ACTIVE enrollments can be assessed. */
    public static Enrollment pickActiveEnrollment(Repository repo) {
        return pick("ACTIVE ENROLLMENT", () -> {
            List<Enrollment> active = new ArrayList<>();
            for (Enrollment e : repo.getEnrollments()) if (e.isActive()) active.add(e);
            return active;
        }, Enrollment::getRegistrationSummary, () -> new EnrollmentForm(repo).run());
    }

    public static LibraryBook pickBook(Repository repo) {
        return pick("LIBRARY BOOK", repo::getBooks, LibraryBook::getRegistrationSummary,
                () -> new LibraryBookForm(repo).run());
    }

    /** Loans not yet returned. No "[+]" option, because a loan is not registered here. */
    public static BookLoan pickActiveLoan(Repository repo) {
        return pick("ACTIVE LOAN", () -> {
            List<BookLoan> active = new ArrayList<>();
            for (BookLoan l : repo.getLoans()) if (l.isActive()) active.add(l);
            return active;
        }, BookLoan::getRegistrationSummary, null);
    }
        /** Students only, no "[+] Register" option (used by reports). */
    public static Student pickExistingStudent(Repository repo) {
        return pick("STUDENT", repo::getStudents, Student::getRegistrationSummary, null);
    }
}