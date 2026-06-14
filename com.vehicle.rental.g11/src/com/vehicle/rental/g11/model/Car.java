package com.vehicle.rental.g11.model;

public class Car extends Vehicle {
    public Car(int vehicleID, String brand, String model, String plateNumber, double dailyRate, VehicleStatus status) {
        super(vehicleID, brand, model, plateNumber, dailyRate, status);
    }

    @Override
    public double calculateRentalCost(int days) {
        return getDailyRate() * days;
    }

    @Override
    public String getType() {
        return "Car";
    }
}
