package com.vehicle.rental.g11.model;

public class Customer {
    private String customerID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String email;

    public Customer(String customerID, String firstName, String middleName, String lastName, String suffix, String email) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.email = email;
    }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
