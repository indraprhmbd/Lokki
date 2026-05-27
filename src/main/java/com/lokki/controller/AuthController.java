package com.lokki.controller;

import com.lokki.service.AuthService;
import com.lokki.service.RecoveryKeyService;
import com.lokki.util.RecoveryKeyFormatter;
import com.lokki.view.LoginFrame;
import com.lokki.view.RecoveryFrame;
import com.lokki.view.SetupFrame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.Arrays;

public class AuthController {

    private final AuthService authService;
    private byte[] vaultKey;

    private SetupFrame setupFrame;
    private LoginFrame loginFrame;
    private RecoveryFrame recoveryFrame;
    private JFrame parentFrame;
    private AuthCallback callback;

    public interface AuthCallback {
        void onAuthenticated(byte[] vaultKey);
        void onSessionCleared();
    }

    public AuthController() {
        this.authService = new AuthService();
    }

    public void setCallback(AuthCallback callback) {
        this.callback = callback;
    }

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Starts the authentication flow. Shows SetupFrame on first run, LoginFrame otherwise.
     */
    public void startAuthFlow() {
        if (authService.isFirstRun()) {
            showSetupFrame();
        } else {
            showLoginFrame();
        }
    }

    private void showSetupFrame() {
        setupFrame = new SetupFrame();
        setupFrame.setCallback(new SetupFrame.SetupCallback() {
            @Override
            public void onSetupComplete(char[] masterPassword) {
                try {
                    char[] recoveryKey = RecoveryKeyService.generate();
                    String formattedKey = RecoveryKeyFormatter.format(recoveryKey);
                    try {
                        vaultKey = authService.setupVault(masterPassword, recoveryKey);
                        setupFrame.showRecoveryKey(formattedKey);
                        setupFrame.dispose();
                        if (callback != null) {
                            callback.onAuthenticated(vaultKey);
                        }
                    } finally {
                        Arrays.fill(recoveryKey, '\u0000');
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(setupFrame,
                            "Failed to create vault: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        setupFrame.setVisible(true);
    }

    private void showLoginFrame() {
        loginFrame = new LoginFrame();
        loginFrame.setCallback(new LoginFrame.LoginCallback() {
            @Override
            public void onLogin(char[] masterPassword) {
                try {
                    vaultKey = authService.login(masterPassword);
                    loginFrame.onLoginSuccess();
                    loginFrame.dispose();
                    if (callback != null) {
                        callback.onAuthenticated(vaultKey);
                    }
                } catch (Exception e) {
                    loginFrame.onLoginFailed();
                }
            }

            @Override
            public void onRecoveryRequested() {
                loginFrame.dispose();
                showRecoveryFrame();
            }
        });
        loginFrame.setVisible(true);
    }

    private void showRecoveryFrame() {
        recoveryFrame = new RecoveryFrame();
        recoveryFrame.setCallback(new RecoveryFrame.RecoveryCallback() {
            @Override
            public void onRecover(char[] recoveryKey, char[] newMasterPassword) {
                try {
                    vaultKey = authService.recoverWithKey(recoveryKey, newMasterPassword);
                    recoveryFrame.dispose();
                    if (callback != null) {
                        callback.onAuthenticated(vaultKey);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(recoveryFrame,
                            "Recovery failed. Please check your recovery key.",
                            "Recovery Failed", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void onCancel() {
                recoveryFrame.dispose();
                showLoginFrame();
            }
        });
        recoveryFrame.setVisible(true);
    }

    /**
     * Clears the session by zeroing the vault key and calls back.
     */
    public void clearSession() {
        if (vaultKey != null) {
            Arrays.fill(vaultKey, (byte) 0);
            vaultKey = null;
        }
        authService.clearSession();
        if (callback != null) {
            callback.onSessionCleared();
        }
    }

    /**
     * Returns the current vault key, or null if not authenticated.
     */
    public byte[] getVaultKey() {
        return vaultKey;
    }
}
