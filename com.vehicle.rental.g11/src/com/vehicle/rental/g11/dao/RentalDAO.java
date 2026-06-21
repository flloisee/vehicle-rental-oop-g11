package com.vehicle.rental.g11.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vehicle.rental.g11.db.DatabaseConnection;
import com.vehicle.rental.g11.exception.RentalSystemException;
import com.vehicle.rental.g11.model.Rentals;

public class RentalDAO {

    private Connection getConn() throws RentalSystemException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------- ADD -----------
    // Used when a customer starts a new rental
    public boolean addRental(Rentals rental) throws RentalSystemException {
        String sql = "INSERT INTO Rentals (customerID, vehicleID, rental_date, planned_return_date, return_date, total_cost) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, rental.getCustomerID());
            ps.setInt(2, rental.getVehicleID());
            ps.setDate(3, Date.valueOf(rental.getRentalDate()));
            ps.setDate(4, Date.valueOf(rental.getPlannedReturnDate()));

            // return_date is nullable - null means vehicle not yet returned
            if (rental.getReturnDate() != null) {
                ps.setDate(5, Date.valueOf(rental.getReturnDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setDouble(6, rental.getTotalCost());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to add rental: " + e.getMessage(), e);
        }
    }

    // ----------- UPDATE -----------
    // Used when updating rental details or recording an actual return
    public boolean updateRental(Rentals rental) throws RentalSystemException {
        String sql = "UPDATE Rentals SET customerID = ?, vehicleID = ?, rental_date = ?, "
                   + "planned_return_date = ?, return_date = ?, total_cost = ? "
                   + "WHERE rentalID = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, rental.getCustomerID());
            ps.setInt(2, rental.getVehicleID());
            ps.setDate(3, Date.valueOf(rental.getRentalDate()));
            ps.setDate(4, Date.valueOf(rental.getPlannedReturnDate()));

            if (rental.getReturnDate() != null) {
                ps.setDate(5, Date.valueOf(rental.getReturnDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setDouble(6, rental.getTotalCost());
            ps.setInt(7, rental.getRentalID());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to update rental: " + e.getMessage(), e);
        }
    }

    // ----------- GET BY ID -----------
    // Useful for loading a rental into the Update form
    public Rentals getRentalById(int rentalID) throws RentalSystemException {
        String sql = "SELECT r.*, c.first_name, c.middle_name, c.last_name, c.suffix, CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name) as customerName, v.brand, v.model " +
                       "FROM Rentals r " +
                                                 "LEFT JOIN Customers c ON r.customerID = c.customerID " +
                        "LEFT JOIN Vehicles v ON r.vehicleID = v.vehicleID " +
                       "WHERE r.rentalID = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, rentalID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to get rental by ID: " + e.getMessage(), e);
        }
    }

    // ----------- GET ALL -----------
    // Useful for displaying all rentals in a JTable on the dashboard
    public List<Rentals> getAllRentals() throws RentalSystemException {
        String sql = "SELECT r.*, c.first_name, c.middle_name, c.last_name, c.suffix, CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name) as customerName, v.brand, v.model " +
                       "FROM Rentals r " +
                                                 "LEFT JOIN Customers c ON r.customerID = c.customerID " +
                        "LEFT JOIN Vehicles v ON r.vehicleID = v.vehicleID " +
                       "ORDER BY r.rental_date DESC";
        List<Rentals> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to get all rentals: " + e.getMessage(), e);
        }

        return list;
    }

    public List<Rentals> searchRentals(String query) throws RentalSystemException {
        List<Rentals> results = new ArrayList<>();
        String sql = "SELECT r.*, c.first_name, c.middle_name, c.last_name, c.suffix, CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name) as customerName, v.brand, v.model " +
                       "FROM Rentals r " +
                                                 "LEFT JOIN Customers c ON r.customerID = c.customerID " +
                        "LEFT JOIN Vehicles v ON r.vehicleID = v.vehicleID " +
                       "WHERE r.customerID LIKE ? OR CAST(r.vehicleID AS CHAR) LIKE ? OR CAST(r.rentalID AS CHAR) LIKE ?";
        String wildCardQuery = "%" + query + "%";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, wildCardQuery);
            ps.setString(2, wildCardQuery);
            ps.setString(3, wildCardQuery);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Search failed: " + e.getMessage(), e);
        }
        return results;
    }

    // ----------- GET BY CUSTOMER -----------
    // Useful for showing a specific customer's rental history
    public List<Rentals> getRentalsByCustomer(String customerID) throws RentalSystemException {
        String sql = "SELECT r.*, c.first_name, c.middle_name, c.last_name, c.suffix, CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name) as customerName, v.brand, v.model " +
                       "FROM Rentals r " +
                                                 "LEFT JOIN Customers c ON r.customerID = c.customerID " +
                        "LEFT JOIN Vehicles v ON r.vehicleID = v.vehicleID " +
                       "WHERE r.customerID = ? ORDER BY r.rental_date DESC";
        List<Rentals> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, customerID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to get rentals by customer: " + e.getMessage(), e);
        }

        return list;
    }

    // ----------- GET ACTIVE RENTALS -----------
    // Active = return_date is NULL (vehicle not yet returned)
    public List<Rentals> getActiveRentals() throws RentalSystemException {
        String sql = "SELECT r.*, c.first_name, c.middle_name, c.last_name, c.suffix, CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name) as customerName, v.brand, v.model " +
                       "FROM Rentals r " +
                                                 "LEFT JOIN Customers c ON r.customerID = c.customerID " +
                        "LEFT JOIN Vehicles v ON r.vehicleID = v.vehicleID " +
                       "WHERE r.return_date IS NULL ORDER BY r.planned_return_date ASC";
        List<Rentals> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to get active rentals: " + e.getMessage(), e);
        }

        return list;
    }

    // ----------- MARK AS RETURNED -----------
    // Shortcut used by the return vehicle flow specifically
    public boolean markAsReturned(int rentalID, LocalDate returnDate) throws RentalSystemException {
        String sql = "UPDATE Rentals SET return_date = ? WHERE rentalID = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, rentalID);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RentalSystemException("Failed to mark rental as returned: " + e.getMessage(), e);
        }
    }

    // ----------- HELPER: map ResultSet row to Rentals object -----------
    private Rentals mapRow(ResultSet rs) throws SQLException {
        Date returnDateRaw = rs.getDate("return_date");
        LocalDate returnDate = (returnDateRaw != null) ? returnDateRaw.toLocalDate() : null;


        String firstName = rs.getString("first_name");
        String middleName = rs.getString("middle_name");
        String lastName = rs.getString("last_name");
        String suffix = rs.getString("suffix");
        String vehicleBrand = rs.getString("brand");
        String vehicleModel = rs.getString("model");

        return new Rentals(
            rs.getInt("rentalID"),
            rs.getString("customerID"),
            firstName,
            middleName,
            lastName,
            suffix,
            rs.getInt("vehicleID"),
            vehicleBrand != null ? vehicleBrand : "Unknown Brand",
            vehicleModel != null ? vehicleModel : "Unknown Model",
            rs.getDate("rental_date").toLocalDate(),
            rs.getDate("planned_return_date").toLocalDate(),
            returnDate,
            rs.getDouble("total_cost")
        );
    }
}