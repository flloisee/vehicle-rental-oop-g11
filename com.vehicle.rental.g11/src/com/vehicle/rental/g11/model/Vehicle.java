package com.vehicle.rental.g11.model;

public abstract class Vehicle {
    private int vehicleID;
    private String brand;
    private String model;
    private String plateNumber;
    private double dailyRate;
    private VehicleStatus status;

    public Vehicle(int vehicleID, String brand, String model, String plateNumber, double dailyRate, VehicleStatus status) {
        this.vehicleID = vehicleID;
        this.brand = brand;
        this.model = model;
        setPlateNumber(plateNumber);
        this.dailyRate = dailyRate;
        this.status = status;
    }

    public abstract double calculateRentalCost(int days);
    public abstract String getType();

    public int getVehicleID() { return vehicleID; }
    public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.length() > 7) {
            throw new IllegalArgumentException("Plate number must be 7 characters or fewer.");
        }
        this.plateNumber = plateNumber;
    }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
}