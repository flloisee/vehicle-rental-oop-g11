package com.vehicle.rental.g11.dao;

import com.vehicle.rental.g11.db.DatabaseConnection;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Customer;
import com.vehicle.rental.g11.service.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class CustomerDAO {

    private Connection getConn() throws RentalSystemException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------- ADD -----------
    public boolean addCustomer(Customer customer, String plainPassword) throws RentalSystemException {
        String sql = "INSERT INTO Customers (customerID, first_name, middle_name, last_name, suffix, email, password) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Generate UUID if not already set
        if (customer.getCustomerID() == null) {
            customer.setCustomerID(UUID.randomUUID().toString());
        }

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerID());
            ps.setString(2, customer.getFirstName());
            ps.setString(3, customer.getMiddleName());
            ps.setString(4, customer.getLastName());
            ps.setString(5, customer.getSuffix());
            ps.setString(6, customer.getEmail());
	    ps.setString(7, hashedPassword);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to add customer: " + e.getMessage(), e);
        }
    }

    // ----------- UPDATE (no password change) -----------
    public boolean updateCustomer(Customer customer) throws RentalSystemException {
        String sql = "UPDATE Customers SET first_name = ?, middle_name = ?, last_name = ?, suffix = ?, email = ?  "
                    + "WHERE customerID = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getMiddleName());
            ps.setString(3, customer.getLastName());
            ps.setString(4, customer.getSuffix());
            ps.setString(5, customer.getEmail());
	    ps.setString(6, customer.getCustomerID());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to update customer: " + e.getMessage(), e);
        }
    }

    // ----------- UPDATE PASSWORD (Admin Recovery) -----------
    public boolean updatePassword(String customerID, String newPlainPassword) throws RentalSystemException {
        String sql = "UPDATE Customers SET password = ? WHERE customerID = ?";
        String hashedPassword = PasswordUtil.hashPassword(newPlainPassword);

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, customerID);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to update customer password: " + e.getMessage(), e);
        }
    }
}