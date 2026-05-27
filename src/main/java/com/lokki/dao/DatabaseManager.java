package com.lokki.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String BASE_URL = "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lokki_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            ensureDatabaseExists();
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        return connection;
    }

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection tempConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = tempConn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS lokki_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("USE lokki_db");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS master_config ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "password_hash VARCHAR(512) NOT NULL, "
                    + "salt_master VARCHAR(128) NOT NULL, "
                    + "salt_recovery VARCHAR(128) NOT NULL, "
                    + "encrypted_vault_key_by_master TEXT NOT NULL, "
                    + "encrypted_vault_key_by_recovery TEXT NOT NULL, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS categories ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(100) NOT NULL UNIQUE, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            stmt.executeUpdate("INSERT IGNORE INTO categories (id, name) VALUES "
                    + "(1, 'General'), (2, 'Social'), (3, 'Work'), (4, 'Finance'), (5, 'Developer')");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS credentials ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "site_name VARCHAR(255) NOT NULL, "
                    + "site_url VARCHAR(500), "
                    + "username VARCHAR(255), "
                    + "encrypted_password TEXT NOT NULL, "
                    + "category_id INT DEFAULT 1, "
                    + "notes TEXT, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL"
                    + ")");

            try {
                stmt.executeUpdate("CREATE INDEX idx_credentials_site_name ON credentials(site_name)");
            } catch (SQLException e) {
                // index may already exist, ignore
            }
            try {
                stmt.executeUpdate("CREATE INDEX idx_credentials_category ON credentials(category_id)");
            } catch (SQLException e) {
                // index may already exist, ignore
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to initialize database schema", e);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
