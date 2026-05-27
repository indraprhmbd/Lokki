package com.lokki;

import com.lokki.controller.AuthController;
import com.lokki.controller.VaultController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initLookAndFeel();

                final AuthController authController = new AuthController();
                authController.setCallback(new AuthController.AuthCallback() {
                    @Override
                    public void onAuthenticated(byte[] vaultKey) {
                        VaultController vaultController = new VaultController(vaultKey);
                        vaultController.setAuthController(authController);
                        vaultController.openMainFrame();
                    }

                    @Override
                    public void onSessionCleared() {
                        authController.startAuthFlow();
                    }
                });
                authController.startAuthFlow();

                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        authController.clearSession();
                    }
                }));
            }
        });
    }

    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // fall back to default look and feel
        }

        Font brandFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font brandFontBold = new Font("Segoe UI", Font.BOLD, 13);
        UIManager.put("defaultFont", brandFont);
        UIManager.put("Button.font", brandFontBold);
        UIManager.put("Label.font", brandFont);
        UIManager.put("TextField.font", brandFont);
        UIManager.put("PasswordField.font", brandFont);
        UIManager.put("TextArea.font", brandFont);
        UIManager.put("CheckBox.font", brandFont);
        UIManager.put("ComboBox.font", brandFont);
        UIManager.put("Table.font", brandFont);
        UIManager.put("TableHeader.font", brandFontBold);
        UIManager.put("MenuBar.font", brandFont);
        UIManager.put("Menu.font", brandFont);
        UIManager.put("MenuItem.font", brandFont);
        UIManager.put("ToolBar.font", brandFont);
        UIManager.put("TitledBorder.font", brandFontBold);
        UIManager.put("ProgressBar.font", brandFont);
    }
}
