package com.lokki.view;

import com.lokki.service.PasswordGeneratorService;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PasswordGeneratorDialog extends JDialog {

    private JSpinner lengthSpinner;
    private JCheckBox uppercaseCheckbox;
    private JCheckBox lowercaseCheckbox;
    private JCheckBox digitsCheckbox;
    private JCheckBox symbolsCheckbox;
    private JTextField previewField;
    private JButton useButton;
    private String selectedPassword;
    private PasswordSelectionCallback callback;

    public interface PasswordSelectionCallback {
        void onPasswordSelected(String password);
    }

    public PasswordGeneratorDialog(JFrame parent) {
        super(parent, "Password Generator", true);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        generatePassword();
    }

    public void setCallback(PasswordSelectionCallback callback) {
        this.callback = callback;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Length:"), gbc);

        lengthSpinner = new JSpinner(new SpinnerNumberModel(20, 8, 64, 1));
        gbc.gridx = 1;
        mainPanel.add(lengthSpinner, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        uppercaseCheckbox = new JCheckBox("Uppercase (A-Z)", true);
        mainPanel.add(uppercaseCheckbox, gbc);

        gbc.gridy = 2;
        lowercaseCheckbox = new JCheckBox("Lowercase (a-z)", true);
        mainPanel.add(lowercaseCheckbox, gbc);

        gbc.gridy = 3;
        digitsCheckbox = new JCheckBox("Digits (0-9)", true);
        mainPanel.add(digitsCheckbox, gbc);

        gbc.gridy = 4;
        symbolsCheckbox = new JCheckBox("Symbols (!@#$...)", true);
        mainPanel.add(symbolsCheckbox, gbc);

        gbc.gridy = 5; gbc.gridwidth = 2;
        previewField = new JTextField(24);
        previewField.setEditable(false);
        previewField.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
        previewField.setHorizontalAlignment(JTextField.CENTER);
        mainPanel.add(previewField, gbc);

        JButton regenerateButton = new JButton("Regenerate");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(regenerateButton);

        useButton = new JButton("Use This Password");
        buttonPanel.add(useButton);
        useButton.setEnabled(false);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        getRootPane().setDefaultButton(useButton);

        java.awt.event.ActionListener generateListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                generatePassword();
            }
        };

        lengthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                generatePassword();
            }
        });
        uppercaseCheckbox.addActionListener(generateListener);
        lowercaseCheckbox.addActionListener(generateListener);
        digitsCheckbox.addActionListener(generateListener);
        symbolsCheckbox.addActionListener(generateListener);

        regenerateButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                generatePassword();
            }
        });

        useButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (callback != null && selectedPassword != null) {
                    callback.onPasswordSelected(selectedPassword);
                }
                dispose();
            }
        });
    }

    private void generatePassword() {
        int length = (Integer) lengthSpinner.getValue();
        boolean useUpper = uppercaseCheckbox.isSelected();
        boolean useLower = lowercaseCheckbox.isSelected();
        boolean useDigits = digitsCheckbox.isSelected();
        boolean useSymbols = symbolsCheckbox.isSelected();

        if (!useUpper && !useLower && !useDigits && !useSymbols) {
            previewField.setText("Select at least one character set");
            useButton.setEnabled(false);
            selectedPassword = null;
            return;
        }

        selectedPassword = PasswordGeneratorService.generate(length, useUpper, useLower, useDigits, useSymbols);
        previewField.setText(selectedPassword);
        useButton.setEnabled(true);
    }
}
