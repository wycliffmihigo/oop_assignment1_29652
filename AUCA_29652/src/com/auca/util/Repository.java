package com.auca.util;

import com.auca.exceptions.BookUnavailableException;
import java.time.LocalDate;
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
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.interfaces.Identifiable;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private final List<Department> departments = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private final List<Teacher> teachers = new ArrayList<>();
    private final List<Administrator> administrators = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<Classroom> classrooms = new ArrayList<>();
    private final List<Enrollment> enrollments = new ArrayList<>();
    private final List<Assessment> assessments = new ArrayList<>();
    private final List<FeePayment> payments = new ArrayList<>();
    private final List<LibraryBook> books = new ArrayList<>();
    private final List<BookLoan> loans = new ArrayList<>();

    public List<Department> getDepartments() { return departments; }
    public List<Student> getStudents() { return students; }
    public List<Teacher> getTeachers() { return teachers; }
    public List<Administrator> getAdministrators() { return administrators; }
    public List<Course> getCourses() { return courses; }
    public List<Classroom> getClassrooms() { return classrooms; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Assessment> getAssessments() { return assessments; }
    public List<FeePayment> getPayments() { return payments; }
    public List<LibraryBook> getBooks() { return books; }
    public List<BookLoan> getLoans() { return loans; }

    // ---------- helper views ----------
    private List<Person> allPeople() {
        List<Person> all = new ArrayList<>();
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        return all;
    }

    private List<AcademicMember> allAcademicMembers() {
        List<AcademicMember> all = new ArrayList<>();
        all.addAll(students);
        all.addAll(teachers);
        return all;
    }

    // ---------- uniqueness rules (throw DuplicateRecordException) ----------
    public void requireUniquePersonId(String id) throws DuplicateRecordException {
        for (Person p : allPeople())
            if (p.getPersonId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Person ID " + id + " already exists.");
    }

    public void requireUniqueEmail(String email) throws DuplicateRecordException {
        for (Person p : allPeople())
            if (p.getEmail().equalsIgnoreCase(email.trim()))
                throw new DuplicateRecordException("Email " + email + " is already used.");
    }

    public void requireUniqueMemberNumber(String number) throws DuplicateRecordException {
        for (AcademicMember m : allAcademicMembers())
            if (m.getMemberNumber().equalsIgnoreCase(number.trim()))
                throw new DuplicateRecordException("Member number " + number + " already exists.");
    }

    public void requireUniqueInstitutionalEmail(String email) throws DuplicateRecordException {
        for (AcademicMember m : allAcademicMembers())
            if (m.getInstitutionalEmail().equalsIgnoreCase(email.trim()))
                throw new DuplicateRecordException("Institutional email " + email + " is already used.");
    }

    public void requireUniqueStudentNumber(String number) throws DuplicateRecordException {
        for (Student s : students)
            if (s.getStudentNumber().equalsIgnoreCase(number.trim()))
                throw new DuplicateRecordException("Student number " + number + " already exists.");
    }

    public void requireUniqueDepartmentId(String id) throws DuplicateRecordException {
        for (Department d : departments)
            if (d.getDepartmentId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Department ID " + id + " already exists.");
    }

    public void requireUniqueDepartmentCode(String code) throws DuplicateRecordException {
        for (Department d : departments)
            if (d.getDepartmentCode().equalsIgnoreCase(code.trim()))
                throw new DuplicateRecordException("Department code " + code + " already exists.");
    }

    // ---------- add operations: re-validate, re-check uniqueness, then store ----------
    public void addDepartment(Department d) throws ValidationException, DuplicateRecordException {
        d.validate();
        requireUniqueDepartmentId(d.getDepartmentId());
        requireUniqueDepartmentCode(d.getDepartmentCode());
        departments.add(d);
    }

    public void addStudent(Student s) throws ValidationException, DuplicateRecordException {
        s.validate();
        requireUniquePersonId(s.getPersonId());
        requireUniqueEmail(s.getEmail());
        requireUniqueMemberNumber(s.getMemberNumber());
        requireUniqueInstitutionalEmail(s.getInstitutionalEmail());
        requireUniqueStudentNumber(s.getStudentNumber());
        students.add(s);
    }
        public void requireUniqueEmployeeNumber(String number) throws DuplicateRecordException {
        for (Teacher t : teachers)
            if (t.getEmployeeNumber().equalsIgnoreCase(number.trim()))
                throw new DuplicateRecordException("Employee number " + number + " already exists.");
    }

    public void addTeacher(Teacher t) throws ValidationException, DuplicateRecordException {
        t.validate();
        requireUniquePersonId(t.getPersonId());
        requireUniqueEmail(t.getEmail());
        requireUniqueMemberNumber(t.getMemberNumber());
        requireUniqueInstitutionalEmail(t.getInstitutionalEmail());
        requireUniqueEmployeeNumber(t.getEmployeeNumber());
        teachers.add(t);
    }
        // ---------- Administrator ----------
    public void requireUniqueStaffNumber(String number) throws DuplicateRecordException {
        for (Administrator a : administrators)
            if (a.getStaffNumber().equalsIgnoreCase(number.trim()))
                throw new DuplicateRecordException("Staff number " + number + " already exists.");
    }

    public void addAdministrator(Administrator a) throws ValidationException, DuplicateRecordException {
        a.validate();
        requireUniquePersonId(a.getPersonId());
        requireUniqueEmail(a.getEmail());
        requireUniqueStaffNumber(a.getStaffNumber());
        administrators.add(a);
    }

    // ---------- Classroom ----------
    public void requireUniqueClassroomId(String id) throws DuplicateRecordException {
        for (Classroom c : classrooms)
            if (c.getClassroomId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Classroom ID " + id + " already exists.");
    }

    public void addClassroom(Classroom c) throws ValidationException, DuplicateRecordException {
        c.validate();
        requireUniqueClassroomId(c.getClassroomId());
        for (Classroom other : classrooms)
            if (other.getBuildingName().equalsIgnoreCase(c.getBuildingName())
                    && other.getRoomNumber().equalsIgnoreCase(c.getRoomNumber()))
                throw new DuplicateRecordException("Room " + c.getDisplayName() + " is already registered.");
        classrooms.add(c);
    }

    // ---------- Library book ----------
    public void requireUniqueBookId(String id) throws DuplicateRecordException {
        for (LibraryBook b : books)
            if (b.getBookId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Book ID " + id + " already exists.");
    }

    public void requireUniqueIsbn(String isbn) throws DuplicateRecordException {
        for (LibraryBook b : books)
            if (b.getIsbn().equalsIgnoreCase(isbn.trim()))
                throw new DuplicateRecordException("ISBN " + isbn + " already exists.");
    }

    public void addBook(LibraryBook b) throws ValidationException, DuplicateRecordException {
        b.validate();
        requireUniqueBookId(b.getBookId());
        requireUniqueIsbn(b.getIsbn());
        books.add(b);
    }
        // ---------- Course ----------
    public void requireUniqueCourseId(String id) throws DuplicateRecordException {
        for (Course c : courses)
            if (c.getCourseId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Course ID " + id + " already exists.");
    }

    public void requireUniqueCourseCode(String code) throws DuplicateRecordException {
        for (Course c : courses)
            if (c.getCourseCode().equalsIgnoreCase(code.trim()))
                throw new DuplicateRecordException("Course code " + code + " already exists.");
    }

    public void addCourse(Course c) throws ValidationException, DuplicateRecordException {
        c.validate();
        requireUniqueCourseId(c.getCourseId());
        requireUniqueCourseCode(c.getCourseCode());
        courses.add(c);
    }

    // ---------- Fee payment ----------
    public void requireUniquePaymentId(String id) throws DuplicateRecordException {
        for (FeePayment p : payments)
            if (p.getPaymentId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Payment ID " + id + " already exists.");
    }

    public void requireUniqueReceipt(String receipt) throws DuplicateRecordException {
        for (FeePayment p : payments)
            if (p.getReceiptNumber().equalsIgnoreCase(receipt.trim()))
                throw new DuplicateRecordException("Receipt number " + receipt + " already exists.");
    }

    public void addPayment(FeePayment p) throws ValidationException, DuplicateRecordException {
        p.validate();
        requireUniquePaymentId(p.getPaymentId());
        requireUniqueReceipt(p.getReceiptNumber());
        payments.add(p);
    }

    // ---------- Enrollment ----------
    public void requireUniqueEnrollmentId(String id) throws DuplicateRecordException {
        for (Enrollment e : enrollments)
            if (e.getEnrollmentId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Enrollment ID " + id + " already exists.");
    }

    public void addEnrollment(Enrollment e) throws ValidationException, DuplicateRecordException {
        e.validate();
        requireUniqueEnrollmentId(e.getEnrollmentId());
        if ("ACTIVE".equals(e.getEnrollmentStatus())) {
            for (Enrollment o : enrollments)
                if (o.isActive() && o.getStudent() == e.getStudent()
                        && o.getCourse() == e.getCourse()
                        && o.getSemester().equals(e.getSemester()))
                    throw new DuplicateRecordException("This student already has an active enrollment in "
                            + e.getCourse().getCourseCode() + " for " + e.getSemester() + ".");
        }
        enrollments.add(e);
        
    }
        // ---------- Assessment ----------
    public void requireUniqueAssessmentId(String id) throws DuplicateRecordException {
        for (Assessment a : assessments)
            if (a.getAssessmentId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Assessment ID " + id + " already exists.");
    }

    public void addAssessment(Assessment a) throws ValidationException, DuplicateRecordException {
        a.validate();                       // also checks the enrollment is ACTIVE
        requireUniqueAssessmentId(a.getAssessmentId());
        assessments.add(a);
    }

    // ---------- Book loans ----------
    public void requireUniqueLoanId(String id) throws DuplicateRecordException {
        for (BookLoan l : loans)
            if (l.getLoanId().equalsIgnoreCase(id.trim()))
                throw new DuplicateRecordException("Loan ID " + id + " already exists.");
    }

    public void borrowBook(BookLoan loan)
            throws ValidationException, DuplicateRecordException, BookUnavailableException {
        loan.validate();
        requireUniqueLoanId(loan.getLoanId());
        LibraryBook book = loan.getBook();
        if (book.getAvailableCopies() <= 0)
            throw new BookUnavailableException("No copy of \"" + book.getTitle() + "\" is available.");
        for (BookLoan o : loans)
            if (o.isActive() && o.getBook() == book && o.getStudent() == loan.getStudent())
                throw new DuplicateRecordException("This student already has this book on loan.");
        book.setAvailableCopies(book.getAvailableCopies() - 1);   // exactly once, after all checks
        loans.add(loan);
    }

    public void returnBook(BookLoan loan, LocalDate returned) throws ValidationException {
        if (!loan.isActive())
            throw new ValidationException("This loan was already returned.");
        Validator.requireNotFuture(returned, "Return date");
        if (returned.isBefore(loan.getBorrowedDate()))
            throw new ValidationException("Return date cannot be before the borrowed date.");
        LibraryBook book = loan.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies())
            throw new ValidationException("Stock error: all copies are already in the library.");
        loan.setReturnedDate(returned);
        book.setAvailableCopies(book.getAvailableCopies() + 1);   // exactly once
    }
        // ---------- search across every record type ----------
    public List<Identifiable> allRecords() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(payments);
        all.addAll(books);
        all.addAll(loans);
        return all;
    }

    public List<Identifiable> findById(String id) throws RecordNotFoundException {
        List<Identifiable> found = new ArrayList<>();
        for (Identifiable r : allRecords())
            if (r.getId().equalsIgnoreCase(id.trim())) found.add(r);
        if (found.isEmpty())
            throw new RecordNotFoundException("No record with ID '" + id.trim() + "' was found.");
        return found;
    }
}