package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.util.Validator;

import java.time.LocalDate;

public abstract class AcademicMember extends Person {
    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    protected AcademicMember(String personId, String firstName, String lastName,
                             String email, String phoneNumber, LocalDate dateOfBirth,
                             String memberNumber, Department department, LocalDate joinDate,
                             String academicStatus, String institutionalEmail,
                             String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public abstract String getAcademicCategory();

    @Override
    public boolean isActive() { return "ACTIVE".equals(academicStatus); }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireName(advisorName, "Advisor name", 2, 120);
        if (department == null)
            throw new ValidationException("Department is required.");
        Validator.requireNotFuture(joinDate, "Join date");
        if (!"ACTIVE".equals(academicStatus) && !"INACTIVE".equals(academicStatus))
            throw new ValidationException("Academic status must be ACTIVE or INACTIVE.");
        Validator.requireEmail(institutionalEmail, "Institutional email");
        Validator.requireLength(advisorName, "Advisor name", 2, 120);
        Validator.requireNotBefore(joinDate, getDateOfBirth(), "Join date", "date of birth");
    }

    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String v) { this.memberNumber = v; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department v) { this.department = v; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate v) { this.joinDate = v; }
    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String v) { this.academicStatus = v; }
    public String getInstitutionalEmail() { return institutionalEmail; }
    public void setInstitutionalEmail(String v) { this.institutionalEmail = v; }
    public String getAdvisorName() { return advisorName; }
    public void setAdvisorName(String v) { this.advisorName = v; }

    @Override
    public String toString() {
        return super.toString() + ", member=" + memberNumber
             + ", dept=" + (department == null ? "none" : department.getDepartmentName())
             + ", joined=" + joinDate + ", status=" + academicStatus
             + ", instEmail=" + institutionalEmail + ", advisor=" + advisorName;
    }
}