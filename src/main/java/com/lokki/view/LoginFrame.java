package com.lokki.view;

import com.lokki.util.AppIcon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {

    private JPasswordField passwordField;
    private JButton unlockButton;
    private JLabel statusLabel;
    private JLabel attemptsLabel;
    private LoginCallback callback;

    private int remainingAttempts = 3;
    private javax.swing.Timer cooldownTimer;

    public interface LoginCallback {
        void onLogin(char[] masterPassword);
        void onRecoveryRequested();
    }

    public LoginFrame() {
        setTitle("Lokki - Local Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(AppIcon.getIcon().getImage());
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    public void setCallback(LoginCallback callback) {
        this.callback = callback;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Unlock Your Vault");
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Master Password:"), gbc);

        passwordField = new JPasswordField(24);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        attemptsLabel = new JLabel(" ");
        attemptsLabel.setForeground(new java.awt.Color(220, 53, 69));
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(attemptsLabel, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        gbc.gridy = 3;
        mainPanel.add(statusLabel, gbc);

        unlockButton = new JButton("Unlock");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(unlockButton);

        JButton recoveryButton = new JButton("Forgot Master Password?");
        recoveryButton.setBorderPainted(false);
        recoveryButton.setContentAreaFilled(false);
        recoveryButton.setForeground(new java.awt.Color(0, 102, 204));
        recoveryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        recoveryButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (callback != null) {
                    callback.onRecoveryRequested();
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(unlockButton);
        bottomPanel.add(recoveryButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(contentPanel);

        getRootPane().setDefaultButton(unlockButton);

        unlockButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (remainingAttempts <= 0) {
                    return;
                }
                char[] password = passwordField.getPassword();
                if (password.length == 0) {
                    statusLabel.setText("Please enter your master password.");
                    return;
                }
                if (callback != null) {
                    callback.onLogin(password);
                }
            }
        });
    }

    public void onLoginFailed() {
        remainingAttempts--;
        if (remainingAttempts > 0) {
            attemptsLabel.setText(remainingAttempts + " attempt(s) remaining");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        } else {
            attemptsLabel.setText("Too many failed attempts");
            passwordField.setEnabled(false);
            unlockButton.setEnabled(false);
            startCooldown();
        }
    }

    private void startCooldown() {
        cooldownTimer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            int countdown = 5;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                countdown--;
                statusLabel.setText("Try again in " + countdown + "s");
                if (countdown <= 0) {
                    cooldownTimer.stop();
                    remainingAttempts = 3;
                    attemptsLabel.setText(" ");
                    statusLabel.setText(" ");
                    passwordField.setEnabled(true);
                    unlockButton.setEnabled(true);
                    passwordField.setText("");
                    passwordField.requestFocusInWindow();
                }
            }
        });
        cooldownTimer.start();
    }

    public void onLoginSuccess() {
        remainingAttempts = 3;
        attemptsLabel.setText(" ");
        statusLabel.setText(" ");
        passwordField.setText("");
    }
}
