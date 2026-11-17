package com.auca.main;

import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.forms.AdministratorForm;
import com.auca.forms.AssessmentForm;
import com.auca.forms.BorrowBookForm;
import com.auca.forms.ClassroomForm;
import com.auca.forms.CourseForm;
import com.auca.forms.DepartmentForm;
import com.auca.forms.EnrollmentForm;
import com.auca.forms.FeePaymentForm;
import com.auca.forms.LibraryBookForm;
import com.auca.forms.Reports;
import com.auca.forms.ReturnBookForm;
import com.auca.forms.StudentForm;
import com.auca.forms.TeacherForm;
import com.auca.forms.UpdateForm;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.InputHelper;
import com.auca.util.Repository;

import java.util.List;
import java.util.function.Supplier;

public class Main {
    private static final String[] TITLES = {
        "Exit", "Register Department", "Register Student", "Register Teacher",
        "Register Administrator", "Register Course", "Register Classroom",
        "Enroll Student", "Record Assessment", "Record Fee Payment",
        "Register Library Book", "Borrow Library Book", "Return Library Book",
        "View All Records", "Search Record by ID", "Update Existing Record",
        "Student Academic Summary", "Student Payment History",
        "View Available Books", "Demonstrate Polymorphism"
    };

    public static void main(String[] args) {
        Repository repo = new Repository();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputHelper.askMenu("Select an option: ", 0, 19);
            try {
                switch (choice) {
                    case 1 -> register(() -> new DepartmentForm(repo).run());
                    case 2 -> register(() -> new StudentForm(repo).run());
                    case 3 -> register(() -> new TeacherForm(repo).run());
                    case 4 -> register(() -> new AdministratorForm(repo).run());
                    case 5 -> register(() -> new CourseForm(repo).run());
                    case 6 -> register(() -> new ClassroomForm(repo).run());
                    case 7 -> register(() -> new EnrollmentForm(repo).run());
                    case 8 -> register(() -> new AssessmentForm(repo).run());
                    case 9 -> register(() -> new FeePaymentForm(repo).run());
                    case 10 -> register(() -> new LibraryBookForm(repo).run());
                    case 11 -> register(() -> new BorrowBookForm(repo).run());
                    case 12 -> new ReturnBookForm(repo).run();
                    case 13 -> viewAll(repo);
                    case 14 -> search(repo);
                    case 15 -> new UpdateForm(repo).run();
                    case 16 -> Reports.academicSummary(repo);
                    case 17 -> Reports.paymentHistory(repo);
                    case 18 -> Reports.availableBooks(repo);
                    case 19 -> Reports.polymorphismDemo(repo);
                    default -> { System.out.println("Goodbye."); running = false; }
                }
            } catch (RecordNotFoundException e) {
                System.out.println("! " + e.getMessage());
            } catch (FormNavigationException e) {
                System.out.println("Cancelled. Returning to the menu.");
            } catch (RuntimeException e) {
                System.out.println("Unexpected problem: " + e.getMessage() + ". Returning to the menu.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n============== AUCA_ID SCHOOL MANAGEMENT ==============");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("%2d. %-28s", i, TITLES[i]);
            if (i < 10) System.out.printf("%2d. %s%n", i + 10, TITLES[i + 10]);
            else System.out.printf("%2d. %s%n", 0, TITLES[0]);
        }
    }

    /** After a successful save: [1] Register another  [2] View record  [0] Main menu. */
    private static <T extends Registrable> void register(Supplier<T> form) {
        boolean again = true;
        while (again) {
            T saved = form.get();
            if (saved == null) return;                 // cancelled: nothing was saved
            while (true) {
                System.out.println("\n  [1] Register another   [2] View record   [0] Main menu");
                int c = InputHelper.askMenu("Select option: ", 0, 2);
                if (c == 2) { saved.printDetails(); continue; }
                again = (c == 1);
                break;
            }
        }
    }

    // ---------------- option 13 ----------------
    private static void section(String title, List<? extends Registrable> list) {
        System.out.println("\n=== " + title + " (" + list.size() + ") ===");
        if (list.isEmpty()) System.out.println("(none registered)");
        for (Registrable r : list) r.printDetails();
    }

    private static void viewAll(Repository repo) {
        section("DEPARTMENTS", repo.getDepartments());
        section("STUDENTS", repo.getStudents());
        section("TEACHERS", repo.getTeachers());
        section("ADMINISTRATORS", repo.getAdministrators());
        section("COURSES", repo.getCourses());
        section("CLASSROOMS", repo.getClassrooms());
        section("ENROLLMENTS", repo.getEnrollments());
        section("ASSESSMENTS", repo.getAssessments());
        section("FEE PAYMENTS", repo.getPayments());
        section("LIBRARY BOOKS", repo.getBooks());
        section("BOOK LOANS", repo.getLoans());
        InputHelper.readRaw("\nPress ENTER to return to the menu...");
    }

    // ---------------- option 14 ----------------
    private static void search(Repository repo) throws RecordNotFoundException {
        String id = InputHelper.readRaw("Enter the ID to search (Person ID, Course ID, Book ID...): ");
        if (id.isEmpty()) {
            System.out.println("! The ID must not be blank.");
            return;
        }
        List<Identifiable> found = repo.findById(id);      // throws RecordNotFoundException
        System.out.println("\n=== " + found.size() + " record(s) found ===");
        for (Identifiable r : found)
            System.out.println("[" + r.getClass().getSimpleName() + "] " + r);
    }
}