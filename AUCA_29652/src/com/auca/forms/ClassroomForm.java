package com.auca.forms;

import com.auca.classroom.Classroom;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.FormNavigationException;
import com.auca.exceptions.ValidationException;
import com.auca.util.InputHelper;
import com.auca.util.Repository;
import com.auca.util.Validator;

public class ClassroomForm {
    private final Repository repo;

    private String classroomId, buildingName, roomNumber;
    private Integer capacity, floorNumber;
    private Boolean available;

    public ClassroomForm(Repository repo) { this.repo = repo; }

    public Classroom run() {
        int step = 1;
        while (true) {
            try {
                if (step == 1) {
                    header();
                    f1(); f2(); f3(); f4(); f5(); f6();
                    step = 2;
                } else {
                    Classroom c = review();
                    if (c != null) return c;
                }
            } catch (FormNavigationException e) {
                if (e.isCancel()) {
                    System.out.println("Classroom registration cancelled. Nothing was saved.");
                    return null;
                }
                step = 1;
            }
        }
    }

    private void header() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| CLASSROOM REGISTRATION                        Step 1 of 2");
        System.out.println("| BACK = restart fields | CANCEL = discard | ENTER = keep value");
        System.out.println("+------------------------------------------------------------+");
    }

    private void f1() {
        classroomId = InputHelper.ask(1, "Classroom ID", "String | unique | 4-30 chars", classroomId, v -> {
            Validator.requireId(v, "Classroom ID", 4, 30);
            repo.requireUniqueClassroomId(v.trim());
        });
    }
    private void f2() {
        buildingName = InputHelper.ask(2, "Building name", "String | 2-100 chars", buildingName,
                v -> Validator.requireLength(v, "Building name", 2, 100));
    }
    private void f3() {
        roomNumber = InputHelper.ask(3, "Room number", "String | 1-20 chars", roomNumber,
                v -> Validator.requireLength(v, "Room number", 1, 20));
    }
    private void f4() {
        capacity = InputHelper.askInt(4, "Capacity", "int | greater than 0", capacity, 1, 10000);
    }
    private void f5() {
        floorNumber = InputHelper.askInt(5, "Floor number", "int | 0 or greater", floorNumber, 0, 200);
    }
    private void f6() {
        String cur = available == null ? null : (available ? "YES" : "NO");
        available = InputHelper.askChoice(6, "Available", cur, "YES", "NO").equals("YES");
    }

    private void editField() {
        int n = InputHelper.askMenu("Field number to edit (1-6): ", 1, 6);
        switch (n) {
            case 1 -> f1(); case 2 -> f2(); case 3 -> f3();
            case 4 -> f4(); case 5 -> f5(); default -> f6();
        }
    }

    private void row(int no, String label, Object value) {
        System.out.printf("| %02d %-16s: %s%n", no, label, value == null ? "(not set)" : value);
    }

    private Classroom review() {
        System.out.println("\n+---------------- REVIEW CLASSROOM -------------------------+");
        row(1, "Classroom ID", classroomId);
        row(2, "Building name", buildingName);
        row(3, "Room number", roomNumber);
        row(4, "Capacity", capacity);
        row(5, "Floor number", floorNumber);
        row(6, "Available", available == null ? null : (available ? "YES" : "NO"));
        System.out.println("+------------------------------------------------------------+");
        System.out.println("| [1] Save classroom   [2] Edit field   [0] Cancel");
        int c = InputHelper.askMenu("Select option: ", 0, 2);
        if (c == 0) throw new FormNavigationException(true);
        if (c == 2) { editField(); return null; }

        Classroom room = new Classroom(classroomId, buildingName, roomNumber,
                capacity, floorNumber, available);
        try {
            repo.addClassroom(room);
            System.out.println("+ Classroom " + classroomId + " registered successfully.");
            return room;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Cannot save: " + e.getMessage());
            System.out.println("  Choose [2] Edit field to fix it.");
            return null;
        }
    }
}