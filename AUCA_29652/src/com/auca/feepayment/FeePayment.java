package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import com.auca.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {
    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;       // CASH, MOBILE_MONEY, BANK_TRANSFER, CARD
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount,
                      LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    @Override public String getId() { return paymentId; }
    @Override public String getDisplayName() { return "Receipt " + receiptNumber; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(paymentId, "Payment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireNotNull(student, "Student");
        Validator.requirePositive(amount, "Amount");
        Validator.requireNotFuture(paymentDate, "Payment date");
        Validator.requireOneOf(paymentMethod, "Payment method",
                "CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD");
        Validator.requireText(receiptNumber, "Receipt number");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + paymentId + "] " + (student == null ? "?" : student.getDisplayName())
             + " paid " + amount + " (" + paymentMethod + ")";
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String v) { this.paymentId = v; }
    public Student getStudent() { return student; }
    public void setStudent(Student v) { this.student = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { this.amount = v; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate v) { this.paymentDate = v; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String v) { this.receiptNumber = v; }

    @Override
    public String toString() {
        return "FeePayment{id=" + paymentId
             + ", student=" + (student == null ? "none" : student.getDisplayName())
             + ", amount=" + amount + ", date=" + paymentDate
             + ", method=" + paymentMethod + ", receipt=" + receiptNumber + "}";
    }
}