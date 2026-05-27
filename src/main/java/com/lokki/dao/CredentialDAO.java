package com.lokki.dao;

import com.lokki.model.Credential;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CredentialDAO {

    /**
     * Returns all credentials ordered by site_name.
     */
    public List<Credential> findAll() throws SQLException {
        List<Credential> credentials = new ArrayList<>();
        String sql = "SELECT * FROM credentials ORDER BY site_name";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                credentials.add(mapRow(rs));
            }
        }
        return credentials;
    }

    /**
     * Returns credentials filtered by category id.
     */
    public List<Credential> findByCategory(int categoryId) throws SQLException {
        List<Credential> credentials = new ArrayList<>();
        String sql = "SELECT * FROM credentials WHERE category_id = ? ORDER BY site_name";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    credentials.add(mapRow(rs));
                }
            }
        }
        return credentials;
    }

    /**
     * Returns credentials whose site_name contains the given search term.
     */
    public List<Credential> search(String searchTerm) throws SQLException {
        List<Credential> credentials = new ArrayList<>();
        String sql = "SELECT * FROM credentials WHERE site_name LIKE ? ORDER BY site_name";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    credentials.add(mapRow(rs));
                }
            }
        }
        return credentials;
    }

    /**
     * Inserts a new credential and sets its generated id.
     */
    public void insert(Credential credential) throws SQLException {
        String sql = "INSERT INTO credentials (site_name, site_url, username, encrypted_password, category_id, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, credential.getSiteName());
            stmt.setString(2, credential.getSiteUrl());
            stmt.setString(3, credential.getUsername());
            stmt.setString(4, credential.getEncryptedPassword());
            stmt.setInt(5, credential.getCategoryId());
            stmt.setString(6, credential.getNotes());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    credential.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Updates an existing credential identified by its id.
     */
    public void update(Credential credential) throws SQLException {
        String sql = "UPDATE credentials SET site_name = ?, site_url = ?, username = ?, "
                + "encrypted_password = ?, category_id = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, credential.getSiteName());
            stmt.setString(2, credential.getSiteUrl());
            stmt.setString(3, credential.getUsername());
            stmt.setString(4, credential.getEncryptedPassword());
            stmt.setInt(5, credential.getCategoryId());
            stmt.setString(6, credential.getNotes());
            stmt.setInt(7, credential.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a credential by its id.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM credentials WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Credential mapRow(ResultSet rs) throws SQLException {
        Credential credential = new Credential();
        credential.setId(rs.getInt("id"));
        credential.setSiteName(rs.getString("site_name"));
        credential.setSiteUrl(rs.getString("site_url"));
        credential.setUsername(rs.getString("username"));
        credential.setEncryptedPassword(rs.getString("encrypted_password"));
        credential.setCategoryId(rs.getInt("category_id"));
        credential.setNotes(rs.getString("notes"));
        credential.setCreatedAt(rs.getString("created_at"));
        credential.setUpdatedAt(rs.getString("updated_at"));
        return credential;
    }
}
