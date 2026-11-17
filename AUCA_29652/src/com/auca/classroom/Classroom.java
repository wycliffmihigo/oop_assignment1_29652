package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Validator;

public class Classroom implements Identifiable, Registrable {
    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber,
                     int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    @Override public String getId() { return classroomId; }
    @Override public String getDisplayName() { return buildingName + " " + roomNumber; }
    @Override public boolean isActive() { return available; }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId(classroomId, "Classroom ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Validator.requireLength(buildingName, "Building name", 2, 100);
        Validator.requireLength(roomNumber, "Room number", 1, 20);
        if (capacity <= 0)
            throw new ValidationException("Capacity must be greater than zero.");
        if (floorNumber < 0)
            throw new ValidationException("Floor number must be zero or greater.");
    }

    @Override public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "[" + classroomId + "] " + getDisplayName() + " (capacity " + capacity + ")";
    }

    public String getClassroomId() { return classroomId; }
    public void setClassroomId(String v) { this.classroomId = v; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String v) { this.buildingName = v; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String v) { this.roomNumber = v; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int v) { this.capacity = v; }
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int v) { this.floorNumber = v; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean v) { this.available = v; }

    @Override
    public String toString() {
        return "Classroom{id=" + classroomId + ", building=" + buildingName
             + ", room=" + roomNumber + ", capacity=" + capacity
             + ", floor=" + floorNumber + ", available=" + available + "}";
    }
}