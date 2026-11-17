package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Validator;

import java.time.LocalDate;

public abstract class Person implements Identifiable, Registrable {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    protected Person(String personId, String firstName, String lastName,
                     String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public abstract String getRoleDescription();

    @Override public String getId() { return personId; }
    @Override public String getDisplayName() { return firstName + " " + lastName; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(personId, "Person ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireName(firstName, "First name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Validator.requireName(lastName, "Last name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Validator.requireEmail(email, "Email");
        Validator.requirePhone(phoneNumber, "Phone");
        Validator.requirePast(dateOfBirth, "Date of birth");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + personId + "] " + getDisplayName() + " (" + getRoleDescription() + ")";
    }

    public String getPersonId() { return personId; }
    public void setPersonId(String v) { this.personId = v; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String v) { this.phoneNumber = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v) { this.dateOfBirth = v; }

    @Override
    public String toString() {
        return getRoleDescription() + "{id=" + personId + ", name=" + getDisplayName()
             + ", email=" + email + ", phone=" + phoneNumber + ", dob=" + dateOfBirth;
    }
}