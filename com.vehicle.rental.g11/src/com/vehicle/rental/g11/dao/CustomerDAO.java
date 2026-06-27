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
        String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, c.is_active "
                   + "FROM Person p JOIN Customer c ON p.personID = c.personID "
                   + "WHERE p.email = ? AND c.is_active = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error fetching customer by email: " + e.getMessage(), e);
        }
        return null;
    }

    public String getPasswordByEmail(String email) throws RentalSystemException {
        return null;
    }

    public List<Customer> getAllCustomers() throws RentalSystemException {
        String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, c.is_active "
                   + "FROM Person p JOIN Customer c ON p.personID = c.personID "
                   + "WHERE c.is_active = 1";
        return executeSelect(sql);
    }

    public List<Customer> searchCustomers(String query) throws RentalSystemException {
        String[] keywords = query.trim().split("\\s+");
        StringBuilder sql = new StringBuilder(
            "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, c.is_active "
          + "FROM Person p JOIN Customer c ON p.personID = c.personID "
          + "WHERE c.is_active = 1");

        for (String keyword : keywords) {
            sql.append(" AND (p.personID LIKE ? OR first_name LIKE ? OR middle_initial LIKE ? OR last_name LIKE ? OR email LIKE ?)");
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
                ps.setString(paramIndex++, pattern);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapCustomer(rs));
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
                customers.add(mapCustomer(rs));
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error fetching customers: " + e.getMessage(), e);
        }
        return customers;
    }

    private Customer mapCustomer(java.sql.ResultSet rs) throws SQLException {
        return new Customer(
            rs.getString("personID"),
            rs.getString("first_name"),
            rs.getString("middle_initial"),
            rs.getString("last_name"),
            rs.getString("suffix"),
            rs.getString("email"),
            rs.getBoolean("is_active")
        );
    }

    // ----------- UPDATE (no password change) -----------
    public boolean updateCustomer(Customer customer) throws RentalSystemException {
        String sql = "UPDATE Person SET first_name = ?, middle_initial = ?, last_name = ?, suffix = ?, email = ? "
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

    // ----------- SOFT DELETE -----------
    /**
     * Soft-deletes a customer by setting is_active = 0.
     * The Person and Customer rows remain in the database so that
     * historical rental and report records keep their FK reference intact.
     * Returns true if a row was updated, false if customer not found.
     */
    public boolean deleteCustomer(String customerID) throws RentalSystemException {
        String checkSql = "SELECT COUNT(*) FROM Rentals WHERE personID = ? AND return_date IS NULL";
        try (PreparedStatement ps = getConn().prepareStatement(checkSql)) {
            ps.setString(1, customerID);
            try (var rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RentalSystemException(
                        "Cannot delete customer: customer has active rentals. Return all vehicles first.");
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Failed to check customer rentals: " + e.getMessage(), e);
        }

        String sql = "UPDATE Customer SET is_active = 0 WHERE personID = ? AND is_active = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, customerID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RentalSystemException("Failed to soft-delete customer: " + e.getMessage(), e);
        }
    }
}