package com.lokki.controller;

import com.lokki.model.Category;
import com.lokki.model.Credential;
import com.lokki.service.VaultService;
import com.lokki.view.AddEditCredentialDialog;
import com.lokki.view.MainFrame;
import com.lokki.view.PasswordGeneratorDialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

public class VaultController {

    private final VaultService vaultService;
    private byte[] vaultKey;
    private MainFrame mainFrame;
    private AuthController authController;

    public VaultController(byte[] vaultKey) {
        this.vaultService = new VaultService();
        this.vaultKey = vaultKey;
    }

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    /**
     * Opens the main vault window and loads all credentials.
     */
    public void openMainFrame() {
        mainFrame = new MainFrame();
        mainFrame.setCallback(new MainFrame.MainFrameCallback() {
            @Override
            public void onAddCredential() {
                showAddCredentialDialog();
            }

            @Override
            public void onEditCredential(Credential credential) {
                showEditCredentialDialog(credential);
            }

            @Override
            public void onDeleteCredential(Credential credential) {
                if (mainFrame.confirmDelete(credential)) {
                    try {
                        vaultService.deleteCredential(credential.getId());
                        refreshCredentials();
                    } catch (Exception e) {
                        mainFrame.showError("Failed to delete credential: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCopyUsername(Credential credential) {
                String username = credential.getUsername();
                if (username != null && !username.isEmpty()) {
                    mainFrame.copyToClipboard(username);
                }
            }

            @Override
            public void onCopyPassword(Credential credential) {
                String password = credential.getEncryptedPassword();
                if (password != null && !password.isEmpty()) {
                    mainFrame.copyToClipboard(password);
                }
            }

            @Override
            public void onSearch(String searchTerm) {
                try {
                    List<Credential> results = vaultService.searchCredentials(vaultKey, searchTerm);
                    mainFrame.refreshTable(results);
                } catch (Exception e) {
                    mainFrame.showError("Search failed: " + e.getMessage());
                }
            }

            @Override
            public void onCategoryFilter(int categoryId) {
                if (categoryId == 0) {
                    refreshCredentials();
                } else {
                    try {
                        List<Credential> filtered = vaultService.getCredentialsByCategory(vaultKey, categoryId);
                        mainFrame.refreshTable(filtered);
                    } catch (Exception e) {
                        mainFrame.showError("Filter failed: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onLock() {
                mainFrame.cleanup();
                mainFrame.dispose();
                if (authController != null) {
                    authController.clearSession();
                }
            }

            @Override
            public void onShowPasswordGenerator() {
                PasswordGeneratorDialog dialog = new PasswordGeneratorDialog(mainFrame);
                dialog.setVisible(true);
            }

            @Override
            public void onExit() {
                try {
                    if (authController != null) {
                        authController.clearSession();
                    }
                } finally {
                    mainFrame.cleanup();
                    mainFrame.dispose();
                    System.exit(0);
                }
            }

            @Override
            public void onRefresh() {
                refreshCredentials();
            }
        });

        loadCategories();
        refreshCredentials();
        mainFrame.setVisible(true);
    }

    private void showAddCredentialDialog() {
        List<Category> categories = vaultService.getAllCategories();
        AddEditCredentialDialog dialog = new AddEditCredentialDialog(mainFrame, categories);
        dialog.setCallback(new AddEditCredentialDialog.CredentialCallback() {
            @Override
            public void onSave(Credential credential, String plaintextPassword) {
                try {
                    vaultService.addCredential(vaultKey, credential, plaintextPassword);
                    refreshCredentials();
                } catch (Exception e) {
                    mainFrame.showError("Failed to add credential: " + e.getMessage());
                }
            }

            @Override
            public void onCancel() {
                // nothing to do
            }
        });
        dialog.setVisible(true);
    }

    private void showEditCredentialDialog(Credential credential) {
        List<Category> categories = vaultService.getAllCategories();
        AddEditCredentialDialog dialog = new AddEditCredentialDialog(mainFrame, categories, credential);
        dialog.setCallback(new AddEditCredentialDialog.CredentialCallback() {
            @Override
            public void onSave(Credential updatedCredential, String plaintextPassword) {
                try {
                    vaultService.updateCredential(vaultKey, updatedCredential, plaintextPassword);
                    refreshCredentials();
                } catch (Exception e) {
                    mainFrame.showError("Failed to update credential: " + e.getMessage());
                }
            }

            @Override
            public void onCancel() {
                // nothing to do
            }
        });
        dialog.setVisible(true);
    }

    private void refreshCredentials() {
        try {
            List<Credential> credentials = vaultService.getAllCredentials(vaultKey);
            mainFrame.refreshTable(credentials);
        } catch (Exception e) {
            mainFrame.showError("Failed to load credentials: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            List<Category> categories = vaultService.getAllCategories();
            mainFrame.setCategories(categories);
        } catch (Exception e) {
            mainFrame.showError("Failed to load categories: " + e.getMessage());
        }
    }

}
