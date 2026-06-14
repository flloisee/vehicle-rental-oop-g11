package com.vehicle.rental.g11.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vehicle.rental.g11.db.DatabaseConnection;
import com.vehicle.rental.g11.model.Vehicle;
import com.vehicle.rental.g11.model.VehicleStatus;

public class VehicleDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------- ADD -----------
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicles (brand, model, type, plate_number, daily_rate, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, vehicle.getBrand());
            ps.setString(2, vehicle.getModel());
            ps.setString(3, vehicle.getType());
            ps.setString(4, vehicle.getPlateNumber());
            ps.setDouble(5, vehicle.getDailyRate());
            ps.setString(6, vehicle.getStatus().getDbValue());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------- UPDATE -----------
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE Vehicles SET brand = ?, model = ?, type = ?, plate_number = ?, "
                    + "daily_rate = ?, status = ? WHERE vehicleID = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, vehicle.getBrand());
            ps.setString(2, vehicle.getModel());
            ps.setString(3, vehicle.getType());
            ps.setString(4, vehicle.getPlateNumber());
            ps.setDouble(5, vehicle.getDailyRate());
            ps.setString(6, vehicle.getStatus().getDbValue());
            ps.setInt(7, vehicle.getVehicleID());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------- Check if plate number exists (validation helper) -----------
    public boolean plateExists(String plateNumber, int excludeVehicleID) {
        String sql = "SELECT vehicleID FROM Vehicles WHERE plate_number = ? AND vehicleID != ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            ps.setInt(2, excludeVehicleID);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------- Get single vehicle by ID (useful for loading into Update form) -----------
    public Vehicle getVehicleById(int vehicleID) {
        String sql = "SELECT * FROM Vehicles WHERE vehicleID = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, vehicleID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                return com.vehicle.rental.g11.model.VehicleFactory.createVehicle(
                        type,
                        rs.getInt("vehicleID"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("plate_number"),
                        rs.getDouble("daily_rate"),
                        VehicleStatus.fromDbValue(rs.getString("status"))
                );
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
