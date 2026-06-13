package com.vehicle.rental.g11.model;

public class Truck extends Vehicle {
    private static final double SURCHARGE = 1.25;

    public Truck(int vehicleID, String brand, String model, String plateNumber, double dailyRate, VehicleStatus status) {
        super(vehicleID, brand, model, plateNumber, dailyRate, status);
    }

    @Override
    public double calculateRentalCost(int days) {
        return getDailyRate() * days * SURCHARGE;
    }

    @Override
    public String getType() {
        return "Truck";
    }
}