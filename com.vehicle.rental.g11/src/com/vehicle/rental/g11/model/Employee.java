package com.vehicle.rental.g11.model;

/**
 * Represents an employee (staff user) of the vehicle rental system.
 * The data is stored across the {@code Person} and {@code Employee} tables.
 */
public class Employee {
    private String personID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String email;
    private String passwordHash; // stored hashed password

    public Employee() {}

    public Employee(String personID, String firstName, String middleName, String lastName,
                    String suffix, String email, String passwordHash) {
        this.personID = personID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and setters
    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }

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

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
