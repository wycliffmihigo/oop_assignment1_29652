package com.auca.forms;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.assessment.Assessment;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.util.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class Reports {
    private Reports() { }

    private static Student chooseStudent(Repository repo) throws RecordNotFoundException {
        if (repo.getStudents().isEmpty())
            throw new RecordNotFoundException("No student is registered yet. Register a student first (option 2).");
        return Selectors.pickExistingStudent(repo);
    }

    // ---------------- option 16 ----------------
    public static void academicSummary(Repository repo) throws RecordNotFoundException {
        Student s = chooseStudent(repo);
        System.out.println("\n=========== STUDENT ACADEMIC SUMMARY ===========");
        System.out.println(s.getRegistrationSummary() + " | Program: " + s.getProgramName()
                + " | Year " + s.getStudyYear());
        int count = 0;
        double totalObtained = 0, totalMax = 0;
        for (Enrollment e : repo.getEnrollments()) {
            if (e.getStudent() != s) continue;
            count++;
            System.out.println("\n" + e.getCourse().getDisplayName() + " | " + e.getSemester()
                    + " | " + e.getEnrollmentStatus());
            double obtained = 0, max = 0;
            int n = 0;
            for (Assessment a : repo.getAssessments()) {
                if (a.getEnrollment() != e) continue;
                n++;
                obtained += a.getObtainedScore();
                max += a.getMaximumScore();
                System.out.printf("   %-11s %6.1f / %-6.1f (%.1f%%)  %s%n", a.getAssessmentType(),
                        a.getObtainedScore(), a.getMaximumScore(),
                        a.getObtainedScore() / a.getMaximumScore() * 100, a.getAssessmentDate());
            }
            if (n == 0) System.out.println("   No assessments recorded.");
            else System.out.printf("   Course average: %.1f%%%n", obtained / max * 100);
            totalObtained += obtained;
            totalMax += max;
        }
        if (count == 0) System.out.println("\nThis student has no enrollments yet.");
        else if (totalMax > 0) System.out.printf("%nOverall average: %.1f%%%n", totalObtained / totalMax * 100);
    }

    // ---------------- option 17 ----------------
    public static void paymentHistory(Repository repo) throws RecordNotFoundException {
        Student s = chooseStudent(repo);
        System.out.println("\n=========== PAYMENT HISTORY ===========");
        System.out.println(s.getRegistrationSummary());
        BigDecimal total = BigDecimal.ZERO;
        int n = 0;
        for (FeePayment p : repo.getPayments()) {
            if (p.getStudent() != s) continue;
            n++;
            total = total.add(p.getAmount());
            System.out.println("  " + p.getPaymentDate() + " | " + p.getReceiptNumber()
                    + " | " + p.getPaymentMethod() + " | " + p.getAmount());
        }
        if (n == 0) System.out.println("No payments recorded for this student.");
        else System.out.println("Payments: " + n + " | Total paid: " + total);
    }

    // ---------------- option 18 ----------------
    public static void availableBooks(Repository repo) {
        System.out.println("\n=========== AVAILABLE BOOKS ===========");
        int n = 0;
        for (LibraryBook b : repo.getBooks()) {
            if (b.getAvailableCopies() > 0) {
                n++;
                System.out.println(b.getRegistrationSummary() + " | ISBN " + b.getIsbn());
            }
        }
        if (n == 0) System.out.println("No book has an available copy right now.");
    }

    // ---------------- option 19 ----------------
    public static void polymorphismDemo(Repository repo) {
        List<Person> people = new ArrayList<>();
        people.addAll(repo.getStudents());
        people.addAll(repo.getTeachers());
        people.addAll(repo.getAdministrators());

        System.out.println("\n===== 1. List<Person>: Student, Teacher and Administrator objects =====");
        if (people.isEmpty())
            System.out.println("No Student, Teacher or Administrator is registered yet.");
        for (Person p : people) {
            // runtime polymorphism: Java runs the overridden method of the REAL object
            System.out.println(p.getRoleDescription() + " -> " + p.getRegistrationSummary()
                    + " | active=" + p.isActive());
            if (p instanceof AcademicMember am)
                System.out.println("      AcademicMember category: " + am.getAcademicCategory()
                        + " | department: " + am.getDepartment().getDepartmentName());
        }

        System.out.println("\n===== 2. List<Identifiable>: unrelated concrete classes =====");
        List<Identifiable> all = repo.allRecords();
        if (all.isEmpty()) System.out.println("No record is registered yet.");
        for (Identifiable i : all)
            System.out.printf("%-14s %-14s %-32s active=%s%n",
                    i.getClass().getSimpleName(), i.getId(), i.getDisplayName(), i.isActive());

        System.out.println("\nNotes:");
        System.out.println(" - One reference type (Person / Identifiable), many behaviours: the overridden");
        System.out.println("   method of the real object runs at run time (polymorphism).");
        System.out.println(" - Student calls super(...) -> AcademicMember calls super(...) -> Person (constructor chaining).");
        System.out.println(" - Person and AcademicMember are abstract: they have unimplemented methods, so");
        System.out.println("   'new Person(...)' will not compile.");
    }
}