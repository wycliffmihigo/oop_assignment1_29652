package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;
import com.auca.util.Validator;

public class Course implements Identifiable, Registrable {
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle,
                  int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    @Override public String getId() { return courseId; }
    @Override public String getDisplayName() { return courseCode + " - " + courseTitle; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(courseId, "Course ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(courseCode, "Course code", 2, 20);
        Validator.requireLength(courseTitle, "Course title", 2, 120);
        Validator.requireRange(creditHours, "Credit hours", 1, 6);
        Validator.requireNotNull(department, "Department");
        Validator.requireNotNull(assignedTeacher, "Assigned teacher");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + courseId + "] " + getDisplayName() + " (" + creditHours + " credits)";
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String v) { this.courseId = v; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String v) { this.courseCode = v; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String v) { this.courseTitle = v; }
    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int v) { this.creditHours = v; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department v) { this.department = v; }
    public Teacher getAssignedTeacher() { return assignedTeacher; }
    public void setAssignedTeacher(Teacher v) { this.assignedTeacher = v; }

    @Override
    public String toString() {
        return "Course{id=" + courseId + ", code=" + courseCode + ", title=" + courseTitle
             + ", credits=" + creditHours
             + ", dept=" + (department == null ? "none" : department.getDepartmentName())
             + ", teacher=" + (assignedTeacher == null ? "none" : assignedTeacher.getDisplayName()) + "}";
    }
}