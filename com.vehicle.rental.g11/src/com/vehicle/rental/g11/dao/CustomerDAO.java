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


public class CustomerDAO {

    private Connection getConn() throws RentalSystemException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------- ADD -----------
    public boolean addCustomer(Customer customer, String plainPassword) throws RentalSystemException {
        // Insert into Person table (holds personal data)
        String personSql = "INSERT INTO Person (personID, first_name, middle_initial, last_name, suffix, email) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
        // Insert into Customer table (FK to Person)
        String customerSql = "INSERT INTO Customer (personID) VALUES (?)";

        // Generate UUID if not already set
        if (customer.getCustomerID() == null) {
            customer.setCustomerID(UUID.randomUUID().toString());
        }

        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPerson = conn.prepareStatement(personSql);
                 PreparedStatement psCustomer = conn.prepareStatement(customerSql)) {

                // Person fields
                psPerson.setString(1, customer.getCustomerID());
                psPerson.setString(2, customer.getFirstName());
                psPerson.setString(3, customer.getMiddleName());
                psPerson.setString(4, customer.getLastName());
                psPerson.setString(5, customer.getSuffix());
                psPerson.setString(6, customer.getEmail());
                psPerson.executeUpdate();

                // Customer FK only
                psCustomer.setString(1, customer.getCustomerID());
                psCustomer.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new RentalSystemException("Failed to add customer: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Database error while adding customer: " + e.getMessage(), e);
        }
    }

    public Customer getCustomerByEmail(String email) throws RentalSystemException {
String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email "
                 + "FROM Person p JOIN Customer c ON p.personID = c.personID WHERE p.email = ?";
         try (PreparedStatement ps = getConn().prepareStatement(sql)) {
             ps.setString(1, email);
             try (var rs = ps.executeQuery()) {
                 if (rs.next()) {
                     return new Customer(
                        rs.getString("personID"),

                         rs.getString("first_name"),
                        rs.getString("middle_initial"),

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
        // Passwords are no longer stored for customers in the new schema.
        // This method is retained for compatibility but always returns null.
        return null;
    }

    public List<Customer> getAllCustomers() throws RentalSystemException {
        String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email "
                + "FROM Person p JOIN Customer c ON p.personID = c.personID";
        return executeSelect(sql);
    }

    public List<Customer> searchCustomers(String query) throws RentalSystemException {
        String[] keywords = query.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email FROM Person p JOIN Customer c ON p.personID = c.personID WHERE 1=1");
        
        for (String keyword : keywords) {
            sql.append(" AND (first_name LIKE ? OR middle_initial LIKE ? OR last_name LIKE ? OR email LIKE ?)");
        }

        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {

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
                        rs.getString("personID"),
                        rs.getString("first_name"),
                        rs.getString("middle_initial"),
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
                    rs.getString("personID"),
                    rs.getString("first_name"),
                    rs.getString("middle_initial"),
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
String sql = "UPDATE Person SET first_name = ?, middle_initial = ?, last_name = ?, suffix = ?, email = ?  "
                     + "WHERE personID = ?";

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
        // Passwords are not stored for customers in the new schema.
        // This operation is unsupported; returning false.
        return false;
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
        /**
     * Permanently delete a customer from the database.
     * Removes the customer record from the Customer table and also deletes the
     * associated Person record (the super‑type). Both deletions are performed
     * within a single transaction so that the database remains consistent.
     * Returns true only if both rows were deleted.
     */
    public boolean deleteCustomer(String customerID) throws RentalSystemException {
        String deleteCustomerSql = "DELETE FROM Customer WHERE personID = ?";
        String deletePersonSql   = "DELETE FROM Person   WHERE personID = ?";

        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psCust   = conn.prepareStatement(deleteCustomerSql);
                 PreparedStatement psPerson = conn.prepareStatement(deletePersonSql)) {

                psCust.setString(1, customerID);
                int custRows = psCust.executeUpdate();

                psPerson.setString(1, customerID);
                int personRows = psPerson.executeUpdate();

                conn.commit();
                return custRows > 0 && personRows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw new RentalSystemException("Failed to delete customer: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Database error while deleting customer: " + e.getMessage(), e);
        }
    }
}