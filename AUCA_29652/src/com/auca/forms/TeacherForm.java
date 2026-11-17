package com.auca.forms;

import com.auca.department.Department;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.teacher.Teacher;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TeacherForm {
    private final Repository repo;

    // Person
    private String personId, firstName, lastName, email, phone;
    private LocalDate dob;
    // AcademicMember
    private String memberNumber, academicStatus, institutionalEmail, advisorName;
    private Department department;
    private LocalDate joinDate;
    // Teacher
    private String employeeNumber, specialization, officeNumber, employmentType;
    private LocalDate hireDate;
    private BigDecimal salary;

    public TeacherForm(Repository repo) { this.repo = repo; }

    /** Returns the saved Teacher, or null if cancelled. */
    public Teacher run() {
        int step = 1;
        while (true) {
            try {
                switch (step) {
                    case 1 -> { header(1, "PERSONAL DETAILS");
                                f01(); f02(); f03(); f04(); f05(); f06(); step = 2; }
                    case 2 -> { header(2, "ACADEMIC DETAILS");
                                f07(); f08(); f09(); f10(); f11(); f12(); step = 3; }
                    case 3 -> { header(3, "TEACHER DETAILS");
                                f13(); f14(); f15(); f16(); f17(); f18(); step = 4; }
                    default -> {
                        Teacher t = review();
                        if (t != null) return t;
                    }
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Teacher registration cancelled. Nothing was saved.");
                    return null;
                }
                if (step > 1) step--;
            }
        }
    }

    private void header(int step, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| TEACHER REGISTRATION                          Step " + step + " of 4");
        System.out.println("| [1] Personal  [2] Academic  [3] Teacher  [4] Review & confirm");
        System.out.println("| BACK = previous step | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("[STEP " + step + "/4] " + title);
    }

    // ---------------- Step 1: Person ----------------
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

    // ---------------- Step 2: AcademicMember ----------------
    private void f07() {
        memberNumber = InputHelper.ask(7, "Member number", "String | unique | 4-30", memberNumber, v -> {
            Validator.requireId(v, "Member number", 4, 30);
            repo.requireUniqueMemberNumber(v.trim());
        });
    }

    /** Smart Department selector with nested registration. */
        private void f08() {
        department = Selectors.pickDepartment(repo);
    }

    private void f09() {
        joinDate = InputHelper.askDate(9, "Join date", joinDate, d -> {
            Validator.requireNotFuture(d, "Join date");
            Validator.requireNotBefore(d, dob, "Join date", "date of birth");
        });
    }
    private void f10() {
        academicStatus = InputHelper.askChoice(10, "Academic status", academicStatus,
                "ACTIVE", "INACTIVE");
    }
    private void f11() {
        institutionalEmail = InputHelper.ask(11, "Institutional email", "String | unique | email",
                institutionalEmail, v -> {
                    Validator.requireEmail(v, "Institutional email");
                    repo.requireUniqueInstitutionalEmail(v.trim());
                });
    }
    private void f12() {
        advisorName = InputHelper.ask(12, "Advisor name", "String | letters, 2-120 chars", advisorName,
                v -> Validator.requireName(v, "Advisor name", 2, 120));
    }

    // ---------------- Step 3: Teacher ----------------
    private void f13() {
        employeeNumber = InputHelper.ask(13, "Employee number", "String | unique | 4-30", employeeNumber, v -> {
            Validator.requireId(v, "Employee number", 4, 30);
            repo.requireUniqueEmployeeNumber(v.trim());
        });
    }
    private void f14() {
        specialization = InputHelper.ask(14, "Specialization", "String | 2-100 chars", specialization,
                v -> Validator.requireLength(v, "Specialization", 2, 100));
    }
    private void f15() {
        hireDate = InputHelper.askDate(15, "Hire date", hireDate,
                d -> {
                    Validator.requireNotBefore(d, dob, "Hire date", "date of birth");
                    Validator.requireNotFuture(d, "Hire date");
                });
    }
    private void f16() {
        String cur = salary == null ? null : salary.toPlainString();
        String text = InputHelper.ask(16, "Salary", "BigDecimal | > 0, max 2 decimals", cur,
                v -> Validator.parseMoney(v, "Salary"));
        salary = new BigDecimal(text.trim());
    }
    private void f17() {
        officeNumber = InputHelper.ask(17, "Office number", "String | 1-20 chars", officeNumber,
                v -> Validator.requireLength(v, "Office number", 1, 20));
    }
    private void f18() {
        employmentType = InputHelper.askChoice(18, "Employment type", employmentType,
                "FULL_TIME", "PART_TIME");
    }

    // ---------------- Step 4: Review / Edit / Save ----------------
    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (01-18): ", 1, 18);
        switch (n) {
            case 1 -> f01();   case 2 -> f02();   case 3 -> f03();
            case 4 -> f04();   case 5 -> f05();   case 6 -> f06();
            case 7 -> f07();   case 8 -> f08();   case 9 -> f09();
            case 10 -> f10();  case 11 -> f11();  case 12 -> f12();
            case 13 -> f13();  case 14 -> f14();  case 15 -> f15();
            case 16 -> f16();  case 17 -> f17();  default -> f18();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-20s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private Teacher review() {
        System.out.println("\n+---------------- REVIEW TEACHER ---------------------------+");
        System.out.println("| PERSON");
        row(1, "Person ID", personId);
        row(2, "First name", firstName);
        row(3, "Last name", lastName);
        row(4, "Email", email);
        row(5, "Phone", phone);
        row(6, "Date of birth", dob);
        System.out.println("| ACADEMIC MEMBER");
        row(7, "Member number", memberNumber);
        row(8, "Department", department == null ? null : department.getRegistrationSummary());
        row(9, "Join date", joinDate);
        row(10, "Academic status", academicStatus);
        row(11, "Institutional email", institutionalEmail);
        row(12, "Advisor name", advisorName);
        System.out.println("| TEACHER");
        row(13, "Employee number", employeeNumber);
        row(14, "Specialization", specialization);
        row(15, "Hire date", hireDate);
        row(16, "Salary", salary);
        row(17, "Office number", officeNumber);
        row(18, "Employment type", employmentType);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save teacher   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Teacher t = new Teacher(personId, firstName, lastName, email, phone, dob,
                memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName,
                employeeNumber, specialization, hireDate, salary, officeNumber, employmentType);
        try {
            repo.addTeacher(t);
            System.out.println("+ Teacher " + employeeNumber + " registered successfully.");
            return t;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}