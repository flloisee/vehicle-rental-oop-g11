package com.vehicle.rental.g11.model;

public class VehicleFactory {
    public static Vehicle createVehicle(String type, int vehicleID, String brand, String model,
                                          String plateNumber, double dailyRate, VehicleStatus status) {
        switch (type.toLowerCase()) {
            case "car":
                return new Car(vehicleID, brand, model, plateNumber, dailyRate, status);
            case "truck":
                return new Truck(vehicleID, brand, model, plateNumber, dailyRate, status);
            case "motorcycle":
                return new Motorcycle(vehicleID, brand, model, plateNumber, dailyRate, status);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}