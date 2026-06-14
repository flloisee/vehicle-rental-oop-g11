package com.vehicle.rental.g11.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    private static final String PASSWORD = dotenv.get("DB_PASSWORD", "");
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}