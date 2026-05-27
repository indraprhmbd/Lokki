package com.lokki.view;

import com.lokki.util.AppIcon;
import com.lokki.view.component.PasswordStrengthBar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SetupFrame extends JFrame {

    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private PasswordStrengthBar strengthBar;
    private JButton setupButton;
    private JCheckBox showPasswordCheckbox;
    private SetupCallback callback;

    public interface SetupCallback {
        void onSetupComplete(char[] masterPassword);
    }

    public SetupFrame() {
        setTitle("Lokki - Setup Your Vault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(AppIcon.getIcon().getImage());
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    public void setCallback(SetupCallback callback) {
        this.callback = callback;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Your Master Password");
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel infoLabel = new JLabel("<html>This password unlocks your vault. It cannot be recovered if forgotten.<br>Save the recovery key you will be shown next.</html>");
        gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(new JLabel("Master Password:"), gbc);

        passwordField = new JPasswordField(24);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);

        confirmField = new JPasswordField(24);
        gbc.gridx = 1;
        mainPanel.add(confirmField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        strengthBar = new PasswordStrengthBar();
        mainPanel.add(strengthBar, gbc);

        showPasswordCheckbox = new JCheckBox("Show passwords");
        gbc.gridy = 5; gbc.gridwidth = 2;
        mainPanel.add(showPasswordCheckbox, gbc);

        setupButton = new JButton("Create Vault");
        setupButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(setupButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        getRootPane().setDefaultButton(setupButton);

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStrengthAndValidate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStrengthAndValidate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrengthAndValidate(); }
        });

        confirmField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStrengthAndValidate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStrengthAndValidate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrengthAndValidate(); }
        });

        showPasswordCheckbox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                char echoChar = showPasswordCheckbox.isSelected() ? (char) 0 : '\u2022';
                passwordField.setEchoChar(echoChar);
                confirmField.setEchoChar(echoChar);
            }
        });

        setupButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                char[] password = passwordField.getPassword();
                char[] confirm = confirmField.getPassword();
                if (!java.util.Arrays.equals(password, confirm)) {
                    JOptionPane.showMessageDialog(SetupFrame.this,
                            "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (callback != null) {
                    callback.onSetupComplete(password);
                }
            }
        });
    }

    private void updateStrengthAndValidate() {
        char[] password = passwordField.getPassword();
        char[] confirm = confirmField.getPassword();

        if (password.length > 0) {
            strengthBar.updateStrength(password);
        }

        boolean passwordsMatch = java.util.Arrays.equals(password, confirm);
        boolean sufficientStrength = PasswordStrengthBar.evaluateStrength(password).ordinal() >= PasswordStrengthBar.Strength.MEDIUM.ordinal();
        setupButton.setEnabled(passwordsMatch && sufficientStrength && password.length > 0);
    }

    public void showRecoveryKey(String formattedKey) {
        final javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Recovery Key", true);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 20, 5, 20);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        JLabel message = new JLabel("<html><h3>Your Recovery Key</h3>"
                + "<p>This key can unlock your vault if you forget your master password.</p>"
                + "<p>Write it down and store it securely. It will never be shown again.</p></html>");
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(message, gbc);

        JTextField keyField = new JTextField(formattedKey);
        keyField.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 18));
        keyField.setHorizontalAlignment(JTextField.CENTER);
        keyField.setEditable(false);
        keyField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY),
                javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        keyField.setBackground(new java.awt.Color(245, 245, 245));
        keyField.setSelectionColor(new java.awt.Color(0, 120, 215));
        keyField.selectAll();
        gbc.gridy = 1; gbc.insets = new java.awt.Insets(5, 20, 5, 20);
        panel.add(keyField, gbc);

        JLabel hintLabel = new JLabel("Press Ctrl+C to copy");
        hintLabel.setFont(hintLabel.getFont().deriveFont(java.awt.Font.ITALIC, 11f));
        hintLabel.setForeground(java.awt.Color.GRAY);
        hintLabel.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridy = 2; gbc.insets = new java.awt.Insets(0, 20, 10, 20);
        panel.add(hintLabel, gbc);

        JCheckBox confirmCheckbox = new JCheckBox("I have saved this recovery key in a safe place");
        gbc.gridy = 3; gbc.insets = new java.awt.Insets(5, 20, 5, 20);
        panel.add(confirmCheckbox, gbc);

        JButton continueButton = new JButton("Continue");
        continueButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        buttonPanel.add(continueButton);
        gbc.gridy = 4; gbc.insets = new java.awt.Insets(5, 20, 15, 20);
        panel.add(buttonPanel, gbc);

        dialog.getRootPane().setDefaultButton(continueButton);

        confirmCheckbox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                continueButton.setEnabled(confirmCheckbox.isSelected());
            }
        });

        continueButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
