package com.vehicle.rental.g11.model;

public class Motorcycle extends Vehicle {
    private static final double DISCOUNT = 0.85;

    public Motorcycle(int vehicleID, String brand, String model, String plateNumber, double dailyRate, VehicleStatus status) {
        super(vehicleID, brand, model, plateNumber, dailyRate, status);
    }

    @Override
    public double calculateRentalCost(int days) {
        return getDailyRate() * days * DISCOUNT;
    }

    @Override
    public String getType() {
        return "Motorcycle";
    }
}
