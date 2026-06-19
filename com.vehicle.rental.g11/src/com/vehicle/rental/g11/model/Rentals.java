package com.vehicle.rental.g11.model;

import java.time.LocalDate;

public class Rentals {
    private int rentalID;
    private String customerID;
    private String customerName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private int vehicleID;
    private String vehicleBrand;
    private String vehicleModel;
    private LocalDate rentalDate;
    private LocalDate plannedReturnDate;
    private LocalDate returnDate;
    private double totalCost;

    public Rentals(int rentalID, String customerID, String customerName, int vehicleID, String vehicleBrand, String vehicleModel,
                   LocalDate rentalDate, LocalDate plannedReturnDate, LocalDate returnDate, double totalCost) {
        this(rentalID, customerID, customerName, null, null, null, null, vehicleID, vehicleBrand, vehicleModel,
             rentalDate, plannedReturnDate, returnDate, totalCost);
    }

    public Rentals(int rentalID, String customerID, String firstName, String middleName, String lastName, String suffix,
                   int vehicleID, String vehicleBrand, String vehicleModel, LocalDate rentalDate,
                   LocalDate plannedReturnDate, LocalDate returnDate, double totalCost) {
        this(rentalID, customerID, buildCustomerName(firstName, middleName, lastName, suffix), firstName, middleName, lastName, suffix,
             vehicleID, vehicleBrand, vehicleModel, rentalDate, plannedReturnDate, returnDate, totalCost);
    }

    private Rentals(int rentalID, String customerID, String customerName, String firstName, String middleName,
                    String lastName, String suffix, int vehicleID, String vehicleBrand, String vehicleModel,
                    LocalDate rentalDate, LocalDate plannedReturnDate, LocalDate returnDate, double totalCost) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.customerName = customerName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.vehicleID = vehicleID;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.rentalDate = rentalDate;
        this.plannedReturnDate = plannedReturnDate;
        this.returnDate = returnDate;
        this.totalCost = totalCost;
    }

    private static String buildCustomerName(String firstName, String middleName, String lastName, String suffix) {
        StringBuilder name = new StringBuilder();
        appendNamePart(name, firstName);
        appendNamePart(name, middleName);
        appendNamePart(name, lastName);
        appendNamePart(name, suffix);
        return name.length() == 0 ? "Unknown Customer" : name.toString().trim();
    }

    private static void appendNamePart(StringBuilder name, String value) {
        if (value != null && !value.trim().isEmpty()) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(value.trim());
        }
    }

    public int getRentalID() { return rentalID; }
    public void setRentalID(int rentalID) { this.rentalID = rentalID; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public int getVehicleID() { return vehicleID; }
    public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }

    public String getVehicleBrand() { return vehicleBrand; }
    public void setVehicleBrand(String vehicleBrand) { this.vehicleBrand = vehicleBrand; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

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
