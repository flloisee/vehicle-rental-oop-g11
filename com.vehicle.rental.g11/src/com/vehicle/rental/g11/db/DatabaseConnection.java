package com.vehicle.rental.g11.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.vehicle.rental.g11.exception.RentalSystemException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")
            .ignoreIfMissing()
            .load();

    private static final String URL = dotenv.get("DB_URL",

            "jdbc:mysql://localhost:3306/vehicle_rental_g11_oop");

    private static final String USER = dotenv.get("DB_USER", "root");

    private static final String PASSWORD = dotenv.get("DB_PASSWORD", "your_db_password");
    private DatabaseConnection() throws RentalSystemException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RentalSystemException("Failed to establish database connection: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() throws RentalSystemException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws RentalSystemException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RentalSystemException("Failed to get database connection: " + e.getMessage(), e);
        }
        return connection;
    }
}
