package com.spas.crss.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mariadb://localhost:3306/spas_crss"
            );

    private static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");

    private static final String PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", "");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MariaDB JDBC driver is not available on the runtime classpath.",
                    e
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {

            System.out.println("=================================");
            System.out.println("SPAS CRSS DATABASE CONNECTION");
            System.out.println("=================================");
            System.out.println("Connection successful!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Database version: "
                    + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("=================================");

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }
}