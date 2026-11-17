package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.util.Validator;

import java.time.LocalDate;

public class Student extends AcademicMember {
    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName, String email,
                   String phoneNumber, LocalDate dateOfBirth,
                   String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String studentNumber, String programName, LocalDate admissionDate,
                   int studyYear, String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
              memberNumber, department, joinDate, academicStatus,
              institutionalEmail, advisorName);
        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    @Override public String getRoleDescription() { return "Student"; }
    @Override public String getAcademicCategory() { return "Learner"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireId(studentNumber, "Student number", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(programName, "Program name", 2, 120);
        Validator.requireNotFuture(admissionDate, "Admission date");
        Validator.requireRange(studyYear, "Study year",
                Validator.MIN_STUDY_YEAR, Validator.MAX_STUDY_YEAR);
        Validator.requirePhone(guardianPhone, "Guardian phone");
        Validator.requireNotBefore(admissionDate, getDateOfBirth(), "Admission date", "date of birth");
        Validator.requireNotFuture(admissionDate, "Admission date");
    }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String v) { this.studentNumber = v; }
    public String getProgramName() { return programName; }
    public void setProgramName(String v) { this.programName = v; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate v) { this.admissionDate = v; }
    public int getStudyYear() { return studyYear; }
    public void setStudyYear(int v) { this.studyYear = v; }
    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String v) { this.guardianPhone = v; }
    public boolean isScholarshipStatus() { return scholarshipStatus; }
    public void setScholarshipStatus(boolean v) { this.scholarshipStatus = v; }

    @Override
    public String toString() {
        return super.toString() + ", studentNo=" + studentNumber + ", program=" + programName
             + ", admitted=" + admissionDate + ", year=" + studyYear
             + ", guardian=" + guardianPhone + ", scholarship=" + scholarshipStatus + "}";
    }
}