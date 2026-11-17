package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Teacher extends AcademicMember {
    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email,
                   String phoneNumber, LocalDate dateOfBirth,
                   String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String employeeNumber, String specialization, LocalDate hireDate,
                   BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
              memberNumber, department, joinDate, academicStatus,
              institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    @Override public String getRoleDescription() { return "Teacher"; }
    @Override public String getAcademicCategory() { return "Faculty"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireId(employeeNumber, "Employee number", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(specialization, "Specialization", 2, 100);
        Validator.requireNotFuture(hireDate, "Hire date");
        Validator.requirePositive(salary, "Salary");
        Validator.requireLength(officeNumber, "Office number", 1, 20);
        Validator.requireOneOf(employmentType, "Employment type", "FULL_TIME", "PART_TIME");
        Validator.requireNotBefore(hireDate, getDateOfBirth(), "Hire date", "date of birth");
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String v) { this.employeeNumber = v; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String v) { this.specialization = v; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate v) { this.hireDate = v; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal v) { this.salary = v; }
    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String v) { this.officeNumber = v; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String v) { this.employmentType = v; }

    @Override
    public String toString() {
        return super.toString() + ", employeeNo=" + employeeNumber + ", specialization="
             + specialization + ", hired=" + hireDate + ", salary=" + salary
             + ", office=" + officeNumber + ", type=" + employmentType + "}";
    }
}