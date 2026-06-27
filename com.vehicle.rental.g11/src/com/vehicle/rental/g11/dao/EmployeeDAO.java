package com.vehicle.rental.g11.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import com.vehicle.rental.g11.db.DatabaseConnection;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Employee;
import com.vehicle.rental.g11.service.PasswordUtil;

/**
 * Data Access Object for Employee authentication and management.
 * Handles insertion, lookup, update, search and deletion of employees.
 */
public class EmployeeDAO {
    private Connection getConn() throws RentalSystemException {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Adds a new employee (creates Person and Employee rows). */
    public boolean addEmployee(Employee employee, String plainPassword) throws RentalSystemException {
        String personSql = "INSERT INTO Person (personID, first_name, middle_initial, last_name, suffix, email) VALUES (?, ?, ?, ?, ?, ?)";
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

    /** Retrieves an employee (including password hash) by email. */
    public Employee getEmployeeByEmail(String email) throws RentalSystemException {
        String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, e.password " +
                     "FROM Person p JOIN Employee e ON p.personID = e.personID WHERE p.email = ?";
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

    /** Seeds the default admin account if it doesn't already exist. */
    public void seedDefaultAdmin() throws RentalSystemException {
        String adminEmail = "admin@carls.com";
        if (getEmployeeByEmail(adminEmail) == null) {
            Employee admin = new Employee(null, "Admin", null, "Admin", null, adminEmail, null);
            addEmployee(admin, "admin123");
        }
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

    /** Retrieves all employees (including password hash). */
    public List<Employee> getAllEmployees() throws RentalSystemException {
        String sql = "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, e.password " +
                     "FROM Person p JOIN Employee e ON p.personID = e.personID ORDER BY p.personID ASC";
        List<Employee> results = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Employee(
                        rs.getString("personID"),
                        rs.getString("first_name"),
                        rs.getString("middle_initial"),
                        rs.getString("last_name"),
                        rs.getString("suffix"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Failed to fetch all employees: " + e.getMessage(), e);
        }
        return results;
    }

    /** Searches employees by keywords in name or email. */
    public List<Employee> searchEmployees(String query) throws RentalSystemException {
        String[] keywords = query.trim().split("\\s+");
        StringBuilder sql = new StringBuilder(
                "SELECT p.personID, p.first_name, p.middle_initial, p.last_name, p.suffix, p.email, e.password " +
                "FROM Person p JOIN Employee e ON p.personID = e.personID WHERE 1=1");
        for (String keyword : keywords) {
            sql.append(" AND (first_name LIKE ? OR middle_initial LIKE ? OR last_name LIKE ? OR email LIKE ?)");
        }
        List<Employee> results = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            int idx = 1;
            for (String keyword : keywords) {
                String pattern = "%" + keyword + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Employee(
                            rs.getString("personID"),
                            rs.getString("first_name"),
                            rs.getString("middle_initial"),
                            rs.getString("last_name"),
                            rs.getString("suffix"),
                            rs.getString("email"),
                            rs.getString("password")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Error searching employees: " + e.getMessage(), e);
        }
        return results;
    }

    /** Updates an employee's personal details and optionally password. */
    public boolean updateEmployee(Employee employee, String newPlainPassword) throws RentalSystemException {
        String personSql = "UPDATE Person SET first_name = ?, middle_initial = ?, last_name = ?, suffix = ?, email = ? WHERE personID = ?";
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPerson = conn.prepareStatement(personSql)) {
                psPerson.setString(1, employee.getFirstName());
                psPerson.setString(2, employee.getMiddleName());
                psPerson.setString(3, employee.getLastName());
                psPerson.setString(4, employee.getSuffix());
                psPerson.setString(5, employee.getEmail());
                psPerson.setString(6, employee.getPersonID());
                psPerson.executeUpdate();

                if (newPlainPassword != null && !newPlainPassword.isEmpty()) {
                    String hash = PasswordUtil.hashPassword(newPlainPassword);
                    String passSql = "UPDATE Employee SET password = ? WHERE personID = ?";
                    try (PreparedStatement psPass = conn.prepareStatement(passSql)) {
                        psPass.setString(1, hash);
                        psPass.setString(2, employee.getPersonID());
                        psPass.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new RentalSystemException("Failed to update employee: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Database error while updating employee: " + e.getMessage(), e);
        }
    }

    /** Permanently deletes an employee (removes both Employee and Person rows). */
    public boolean deleteEmployee(String personID) throws RentalSystemException {
        String deleteEmpSql = "DELETE FROM Employee WHERE personID = ?";
        String deletePersonSql = "DELETE FROM Person WHERE personID = ?";
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEmp = conn.prepareStatement(deleteEmpSql);
                 PreparedStatement psPerson = conn.prepareStatement(deletePersonSql)) {
                psEmp.setString(1, personID);
                int empRows = psEmp.executeUpdate();
                psPerson.setString(1, personID);
                int personRows = psPerson.executeUpdate();
                conn.commit();
                return empRows > 0 && personRows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw new RentalSystemException("Failed to delete employee: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Database error while deleting employee: " + e.getMessage(), e);
        }
    }
}
