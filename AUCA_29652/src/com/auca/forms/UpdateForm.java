package com.auca.forms;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class UpdateForm {
    private final Repository repo;

    /** Re-validates the whole record after a change. */
    private interface Verifier {
        void verify() throws ValidationException, DuplicateRecordException;
    }
    private record Change(String newText, Runnable commit, Runnable revert) { }
    private record Field(String label, Supplier<String> current, IntFunction<Change> editor) { }
    private record Target(Registrable item, List<Field> fields, Verifier verifier) { }

    public UpdateForm(Repository repo) { this.repo = repo; }

    public void run() {
        try {
            Target t = chooseTarget();
            if (t != null) editLoop(t);
        } catch (FormNavigationException e) {
            System.out.println("Update cancelled. Nothing was changed.");
        }
    }

    // ---------------- choose what to update ----------------
    private Target chooseTarget() {
        String[] types = {"Department", "Student", "Teacher", "Administrator", "Course", "Classroom",
                "Enrollment", "Assessment", "Fee Payment", "Library Book", "Book Loan"};
        System.out.println("\n+---------------- UPDATE RECORD: CHOOSE TYPE ---------------+");
        for (int i = 0; i < types.length; i++)
            System.out.println("  " + (i + 1) + " " + types[i]);
        System.out.println("  0 Cancel");
        int c = InputHelper.askMenu("Select type: ", 0, types.length);
        return switch (c) {
            case 1 -> department();
            case 2 -> student();
            case 3 -> teacher();
            case 4 -> administrator();
            case 5 -> course();
            case 6 -> classroom();
            case 7 -> enrollment();
            case 8 -> assessment();
            case 9 -> payment();
            case 10 -> book();
            case 11 -> loan();
            default -> null;
        };
    }

    private <T extends Registrable> T choose(String title, List<T> list) {
        if (list.isEmpty()) {
            System.out.println("No " + title.toLowerCase() + " record is registered yet.");
            return null;
        }
        System.out.println("\n+----------------- CHOOSE " + title + " ----------------+");
        for (int i = 0; i < list.size(); i++)
            System.out.println("  " + (i + 1) + " " + list.get(i).getRegistrationSummary());
        System.out.println("  0 Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, list.size());
        return c == 0 ? null : list.get(c - 1);
    }

    // ---------------- edit loop ----------------
    private void editLoop(Target t) {
        while (true) {
            System.out.println("\n+---------------- EDIT: " + t.item().getRegistrationSummary());
            List<Field> f = t.fields();
            for (int i = 0; i < f.size(); i++)
                System.out.printf("| %02d %-22s: %s%n", i + 1, f.get(i).label(), f.get(i).current().get());
            System.out.println("| IDs cannot be changed. Returns are done with option 12.");
            int n = InputHelper.askMenu("Field number to edit (0 = finish): ", 0, f.size());
            if (n == 0) return;

            Field field = f.get(n - 1);
            Change ch;
            try {
                ch = field.editor().apply(n);
            } catch (FormNavigationException e) {
                System.out.println("Edit cancelled. Nothing was changed.");
                continue;
            }
            System.out.println("Change " + field.label() + ":  " + field.current().get()
                    + "  ->  " + ch.newText());
            System.out.println("[1] Confirm change   [0] Discard");
            if (InputHelper.askMenu("Select option: ", 0, 1) == 0) {
                System.out.println("Change discarded.");
                continue;
            }
            ch.commit().run();
            try {
                t.verifier().verify();                       // validate again
                System.out.println("+ " + field.label() + " updated successfully.");
            } catch (ValidationException | DuplicateRecordException e) {
                ch.revert().run();                           // undo: nothing half-saved
                System.out.println("! Cannot save: " + e.getMessage());
                System.out.println("  The old value was kept.");
            }
        }
    }

    // ---------------- field builders ----------------
    private static Field text(String label, String format, Supplier<String> get,
                              Consumer<String> set, InputHelper.Check check) {
        return new Field(label, get, no -> {
            String old = get.get();
            String v = InputHelper.ask(no, label, format, old, check);
            return new Change(v, () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static Field date(String label, Supplier<LocalDate> get, Consumer<LocalDate> set,
                              InputHelper.DateRule rule) {
        return new Field(label, () -> String.valueOf(get.get()), no -> {
            LocalDate old = get.get();
            LocalDate v = InputHelper.askDate(no, label, old, rule);
            return new Change(v.toString(), () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static Field integer(String label, String format, Supplier<Integer> get,
                                 Consumer<Integer> set, int min, int max) {
        return new Field(label, () -> String.valueOf(get.get()), no -> {
            int old = get.get();
            int v = InputHelper.askInt(no, label, format, old, min, max);
            return new Change(String.valueOf(v), () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static Field choice(String label, Supplier<String> get, Consumer<String> set,
                                String... options) {
        return new Field(label, get, no -> {
            String old = get.get();
            String v = InputHelper.askChoice(no, label, old, options);
            return new Change(v, () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static Field flag(String label, Supplier<Boolean> get, Consumer<Boolean> set) {
        return new Field(label, () -> get.get() ? "YES" : "NO", no -> {
            boolean old = get.get();
            boolean v = InputHelper.askChoice(no, label, old ? "YES" : "NO", "YES", "NO").equals("YES");
            return new Change(v ? "YES" : "NO", () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static Field money(String label, Supplier<BigDecimal> get, Consumer<BigDecimal> set) {
        return new Field(label, () -> get.get().toPlainString(), no -> {
            BigDecimal old = get.get();
            String text = InputHelper.ask(no, label, "BigDecimal | > 0, max 2 decimals",
                    old.toPlainString(), x -> Validator.parseMoney(x, label));
            BigDecimal v = new BigDecimal(text.trim());
            return new Change(v.toPlainString(), () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static Field number(String label, Supplier<Double> get, Consumer<Double> set) {
        return new Field(label, () -> String.valueOf(get.get()), no -> {
            double old = get.get();
            String text = InputHelper.ask(no, label, "double | number", String.valueOf(old),
                    x -> Validator.parseScore(x, label));
            double v = Double.parseDouble(text.trim());
            return new Change(String.valueOf(v), () -> set.accept(v), () -> set.accept(old));
        });
    }

    private static <T> Field ref(String label, Supplier<T> get, Function<T, String> show,
                                 Supplier<T> picker, Consumer<T> set) {
        return new Field(label, () -> get.get() == null ? "(none)" : show.apply(get.get()), no -> {
            T old = get.get();
            T v = picker.get();                    // numbered list of real records
            return new Change(show.apply(v), () -> set.accept(v), () -> set.accept(old));
        });
    }

    /** A unique value may be kept as it is; only a CHANGED value is checked for duplicates. */
    private static InputHelper.Check unlessSame(Supplier<String> current, InputHelper.Check check) {
        return v -> {
            if (!v.trim().equalsIgnoreCase(current.get())) check.test(v);
        };
    }

    // ---------------- shared field groups ----------------
    private List<Field> personFields(Person p) {
        List<Field> f = new ArrayList<>();
        f.add(text("First name", "String | letters, 2-60", p::getFirstName, p::setFirstName,
                v -> Validator.requireName(v, "First name", 2, 60)));
        f.add(text("Last name", "String | letters, 2-60", p::getLastName, p::setLastName,
                v -> Validator.requireName(v, "Last name", 2, 60)));
        f.add(text("Email", "String | unique | email", p::getEmail, p::setEmail,
                unlessSame(p::getEmail, v -> {
                    Validator.requireEmail(v, "Email");
                    repo.requireUniqueEmail(v.trim());
                })));
        f.add(text("Phone", "String | 10-15 digits", p::getPhoneNumber, p::setPhoneNumber,
                v -> Validator.requirePhone(v, "Phone")));
        f.add(date("Date of birth", p::getDateOfBirth, p::setDateOfBirth,
                d -> Validator.requirePast(d, "Date of birth")));
        return f;
    }

    private List<Field> academicFields(AcademicMember m) {
        List<Field> f = new ArrayList<>();
        f.add(text("Member number", "String | unique | 4-30", m::getMemberNumber, m::setMemberNumber,
                unlessSame(m::getMemberNumber, v -> {
                    Validator.requireId(v, "Member number", 4, 30);
                    repo.requireUniqueMemberNumber(v.trim());
                })));
        f.add(ref("Department", m::getDepartment, Department::getRegistrationSummary,
                () -> Selectors.pickDepartment(repo), m::setDepartment));
        f.add(date("Join date", m::getJoinDate, m::setJoinDate, d -> {
            Validator.requireNotFuture(d, "Join date");
            Validator.requireNotBefore(d, m.getDateOfBirth(), "Join date", "date of birth");
        }));
        f.add(choice("Academic status", m::getAcademicStatus, m::setAcademicStatus,
                "ACTIVE", "INACTIVE"));
        f.add(text("Institutional email", "String | unique | email", m::getInstitutionalEmail,
                m::setInstitutionalEmail, unlessSame(m::getInstitutionalEmail, v -> {
                    Validator.requireEmail(v, "Institutional email");
                    repo.requireUniqueInstitutionalEmail(v.trim());
                })));
        f.add(text("Advisor name", "String | letters, 2-120", m::getAdvisorName, m::setAdvisorName,
                v -> Validator.requireName(v, "Advisor name", 2, 120)));
        return f;
    }

    // ---------------- one method per record type ----------------
    private Target department() {
        Department d = choose("DEPARTMENT", repo.getDepartments());
        if (d == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(text("Department name", "String | letters, 2-60", d::getDepartmentName,
                d::setDepartmentName, v -> Validator.requireName(v, "Department name", 2, 60)));
        f.add(text("Department code", "String | unique | 2-10", d::getDepartmentCode,
                d::setDepartmentCode, unlessSame(d::getDepartmentCode, v -> {
                    Validator.requireLength(v, "Department code", 2, 10);
                    repo.requireUniqueDepartmentCode(v.trim());
                })));
        f.add(text("Office location", "String | 2-100", d::getOfficeLocation, d::setOfficeLocation,
                v -> Validator.requireLength(v, "Office location", 2, 100)));
        f.add(text("Contact email", "String | email", d::getContactEmail, d::setContactEmail,
                v -> Validator.requireEmail(v, "Contact email")));
        f.add(ref("Head teacher", d::getHeadTeacher, Teacher::getRegistrationSummary,
                () -> Selectors.pickTeacher(repo), d::setHeadTeacher));
        return new Target(d, f, d::validate);
    }

    private Target student() {
        Student s = choose("STUDENT", repo.getStudents());
        if (s == null) return null;
        List<Field> f = personFields(s);
        f.addAll(academicFields(s));
        f.add(text("Student number", "String | unique | 4-30", s::getStudentNumber, s::setStudentNumber,
                unlessSame(s::getStudentNumber, v -> {
                    Validator.requireId(v, "Student number", 4, 30);
                    repo.requireUniqueStudentNumber(v.trim());
                })));
        f.add(text("Program name", "String | 2-120", s::getProgramName, s::setProgramName,
                v -> Validator.requireHasLetter(v, "Program name", 2, 120)));
        f.add(date("Admission date", s::getAdmissionDate, s::setAdmissionDate, d -> {
            Validator.requireNotFuture(d, "Admission date");
            Validator.requireNotBefore(d, s.getDateOfBirth(), "Admission date", "date of birth");
        }));
        f.add(integer("Study year", "int | " + Validator.MIN_STUDY_YEAR + "-" + Validator.MAX_STUDY_YEAR,
                s::getStudyYear, s::setStudyYear, Validator.MIN_STUDY_YEAR, Validator.MAX_STUDY_YEAR));
        f.add(text("Guardian phone", "String | 10-15 digits", s::getGuardianPhone, s::setGuardianPhone,
                v -> Validator.requirePhone(v, "Guardian phone")));
        f.add(flag("Scholarship", s::isScholarshipStatus, s::setScholarshipStatus));
        return new Target(s, f, s::validate);
    }

    private Target teacher() {
        Teacher t = choose("TEACHER", repo.getTeachers());
        if (t == null) return null;
        List<Field> f = personFields(t);
        f.addAll(academicFields(t));
        f.add(text("Employee number", "String | unique | 4-30", t::getEmployeeNumber,
                t::setEmployeeNumber, unlessSame(t::getEmployeeNumber, v -> {
                    Validator.requireId(v, "Employee number", 4, 30);
                    repo.requireUniqueEmployeeNumber(v.trim());
                })));
        f.add(text("Specialization", "String | 2-100", t::getSpecialization, t::setSpecialization,
                v -> Validator.requireHasLetter(v, "Specialization", 2, 100)));
        f.add(date("Hire date", t::getHireDate, t::setHireDate, d -> {
            Validator.requireNotFuture(d, "Hire date");
            Validator.requireNotBefore(d, t.getDateOfBirth(), "Hire date", "date of birth");
        }));
        f.add(money("Salary", t::getSalary, t::setSalary));
        f.add(text("Office number", "String | 1-20", t::getOfficeNumber, t::setOfficeNumber,
                v -> Validator.requireLength(v, "Office number", 1, 20)));
        f.add(choice("Employment type", t::getEmploymentType, t::setEmploymentType,
                "FULL_TIME", "PART_TIME"));
        return new Target(t, f, t::validate);
    }

    private Target administrator() {
        Administrator a = choose("ADMINISTRATOR", repo.getAdministrators());
        if (a == null) return null;
        List<Field> f = personFields(a);
        f.add(text("Staff number", "String | unique | 4-30", a::getStaffNumber, a::setStaffNumber,
                unlessSame(a::getStaffNumber, v -> {
                    Validator.requireId(v, "Staff number", 4, 30);
                    repo.requireUniqueStaffNumber(v.trim());
                })));
        f.add(text("Job title", "String | 2-100", a::getJobTitle, a::setJobTitle,
                v -> Validator.requireHasLetter(v, "Job title", 2, 100)));
        f.add(text("Assigned office", "String | 1-100", a::getAssignedOffice, a::setAssignedOffice,
                v -> Validator.requireLength(v, "Assigned office", 1, 100)));
        f.add(date("Employment date", a::getEmploymentDate, a::setEmploymentDate, d -> {
            Validator.requireNotFuture(d, "Employment date");
            Validator.requireNotBefore(d, a.getDateOfBirth(), "Employment date", "date of birth");
        }));
        f.add(integer("Access level", "int | 1-5", a::getAccessLevel, a::setAccessLevel, 1, 5));
        f.add(flag("Active", a::isActive, a::setActive));
        return new Target(a, f, a::validate);
    }

    private Target course() {
        Course c = choose("COURSE", repo.getCourses());
        if (c == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(text("Course code", "String | unique | 2-20", c::getCourseCode, c::setCourseCode,
                unlessSame(c::getCourseCode, v -> {
                    Validator.requireLength(v, "Course code", 2, 20);
                    repo.requireUniqueCourseCode(v.trim());
                })));
        f.add(text("Course title", "String | 2-120", c::getCourseTitle, c::setCourseTitle,
                v -> Validator.requireHasLetter(v, "Course title", 2, 120)));
        f.add(integer("Credit hours", "int | 1-6", c::getCreditHours, c::setCreditHours, 1, 6));
        f.add(ref("Department", c::getDepartment, Department::getRegistrationSummary,
                () -> Selectors.pickDepartment(repo), c::setDepartment));
        f.add(ref("Assigned teacher", c::getAssignedTeacher, Teacher::getRegistrationSummary,
                () -> Selectors.pickTeacher(repo), c::setAssignedTeacher));
        return new Target(c, f, c::validate);
    }

    private Target classroom() {
        Classroom room = choose("CLASSROOM", repo.getClassrooms());
        if (room == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(text("Building name", "String | 2-100", room::getBuildingName, room::setBuildingName,
                v -> Validator.requireHasLetter(v, "Building name", 2, 100)));
        f.add(text("Room number", "String | 1-20", room::getRoomNumber, room::setRoomNumber,
                v -> Validator.requireLength(v, "Room number", 1, 20)));
        f.add(integer("Capacity", "int | > 0", room::getCapacity, room::setCapacity, 1, 10000));
        f.add(integer("Floor number", "int | 0 or more", room::getFloorNumber, room::setFloorNumber, 0, 200));
        f.add(flag("Available", room::isAvailable, room::setAvailable));
        return new Target(room, f, () -> {
            room.validate();
            for (Classroom o : repo.getClassrooms())
                if (o != room && o.getBuildingName().equalsIgnoreCase(room.getBuildingName())
                        && o.getRoomNumber().equalsIgnoreCase(room.getRoomNumber()))
                    throw new DuplicateRecordException("Room " + room.getDisplayName() + " is already registered.");
        });
    }

    private Target enrollment() {
        Enrollment e = choose("ENROLLMENT", repo.getEnrollments());
        if (e == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(date("Enrollment date", e::getEnrollmentDate, e::setEnrollmentDate,
                d -> Validator.requireNotFuture(d, "Enrollment date")));
        f.add(choice("Semester", e::getSemester, e::setSemester, "FALL", "SPRING", "SUMMER"));
        f.add(choice("Enrollment status", e::getEnrollmentStatus, e::setEnrollmentStatus,
                "ACTIVE", "COMPLETED", "DROPPED"));
        return new Target(e, f, () -> {
            e.validate();
            if (e.isActive())
                for (Enrollment o : repo.getEnrollments())
                    if (o != e && o.isActive() && o.getStudent() == e.getStudent()
                            && o.getCourse() == e.getCourse() && o.getSemester().equals(e.getSemester()))
                        throw new DuplicateRecordException(
                                "Another active enrollment exists for this student, course and semester.");
        });
    }

    private Target assessment() {
        Assessment a = choose("ASSESSMENT", repo.getAssessments());
        if (a == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(choice("Assessment type", a::getAssessmentType, a::setAssessmentType,
                "QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM"));
        f.add(number("Maximum score", a::getMaximumScore, a::setMaximumScore));
        f.add(number("Obtained score", a::getObtainedScore, a::setObtainedScore));
        f.add(date("Assessment date", a::getAssessmentDate, a::setAssessmentDate, d -> {
            Validator.requireNotFuture(d, "Assessment date");
            Validator.requireNotBefore(d, a.getEnrollment().getEnrollmentDate(),
                    "Assessment date", "enrollment date");
        }));
        return new Target(a, f, a::validate);
    }

    private Target payment() {
        FeePayment pay = choose("FEE PAYMENT", repo.getPayments());
        if (pay == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(money("Amount", pay::getAmount, pay::setAmount));
        f.add(date("Payment date", pay::getPaymentDate, pay::setPaymentDate,
                d -> Validator.requireNotFuture(d, "Payment date")));
        f.add(choice("Payment method", pay::getPaymentMethod, pay::setPaymentMethod,
                "CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD"));
        f.add(text("Receipt number", "String | unique", pay::getReceiptNumber, pay::setReceiptNumber,
                unlessSame(pay::getReceiptNumber, v -> {
                    Validator.requireText(v, "Receipt number");
                    repo.requireUniqueReceipt(v.trim());
                })));
        return new Target(pay, f, pay::validate);
    }

    private Target book() {
        LibraryBook b = choose("LIBRARY BOOK", repo.getBooks());
        if (b == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(text("ISBN", "String | unique | ISBN-10/13", b::getIsbn, b::setIsbn,
                unlessSame(b::getIsbn, v -> {
                    Validator.requireIsbn(v, "ISBN");
                    repo.requireUniqueIsbn(v.trim());
                })));
        f.add(text("Title", "String | 1-150", b::getTitle, b::setTitle,
                v -> Validator.requireLength(v, "Title", 1, 150)));
        f.add(text("Author", "String | letters, 2-100", b::getAuthor, b::setAuthor,
                v -> Validator.requireName(v, "Author", 2, 100)));
        f.add(integer("Total copies", "int | 0 or more", b::getTotalCopies, b::setTotalCopies, 0, 100000));
        f.add(integer("Available copies", "int | 0 to total", b::getAvailableCopies,
                b::setAvailableCopies, 0, 100000));
        return new Target(b, f, b::validate);
    }

    private Target loan() {
        BookLoan loan = choose("BOOK LOAN", repo.getLoans());
        if (loan == null) return null;
        List<Field> f = new ArrayList<>();
        f.add(date("Borrowed date", loan::getBorrowedDate, loan::setBorrowedDate,
                d -> Validator.requireNotFuture(d, "Borrowed date")));
        f.add(date("Due date", loan::getDueDate, loan::setDueDate, d -> {
            if (!d.isAfter(loan.getBorrowedDate()))
                throw new ValidationException("Due date must be after the borrowed date.");
        }));
        return new Target(loan, f, loan::validate);
    }
}