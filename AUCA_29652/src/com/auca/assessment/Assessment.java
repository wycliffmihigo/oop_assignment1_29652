package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Validator;

import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;      // QUIZ, ASSIGNMENT, MIDTERM, FINAL_EXAM
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType,
                      double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override public String getId() { return assessmentId; }

    @Override
    public String getDisplayName() {
        return assessmentType + " - " + (enrollment == null ? "?" : enrollment.getDisplayName());
    }

    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(assessmentId, "Assessment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireNotNull(enrollment, "Enrollment");
        if (!enrollment.isActive())
            throw new ValidationException("Only an active enrollment can be assessed.");
        Validator.requireOneOf(assessmentType, "Assessment type",
                "QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM");
        if (maximumScore <= 0)
            throw new ValidationException("Maximum score must be greater than zero.");
        if (obtainedScore < 0 || obtainedScore > maximumScore)
            throw new ValidationException("Obtained score must be between 0 and " + maximumScore + ".");
        Validator.requireNotFuture(assessmentDate, "Assessment date");
        Validator.requireNotBefore(assessmentDate, enrollment.getEnrollmentDate(), "Assessment date", "enrollment date");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + assessmentId + "] " + getDisplayName() + ": " + obtainedScore + "/" + maximumScore;
    }

    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String v) { this.assessmentId = v; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment v) { this.enrollment = v; }
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String v) { this.assessmentType = v; }
    public double getMaximumScore() { return maximumScore; }
    public void setMaximumScore(double v) { this.maximumScore = v; }
    public double getObtainedScore() { return obtainedScore; }
    public void setObtainedScore(double v) { this.obtainedScore = v; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate v) { this.assessmentDate = v; }

    @Override
    public String toString() {
        return "Assessment{id=" + assessmentId
             + ", enrollment=" + (enrollment == null ? "none" : enrollment.getDisplayName())
             + ", type=" + assessmentType + ", score=" + obtainedScore + "/" + maximumScore
             + ", date=" + assessmentDate + "}";
    }
}