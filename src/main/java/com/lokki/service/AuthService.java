package com.lokki.service;

import com.lokki.dao.MasterConfigDAO;
import com.lokki.model.MasterConfig;
import com.lokki.util.RecoveryKeyFormatter;
import com.lokki.util.SecureMemoryUtil;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AuthService {

    private static final int VAULT_KEY_LENGTH = 32;
    private final MasterConfigDAO masterConfigDAO;
    private final SecureRandom secureRandom;
    private byte[] activeVaultKey;

    public AuthService() {
        this.masterConfigDAO = new MasterConfigDAO();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Checks whether the vault has been set up by querying the master_config table.
     */
    public boolean isFirstRun() {
        try {
            return masterConfigDAO.count() == 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Creates the initial vault: generates vault key, derives keys, encrypts vault key twice,
     * and persists everything via MasterConfigDAO. The recovery key is provided externally
     * so the caller can display it to the user before it is cleared.
     */
    public byte[] setupVault(char[] masterPassword, char[] recoveryKey) {
        byte[] vaultKey = new byte[VAULT_KEY_LENGTH];
        secureRandom.nextBytes(vaultKey);

        byte[] saltMaster = KeyDerivationService.generateSalt();
        byte[] saltRecovery = KeyDerivationService.generateSalt();

        byte[] masterDerivedKey = KeyDerivationService.deriveKey(masterPassword, saltMaster);

        byte[] recoveryDerivedKey = KeyDerivationService.deriveKey(recoveryKey, saltRecovery);

        String encVaultKeyByMaster = EncryptionService.encryptWithAES(masterDerivedKey, Base64.getEncoder().encodeToString(vaultKey));
        String encVaultKeyByRecovery = EncryptionService.encryptWithAES(recoveryDerivedKey, Base64.getEncoder().encodeToString(vaultKey));

        String passwordHash = KeyDerivationService.hashPassword(masterPassword, saltMaster);

        MasterConfig config = new MasterConfig(
                passwordHash,
                Base64.getEncoder().encodeToString(saltMaster),
                Base64.getEncoder().encodeToString(saltRecovery),
                encVaultKeyByMaster,
                encVaultKeyByRecovery
        );

        try {
            masterConfigDAO.insert(config);
        } catch (Exception e) {
            SecureMemoryUtil.clearByteArray(vaultKey);
            e.printStackTrace();
            throw new RuntimeException("Failed to save master configuration: " + e.getMessage(), e);
        } finally {
            SecureMemoryUtil.clearByteArray(masterDerivedKey);
            SecureMemoryUtil.clearByteArray(recoveryDerivedKey);
            SecureMemoryUtil.clearCharArray(masterPassword);
            SecureMemoryUtil.clearCharArray(recoveryKey);
        }

        this.activeVaultKey = vaultKey;
        return vaultKey;
    }

    /**
     * Authenticates the user by deriving a key from the master password,
     * verifying the password hash, and decrypting the vault key.
     */
    public byte[] login(char[] masterPassword) {
        byte[] masterDerivedKey = null;
        try {
            MasterConfig config = masterConfigDAO.get();
            if (config == null) {
                throw new RuntimeException("Vault not initialized");
            }

            byte[] saltMaster = Base64.getDecoder().decode(config.getSaltMaster());
            masterDerivedKey = KeyDerivationService.deriveKey(masterPassword, saltMaster);

            String expectedHash = KeyDerivationService.hashPassword(masterPassword, saltMaster);
            if (!expectedHash.equals(config.getPasswordHash())) {
                throw new RuntimeException("Invalid master password");
            }

            String decryptedVaultKeyBase64 = EncryptionService.decryptWithAES(masterDerivedKey, config.getEncryptedVaultKeyByMaster());
            byte[] vaultKey = Base64.getDecoder().decode(decryptedVaultKeyBase64);

            this.activeVaultKey = vaultKey;
            return vaultKey;
        } catch (Exception e) {
            throw new RuntimeException("Login failed", e);
        } finally {
            if (masterDerivedKey != null) {
                SecureMemoryUtil.clearByteArray(masterDerivedKey);
            }
            SecureMemoryUtil.clearCharArray(masterPassword);
        }
    }

    /**
     * Recovers the vault using the recovery key, then re-encrypts the vault key
     * with a new master password.
     */
    public byte[] recoverWithKey(char[] recoveryKey, char[] newMasterPassword) {
        try {
            MasterConfig config = masterConfigDAO.get();
            if (config == null) {
                throw new RuntimeException("Vault not initialized");
            }

            byte[] saltRecovery = Base64.getDecoder().decode(config.getSaltRecovery());
            byte[] recoveryDerivedKey = KeyDerivationService.deriveKey(recoveryKey, saltRecovery);

            String decryptedVaultKeyBase64 = EncryptionService.decryptWithAES(recoveryDerivedKey, config.getEncryptedVaultKeyByRecovery());
            byte[] vaultKey = Base64.getDecoder().decode(decryptedVaultKeyBase64);

            byte[] newSaltMaster = KeyDerivationService.generateSalt();
            byte[] newMasterDerivedKey = KeyDerivationService.deriveKey(newMasterPassword, newSaltMaster);

            String newEncVaultKeyByMaster = EncryptionService.encryptWithAES(newMasterDerivedKey, Base64.getEncoder().encodeToString(vaultKey));
            String newPasswordHash = KeyDerivationService.hashPassword(newMasterPassword, newSaltMaster);

            masterConfigDAO.updateMasterFields(
                    newPasswordHash,
                    Base64.getEncoder().encodeToString(newSaltMaster),
                    newEncVaultKeyByMaster
            );

            SecureMemoryUtil.clearByteArray(recoveryDerivedKey);
            SecureMemoryUtil.clearByteArray(newMasterDerivedKey);
            SecureMemoryUtil.clearCharArray(recoveryKey);
            SecureMemoryUtil.clearCharArray(newMasterPassword);

            this.activeVaultKey = vaultKey;
            return vaultKey;
        } catch (Exception e) {
            SecureMemoryUtil.clearCharArray(recoveryKey);
            SecureMemoryUtil.clearCharArray(newMasterPassword);
            throw new RuntimeException("Recovery failed - invalid recovery key", e);
        }
    }

    /**
     * Returns the recovery key formatted for display.
     */
    public String getFormattedRecoveryKey(char[] recoveryKey) {
        return RecoveryKeyService.formatForDisplay(recoveryKey);
    }

    /**
     * Returns the currently active vault key.
     */
    public byte[] getActiveVaultKey() {
        return activeVaultKey;
    }

    /**
     * Zeros the active vault key in memory.
     */
    public void clearSession() {
        if (activeVaultKey != null) {
            Arrays.fill(activeVaultKey, (byte) 0);
            activeVaultKey = null;
        }
    }
}
