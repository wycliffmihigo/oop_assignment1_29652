package com.auca.util;

import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.Scanner;

public final class InputHelper {
    private static final Scanner SCANNER = new Scanner(System.in);   // the ONE shared Scanner

    @FunctionalInterface
    public interface Check {
        void test(String value) throws ValidationException, DuplicateRecordException;
    }

    @FunctionalInterface
    public interface DateRule {
        void test(LocalDate date) throws ValidationException;
    }

    private InputHelper() { }

    /** Reads one line. No BACK/CANCEL handling (used for menus). */
    public static String readRaw(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            System.out.println("\nInput closed. Exiting.");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }

    /** Reads one line; BACK and CANCEL jump out of the form. */
    public static String readLine(String prompt) {
        String text = readRaw(prompt);
        if (text.equalsIgnoreCase("BACK")) throw new FormNavigationException(false);
        if (text.equalsIgnoreCase("CANCEL")) throw new FormNavigationException(true);
        return text;
    }

    /** Numbered menu choice between min and max. Never crashes on bad input. */
    public static int askMenu(String prompt, int min, int max) {
        while (true) {
            String text = readRaw(prompt);
            try {
                int n = Integer.parseInt(text);
                if (n >= min && n <= max) return n;
                System.out.println("! Choose a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("! '" + text + "' is not a number. Enter " + min + "-" + max + ".");
            }
        }
    }

    /** Generic text field: read, check, show the reason, ask the SAME field again. */
    public static String ask(int no, String label, String format, String current, Check check) {
        while (true) {
            String hint = (current == null) ? "" : " (ENTER keeps: " + current + ")";
            String text = readLine(String.format("%02d %-20s [%s]%s : ", no, label, format, hint));
            if (text.isEmpty() && current != null) text = current;
            try {
                check.test(text);
                return text;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! " + e.getMessage());
                System.out.println("  Please re-enter field " + String.format("%02d", no) + ".");
            }
        }
    }

    public static int askInt(int no, String label, String format, Integer current, int min, int max) {
        String text = ask(no, label, format, current == null ? null : current.toString(), v -> {
            try {
                Validator.requireRange(Integer.parseInt(v.trim()), label, min, max);
            } catch (NumberFormatException e) {
                throw new ValidationException(label + " must be a whole number.");
            }
        });
        return Integer.parseInt(text.trim());
    }

    public static LocalDate askDate(int no, String label, LocalDate current, DateRule rule) {
        String text = ask(no, label, "LocalDate | YYYY-MM-DD",
                current == null ? null : current.toString(),
                v -> rule.test(Validator.parseDate(v, label)));
        return LocalDate.parse(text.trim());
    }

    /** Choice by number or by name, e.g. [1 ACTIVE / 2 INACTIVE]. */
    public static String askChoice(int no, String label, String current, String... options) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            if (i > 0) format.append(" / ");
            format.append(i + 1).append(' ').append(options[i]);
        }
        String text = ask(no, label, format.toString(), current, v -> {
            if (resolve(v, options) == null)
                throw new ValidationException(label + " must be " + String.join(" or ", options)
                        + " (type the number or the word).");
        });
        return resolve(text, options);
    }

    private static String resolve(String v, String[] options) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(v.trim()) || String.valueOf(i + 1).equals(v.trim()))
                return options[i];
        }
        return null;
    }
}