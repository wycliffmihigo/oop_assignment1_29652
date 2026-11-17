package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import com.auca.util.Validator;

import java.time.LocalDate;

public class Administrator extends Person {
    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email,
                         String phoneNumber, LocalDate dateOfBirth,
                         String staffNumber, String jobTitle, String assignedOffice,
                         LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override public String getRoleDescription() { return "Administrator"; }
    @Override public boolean isActive() { return active; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireId(staffNumber, "Staff number", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(jobTitle, "Job title", 2, 100);
        Validator.requireLength(assignedOffice, "Assigned office", 1, 100);
        Validator.requireNotFuture(employmentDate, "Employment date");
        Validator.requireRange(accessLevel, "Access level", 1, 5);
        Validator.requireNotBefore(employmentDate, getDateOfBirth(), "Employment date", "date of birth");
    }

    public String getStaffNumber() { return staffNumber; }
    public void setStaffNumber(String v) { this.staffNumber = v; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String v) { this.jobTitle = v; }
    public String getAssignedOffice() { return assignedOffice; }
    public void setAssignedOffice(String v) { this.assignedOffice = v; }
    public LocalDate getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDate v) { this.employmentDate = v; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int v) { this.accessLevel = v; }
    public void setActive(boolean v) { this.active = v; }

    @Override
    public String toString() {
        return super.toString() + ", staffNo=" + staffNumber + ", title=" + jobTitle
             + ", office=" + assignedOffice + ", employed=" + employmentDate
             + ", access=" + accessLevel + ", active=" + active + "}";
    }
}