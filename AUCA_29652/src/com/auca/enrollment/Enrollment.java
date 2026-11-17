package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import com.auca.util.Validator;

import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;            // FALL, SPRING, SUMMER
    private String enrollmentStatus;    // ACTIVE, COMPLETED, DROPPED

    public Enrollment(String enrollmentId, Student student, Course course,
                      LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override public String getId() { return enrollmentId; }

    @Override
    public String getDisplayName() {
        return (student == null ? "?" : student.getDisplayName()) + " in "
             + (course == null ? "?" : course.getCourseCode());
    }

    @Override public boolean isActive() { return "ACTIVE".equals(enrollmentStatus); }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(enrollmentId, "Enrollment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireNotNull(student, "Student");
        Validator.requireNotNull(course, "Course");
        Validator.requireNotFuture(enrollmentDate, "Enrollment date");
        Validator.requireOneOf(semester, "Semester", "FALL", "SPRING", "SUMMER");
        Validator.requireOneOf(enrollmentStatus, "Enrollment status", "ACTIVE", "COMPLETED", "DROPPED");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + enrollmentId + "] " + getDisplayName() + " (" + semester + ", " + enrollmentStatus + ")";
    }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String v) { this.enrollmentId = v; }
    public Student getStudent() { return student; }
    public void setStudent(Student v) { this.student = v; }
    public Course getCourse() { return course; }
    public void setCourse(Course v) { this.course = v; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate v) { this.enrollmentDate = v; }
    public String getSemester() { return semester; }
    public void setSemester(String v) { this.semester = v; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String v) { this.enrollmentStatus = v; }

    @Override
    public String toString() {
        return "Enrollment{id=" + enrollmentId + ", student=" + (student == null ? "none" : student.getDisplayName())
             + ", course=" + (course == null ? "none" : course.getDisplayName())
             + ", date=" + enrollmentDate + ", semester=" + semester
             + ", status=" + enrollmentStatus + "}";
    }
}