package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;
import com.auca.util.Validator;

public class Department implements Identifiable, Registrable {
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher;

    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    @Override public String getId() { return departmentId; }
    @Override public String getDisplayName() { return departmentName; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(departmentId, "Department ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(departmentName, "Department name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Validator.requireLength(departmentCode, "Department code", 2, 10);
        Validator.requireLength(officeLocation, "Office location", 2, 100);
        Validator.requireEmail(contactEmail, "Contact email");
        Validator.requireName(departmentName, "Department name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + departmentId + "] " + departmentName;
    }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String v) { this.departmentId = v; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String v) { this.departmentName = v; }
    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String v) { this.departmentCode = v; }
    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String v) { this.officeLocation = v; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String v) { this.contactEmail = v; }
    public Teacher getHeadTeacher() { return headTeacher; }
    public void setHeadTeacher(Teacher t) { this.headTeacher = t; }

    @Override
    public String toString() {
        return "Department{id=" + departmentId + ", name=" + departmentName
             + ", code=" + departmentCode + ", office=" + officeLocation
             + ", email=" + contactEmail
             + ", head=" + (headTeacher == null ? "none" : headTeacher.getDisplayName()) + "}";
    }
}