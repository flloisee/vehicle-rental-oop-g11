package com.vehicle.rental.g11.model;

public enum VehicleStatus {
    Available,
    Maintenance,
    Cleaning,
    Out_of_Service("Out of Service"),
    Rented,
    Reserved;

    private final String dbValue;

    VehicleStatus() {
        this.dbValue = this.name();
    }

    VehicleStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static VehicleStatus fromDbValue(String dbValue) {
        for (VehicleStatus s : values()) {
            if (s.getDbValue().equalsIgnoreCase(dbValue)) return s;
        }
        throw new IllegalArgumentException("Unknown status: " + dbValue);
    }
}