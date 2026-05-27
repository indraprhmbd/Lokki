package com.lokki.dao;

import com.lokki.model.MasterConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MasterConfigDAO {

    /**
     * Inserts the initial master configuration row into master_config table.
     */
    public void insert(MasterConfig config) throws SQLException {
        String sql = "INSERT INTO master_config (password_hash, salt_master, salt_recovery, "
                + "encrypted_vault_key_by_master, encrypted_vault_key_by_recovery) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, config.getPasswordHash());
            stmt.setString(2, config.getSaltMaster());
            stmt.setString(3, config.getSaltRecovery());
            stmt.setString(4, config.getEncryptedVaultKeyByMaster());
            stmt.setString(5, config.getEncryptedVaultKeyByRecovery());
            stmt.executeUpdate();
        }
    }

    /**
     * Returns the first (and only) row from master_config, or null if table is empty.
     */
    public MasterConfig get() throws SQLException {
        String sql = "SELECT * FROM master_config LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                MasterConfig config = new MasterConfig();
                config.setId(rs.getInt("id"));
                config.setPasswordHash(rs.getString("password_hash"));
                config.setSaltMaster(rs.getString("salt_master"));
                config.setSaltRecovery(rs.getString("salt_recovery"));
                config.setEncryptedVaultKeyByMaster(rs.getString("encrypted_vault_key_by_master"));
                config.setEncryptedVaultKeyByRecovery(rs.getString("encrypted_vault_key_by_recovery"));
                config.setCreatedAt(rs.getString("created_at"));
                return config;
            }
            return null;
        }
    }

    /**
     * Returns the number of rows in master_config. Used to detect first run.
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM master_config";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * Updates the master-related fields during password change or recovery.
     */
    public void updateMasterFields(String passwordHash, String saltMaster, String encryptedVaultKeyByMaster) throws SQLException {
        String sql = "UPDATE master_config SET password_hash = ?, salt_master = ?, encrypted_vault_key_by_master = ? WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setString(2, saltMaster);
            stmt.setString(3, encryptedVaultKeyByMaster);
            stmt.executeUpdate();
        }
    }
}
