package com.lokki.service;

import com.lokki.dao.CategoryDAO;
import com.lokki.dao.CredentialDAO;
import com.lokki.model.Category;
import com.lokki.model.Credential;
import java.sql.SQLException;
import java.util.List;

public class VaultService {

    private final CredentialDAO credentialDAO;
    private final CategoryDAO categoryDAO;

    public VaultService() {
        this.credentialDAO = new CredentialDAO();
        this.categoryDAO = new CategoryDAO();
    }

    /**
     * Returns all credentials with decrypted passwords.
     */
    public List<Credential> getAllCredentials(byte[] vaultKey) {
        try {
            List<Credential> credentials = credentialDAO.findAll();
            decryptPasswords(vaultKey, credentials);
            return credentials;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load credentials", e);
        }
    }

    /**
     * Returns credentials filtered by category with decrypted passwords.
     */
    public List<Credential> getCredentialsByCategory(byte[] vaultKey, int categoryId) {
        try {
            List<Credential> credentials = credentialDAO.findByCategory(categoryId);
            decryptPasswords(vaultKey, credentials);
            return credentials;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load credentials by category", e);
        }
    }

    /**
     * Searches credentials by site name and returns them with decrypted passwords.
     */
    public List<Credential> searchCredentials(byte[] vaultKey, String searchTerm) {
        try {
            List<Credential> credentials = credentialDAO.search(searchTerm);
            decryptPasswords(vaultKey, credentials);
            return credentials;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search credentials", e);
        }
    }

    /**
     * Encrypts the plaintext password with the vault key and saves the credential.
     */
    public void addCredential(byte[] vaultKey, Credential credential, String plaintextPassword) {
        try {
            String encrypted = EncryptionService.encryptWithAES(vaultKey, plaintextPassword);
            credential.setEncryptedPassword(encrypted);
            credentialDAO.insert(credential);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add credential", e);
        }
    }

    /**
     * Updates an existing credential, re-encrypting the password if provided.
     */
    public void updateCredential(byte[] vaultKey, Credential credential, String plaintextPassword) {
        try {
            if (plaintextPassword != null && !plaintextPassword.isEmpty()) {
                String encrypted = EncryptionService.encryptWithAES(vaultKey, plaintextPassword);
                credential.setEncryptedPassword(encrypted);
            } else {
                Credential existing = credentialDAO.findById(credential.getId());
                if (existing != null) {
                    credential.setEncryptedPassword(existing.getEncryptedPassword());
                }
            }
            credentialDAO.update(credential);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update credential", e);
        }
    }

    /**
     * Deletes a credential by its id.
     */
    public void deleteCredential(int id) {
        try {
            credentialDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete credential", e);
        }
    }

    /**
     * Returns all categories from the database.
     */
    public List<Category> getAllCategories() {
        try {
            return categoryDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load categories", e);
        }
    }

    /**
     * Decrypts the password field of every credential in the list using the vault key.
     */
    private void decryptPasswords(byte[] vaultKey, List<Credential> credentials) {
        for (Credential credential : credentials) {
            try {
                String decrypted = EncryptionService.decryptWithAES(vaultKey, credential.getEncryptedPassword());
                credential.setEncryptedPassword(decrypted);
            } catch (Exception e) {
                credential.setEncryptedPassword("[decryption error]");
            }
        }
    }
}
