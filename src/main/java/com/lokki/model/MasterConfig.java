package com.lokki.model;

public class MasterConfig {

    private int id;
    private String passwordHash;
    private String saltMaster;
    private String saltRecovery;
    private String encryptedVaultKeyByMaster;
    private String encryptedVaultKeyByRecovery;
    private String createdAt;

    public MasterConfig() {}

    public MasterConfig(String passwordHash, String saltMaster, String saltRecovery,
                        String encryptedVaultKeyByMaster, String encryptedVaultKeyByRecovery) {
        this.passwordHash = passwordHash;
        this.saltMaster = saltMaster;
        this.saltRecovery = saltRecovery;
        this.encryptedVaultKeyByMaster = encryptedVaultKeyByMaster;
        this.encryptedVaultKeyByRecovery = encryptedVaultKeyByRecovery;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSaltMaster() {
        return saltMaster;
    }

    public void setSaltMaster(String saltMaster) {
        this.saltMaster = saltMaster;
    }

    public String getSaltRecovery() {
        return saltRecovery;
    }

    public void setSaltRecovery(String saltRecovery) {
        this.saltRecovery = saltRecovery;
    }

    public String getEncryptedVaultKeyByMaster() {
        return encryptedVaultKeyByMaster;
    }

    public void setEncryptedVaultKeyByMaster(String encryptedVaultKeyByMaster) {
        this.encryptedVaultKeyByMaster = encryptedVaultKeyByMaster;
    }

    public String getEncryptedVaultKeyByRecovery() {
        return encryptedVaultKeyByRecovery;
    }

    public void setEncryptedVaultKeyByRecovery(String encryptedVaultKeyByRecovery) {
        this.encryptedVaultKeyByRecovery = encryptedVaultKeyByRecovery;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
