package com.vehicle.rental.g11.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import com.vehicle.rental.g11.db.DatabaseConnection;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Employee;
import com.vehicle.rental.g11.service.PasswordUtil;

/**
 * Data Access Object for Employee authentication.
 * Handles insertion into both Person and Employee tables and provides lookup utilities.
 */
public class EmployeeDAO {
    private Connection getConn() throws RentalSystemException {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Adds a new employee. Inserts a row into Person then into Employee (FK).
     * @param employee Employee object (personID may be null – generated if needed)
     * @param plainPassword raw password to be hashed
     * @return true if both inserts succeeded
     */
    public boolean addEmployee(Employee employee, String plainPassword) throws RentalSystemException {
        String personSql = "INSERT INTO Person (personID, first_name, middle_initial, last_name, suffix, email) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
        String employeeSql = "INSERT INTO Employee (personID, password) VALUES (?, ?)";

        if (employee.getPersonID() == null) {
            employee.setPersonID(UUID.randomUUID().toString());
        }
        String hashed = PasswordUtil.hashPassword(plainPassword);

        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPerson = conn.prepareStatement(personSql);
                 PreparedStatement psEmp = conn.prepareStatement(employeeSql)) {

                psPerson.setString(1, employee.getPersonID());
                psPerson.setString(2, employee.getFirstName());
                psPerson.setString(3, employee.getMiddleName());
                psPerson.setString(4, employee.getLastName());
                psPerson.setString(5, employee.getSuffix());
                psPerson.setString(6, employee.getEmail());
                psPerson.executeUpdate();

                psEmp.setString(1, employee.getPersonID());
                psEmp.setString(2, hashed);
                psEmp.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new RentalSystemException("Failed to add employee: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Database error while adding employee: " + e.getMessage(), e);
        }
    }

    /** Retrieves an employee (without password) by email. */
    public Employee getEmployeeByEmail(String email) throws RentalSystemException {
        String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, e.password "
                   + "FROM Person p JOIN Employee e ON p.personID = e.personID WHERE p.email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getString("personID"),
                        rs.getString("first_name"),
                        rs.getString("middle_initial"),
                        rs.getString("last_name"),
                        rs.getString("suffix"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error fetching employee by email: " + e.getMessage(), e);
        }
        return null;
    }

    /** Returns the stored password hash for a given email. */
    public String getPasswordHashByEmail(String email) throws RentalSystemException {
        Employee emp = getEmployeeByEmail(email);
        return emp != null ? emp.getPasswordHash() : null;
    }

    /** Updates an employee's password (admin recovery). */
    public boolean updatePassword(String personID, String newPlainPassword) throws RentalSystemException {
        String sql = "UPDATE Employee SET password = ? WHERE personID = ?";
        String hashed = PasswordUtil.hashPassword(newPlainPassword);
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setString(2, personID);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RentalSystemException("Failed to update employee password: " + e.getMessage(), e);
        }
    }
}
