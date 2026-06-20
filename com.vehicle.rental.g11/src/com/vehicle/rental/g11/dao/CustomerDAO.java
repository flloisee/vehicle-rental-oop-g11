package com.vehicle.rental.g11.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vehicle.rental.g11.db.DatabaseConnection;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Customer;
import com.vehicle.rental.g11.service.PasswordUtil;

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

    public Customer getCustomerByEmail(String email) throws RentalSystemException {
        String sql = "SELECT * FROM Customers WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getString("customerID"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("suffix"),
                        rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error fetching customer by email: " + e.getMessage(), e);
        }
        return null;
    }

    public String getPasswordByEmail(String email) throws RentalSystemException {
        String sql = "SELECT password FROM Customers WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error fetching password by email: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws RentalSystemException {
        String sql = "SELECT * FROM Customers";
        return executeSelect(sql);
    }

    public List<Customer> searchCustomers(String query) throws RentalSystemException {
        String[] keywords = query.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("SELECT * FROM Customers WHERE 1=1");
        
        for (String keyword : keywords) {
            sql.append(" AND (first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ? OR email LIKE ?)");
        }

        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            String searchPattern = "%" + query + "%"; // This was old, I need to use keyword
            int paramIndex = 1;
            for (String keyword : keywords) {
                String pattern = "%" + keyword + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(new Customer(
                        rs.getString("customerID"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("suffix"),
                        rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error searching customers: " + e.getMessage(), e);
        }
        return customers;
    }

    private List<Customer> executeSelect(String sql) throws RentalSystemException {
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("customerID"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("suffix"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error fetching customers: " + e.getMessage(), e);
        }
        return customers;
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

    /**
     * Soft-delete (archive) a customer by setting `is_active` = 0.
     * Validation: checks that the customer exists and is currently active.
     * Returns true if the update affected at least one row, false if customer
     * doesn't exist or was already inactive.
     * Uses PreparedStatement to avoid SQL injection and proper exception handling.
     *
     * Sample console flow:
     * - Success: "Customer 123 archived successfully."
     * - Not found: "Customer 123 not found; nothing to archive."
     * - Already inactive: "Customer 123 is already archived."
     *
     * Why soft delete: preserves historical data and avoids breaking
     * foreign-key references; allows easy restore and auditability.
     */
    public boolean archiveCustomer(int customerId) throws RentalSystemException {
        String checkSql = "SELECT is_active FROM customers WHERE customer_id = ?";
        String updateSql = "UPDATE customers SET is_active = 0 WHERE customer_id = ?";

        try (Connection conn = getConn();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            // Validation: ensure customer exists
            checkPs.setInt(1, customerId);
            try (var rs = checkPs.executeQuery()) {
                if (!rs.next()) {
                    // Customer does not exist
                    return false;
                }
                boolean isActive = rs.getBoolean("is_active");
                if (!isActive) {
                    // Already archived/inactive
                    return false;
                }
            }

            // Perform soft delete
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setInt(1, customerId);
                int rows = updatePs.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to archive customer: " + e.getMessage(), e);
        }
    }

    /**
     * Permanently delete a customer from the database.
     * Removes the customer record completely from the Customers table.
     * Returns true if deletion was successful, false if customer not found.
     * Uses PreparedStatement to prevent SQL injection.
     *
     * Sample console flow:
     * - Success: "Customer abc-123 permanently deleted."
     * - Not found: "Customer abc-123 not found."
     */
    public boolean deleteCustomer(String customerID) throws RentalSystemException {
        String deleteSql = "DELETE FROM Customers WHERE customerID = ?";

        try (Connection conn = getConn();
             PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {

            deletePs.setString(1, customerID);
            int rows = deletePs.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to delete customer: " + e.getMessage(), e);
        }
    }
}