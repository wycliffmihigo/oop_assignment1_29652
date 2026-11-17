package com.auca.util;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern NAME = Pattern.compile("^[\\p{L}][\\p{L} '.\\-]*$");
    private static final Pattern EMAIL =
    Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MONEY = Pattern.compile("^\\d{1,12}(\\.\\d{1,2})?$");
    private static final Pattern NUMBER = Pattern.compile("^\\d{1,9}(\\.\\d{1,2})?$");
    private static final Pattern ID = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_-]*$");
    private static final Pattern PHONE = Pattern.compile("^\\+?\\d+$");

    public static final int MIN_STUDY_YEAR = 1;
    public static final int MAX_STUDY_YEAR = 6;   // change to 8 if your lecturer says so

    private Validator() { }

    public static void requireText(String v, String label) throws ValidationException {
        if (v == null || v.trim().isEmpty())
            throw new ValidationException(label + " must not be blank.");
    }

    public static void requireLength(String v, String label, int min, int max)
            throws ValidationException {
        requireText(v, label);
        int len = v.trim().length();
        if (len < min || len > max)
            throw new ValidationException(label + " must be " + min + "-" + max + " characters.");
    }

    public static void requireEmail(String v, String label) throws ValidationException {
        requireText(v, label);
        if (v.length() > Registrable.MAX_EMAIL_LENGTH || !EMAIL.matcher(v.trim()).matches())
            throw new ValidationException(label + " is not a valid email address.");
    }

    public static void requirePhone(String v, String label) throws ValidationException {
        requireText(v, label);
        String p = v.trim();
        int digits = p.startsWith("+") ? p.length() - 1 : p.length();
        if (!PHONE.matcher(p).matches()
                || digits < Registrable.MIN_PHONE_LENGTH
                || digits > Registrable.MAX_PHONE_LENGTH)
            throw new ValidationException(label + " must contain 10-15 digits (optional leading +).");
    }

    public static LocalDate parseDate(String text, String label) throws ValidationException {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new ValidationException("Invalid " + label + ": use YYYY-MM-DD (example: 2004-05-16).");
        }
    }

    public static void requirePast(LocalDate d, String label) throws ValidationException {
        if (d == null || !d.isBefore(LocalDate.now()))
            throw new ValidationException(label + " must be in the past.");
    }

    public static void requireNotFuture(LocalDate d, String label) throws ValidationException {
        if (d == null || d.isAfter(LocalDate.now()))
            throw new ValidationException(label + " must not be in the future.");
    }

    public static void requireRange(int v, String label, int min, int max)
            throws ValidationException {
        if (v < min || v > max)
            throw new ValidationException(label + " must be between " + min + " and " + max + ".");
    }

    public static void requireOneOf(String v, String label, String... allowed)
            throws ValidationException {
        if (v == null || !Arrays.asList(allowed).contains(v.trim()))
            throw new ValidationException(label + " must be one of: " + String.join(", ", allowed) + ".");
    }

    public static void requirePositive(BigDecimal v, String label) throws ValidationException {
        if (v == null || v.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException(label + " must be greater than zero.");
    }

    public static void requireNotNull(Object v, String label) throws ValidationException {
        if (v == null)
            throw new ValidationException(label + " is required.");
    }
        public static void requireName(String v, String label, int min, int max)
            throws ValidationException {
        requireLength(v, label, min, max);
        if (!NAME.matcher(v.trim()).matches())
            throw new ValidationException(label + " must contain letters only "
                    + "(spaces, hyphens and apostrophes allowed).");
    }
        public static void requireHasLetter(String v, String label, int min, int max)
            throws ValidationException {
        requireLength(v, label, min, max);
        if (!v.matches(".*\\p{L}.*"))
            throw new ValidationException(label + " must contain at least one letter.");
    }
        public static void requireId(String v, String label, int min, int max) throws ValidationException {
        requireLength(v, label, min, max);
        if (!ID.matcher(v.trim()).matches())
            throw new ValidationException(label + " may contain only letters, digits, '-' and '_' (no spaces).");
    }
        public static BigDecimal parseMoney(String v, String label) throws ValidationException {
        if (v == null || !MONEY.matcher(v.trim()).matches())
            throw new ValidationException(label + " must be a positive number with at most 2 decimals, e.g. 150000.50");
        BigDecimal m = new BigDecimal(v.trim());
        requirePositive(m, label);
        return m;
    }

    public static double parseScore(String v, String label) throws ValidationException {
        if (v == null || !NUMBER.matcher(v.trim()).matches())
            throw new ValidationException(label + " must be a number such as 85 or 85.5");
        return Double.parseDouble(v.trim());
    }
        public static void requireIsbn(String v, String label) throws ValidationException {
        requireText(v, label);
        String d = v.trim().replace("-", "");
        if (!(d.matches("\\d{13}") || d.matches("\\d{9}[0-9Xx]")))
            throw new ValidationException(label + " must be a valid ISBN-10 or ISBN-13 (hyphens allowed).");
    }
        public static void requireNotBefore(LocalDate d, LocalDate earliest, String label, String earliestLabel)
            throws ValidationException {
        if (d != null && earliest != null && d.isBefore(earliest))
            throw new ValidationException(label + " cannot be before " + earliestLabel + " (" + earliest + ").");
    }
}