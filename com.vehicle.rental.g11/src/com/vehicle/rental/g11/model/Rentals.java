package com.vehicle.rental.g11.model;

import java.time.LocalDate;

public class Rentals {
    private int rentalID;
    private String customerID;
    private int vehicleID;
    private LocalDate rentalDate;
    private LocalDate plannedReturnDate;
    private LocalDate returnDate; // nullable - null means not yet returned
    private double totalCost;

    public Rentals(int rentalID, String customerID, int vehicleID, LocalDate rentalDate,
                   LocalDate plannedReturnDate, LocalDate returnDate, double totalCost) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.vehicleID = vehicleID;
        this.rentalDate = rentalDate;
        this.plannedReturnDate = plannedReturnDate;
        this.returnDate = returnDate;
        this.totalCost = totalCost;
    }

    public int getRentalID() { return rentalID; }
    public void setRentalID(int rentalID) { this.rentalID = rentalID; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public int getVehicleID() { return vehicleID; }
    public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }

    public LocalDate getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDate rentalDate) { this.rentalDate = rentalDate; }

    public LocalDate getPlannedReturnDate() { return plannedReturnDate; }
    public void setPlannedReturnDate(LocalDate plannedReturnDate) { this.plannedReturnDate = plannedReturnDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public boolean isReturned() {
        return returnDate != null;
    }
}