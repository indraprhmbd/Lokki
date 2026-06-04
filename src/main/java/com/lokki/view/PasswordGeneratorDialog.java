package com.lokki.view;

import com.lokki.service.PassphraseGenerator;
import com.lokki.service.PasswordGenerator;
import com.lokki.service.RandomPasswordGenerator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PasswordGeneratorDialog extends JDialog {

    public interface PasswordSelectionCallback {
        void onPasswordSelected(String password);
    }

    private JComboBox<String> modeCombo;
    private JPanel settingsPanel;
    private CardLayout cardLayout;

    private JSpinner lengthSpinner;
    private JCheckBox uppercaseCheckbox;
    private JCheckBox lowercaseCheckbox;
    private JCheckBox digitsCheckbox;
    private JCheckBox symbolsCheckbox;

    private JSpinner wordCountSpinner;
    private JTextField separatorField;
    private JCheckBox capitalizeCheckbox;
    private JCheckBox passphraseDigitCheckbox;
    private JCheckBox passphraseSymbolCheckbox;

    private JTextField previewField;
    private JButton useButton;
    private String selectedPassword;
    private PasswordSelectionCallback callback;

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
        mainPanel.add(new JLabel("Mode:"), gbc);

        modeCombo = new JComboBox<>(new String[]{"Random", "Passphrase"});
        gbc.gridx = 1;
        mainPanel.add(modeCombo, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        cardLayout = new CardLayout();
        settingsPanel = new JPanel(cardLayout);
        settingsPanel.add(createRandomPanel(), "RANDOM");
        settingsPanel.add(createPassphrasePanel(), "PASSPHRASE");
        mainPanel.add(settingsPanel, gbc);

        gbc.gridy = 2; gbc.gridwidth = 2;
        previewField = new JTextField(24);
        previewField.setEditable(false);
        previewField.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
        previewField.setHorizontalAlignment(JTextField.CENTER);
        mainPanel.add(previewField, gbc);

        gbc.gridy = 3;
        useButton = new JButton("Use This Password");
        useButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(useButton);
        JButton regenerateButton = new JButton("Regenerate");
        buttonPanel.add(regenerateButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        getRootPane().setDefaultButton(useButton);

        modeCombo.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (modeCombo.getSelectedIndex() == 0) {
                    cardLayout.show(settingsPanel, "RANDOM");
                } else {
                    cardLayout.show(settingsPanel, "PASSPHRASE");
                }
                generatePassword();
            }
        });

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

        wordCountSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                generatePassword();
            }
        });
        separatorField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { generatePassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { generatePassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { generatePassword(); }
        });
        capitalizeCheckbox.addActionListener(generateListener);
        passphraseDigitCheckbox.addActionListener(generateListener);
        passphraseSymbolCheckbox.addActionListener(generateListener);

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

    private JPanel createRandomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Length:"), gbc);

        lengthSpinner = new JSpinner(new SpinnerNumberModel(20, 8, 64, 1));
        gbc.gridx = 1;
        panel.add(lengthSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        uppercaseCheckbox = new JCheckBox("Uppercase (A-Z)", true);
        panel.add(uppercaseCheckbox, gbc);

        gbc.gridy = 2;
        lowercaseCheckbox = new JCheckBox("Lowercase (a-z)", true);
        panel.add(lowercaseCheckbox, gbc);

        gbc.gridy = 3;
        digitsCheckbox = new JCheckBox("Digits (0-9)", true);
        panel.add(digitsCheckbox, gbc);

        gbc.gridy = 4;
        symbolsCheckbox = new JCheckBox("Symbols (!@#$%...)", true);
        panel.add(symbolsCheckbox, gbc);

        return panel;
    }

    private JPanel createPassphrasePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Word count:"), gbc);

        wordCountSpinner = new JSpinner(new SpinnerNumberModel(4, 2, 10, 1));
        gbc.gridx = 1;
        panel.add(wordCountSpinner, gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Separator:"), gbc);

        separatorField = new JTextField("-", 6);
        gbc.gridx = 1;
        panel.add(separatorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        capitalizeCheckbox = new JCheckBox("Capitalize each word", true);
        panel.add(capitalizeCheckbox, gbc);

        gbc.gridy = 3;
        passphraseDigitCheckbox = new JCheckBox("Append a digit", true);
        panel.add(passphraseDigitCheckbox, gbc);

        gbc.gridy = 4;
        passphraseSymbolCheckbox = new JCheckBox("Append a symbol", false);
        panel.add(passphraseSymbolCheckbox, gbc);

        return panel;
    }

    private void generatePassword() {
        boolean isRandom = modeCombo.getSelectedIndex() == 0;

        if (isRandom) {
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

            PasswordGenerator generator = new RandomPasswordGenerator(length, useUpper, useLower, useDigits, useSymbols);
            selectedPassword = generator.generate();
        } else {
            int wordCount = (Integer) wordCountSpinner.getValue();
            String separator = separatorField.getText();
            boolean capitalize = capitalizeCheckbox.isSelected();
            boolean includeDigit = passphraseDigitCheckbox.isSelected();
            boolean includeSymbol = passphraseSymbolCheckbox.isSelected();

            PasswordGenerator generator = new PassphraseGenerator(wordCount, separator, capitalize, includeDigit, includeSymbol);
            selectedPassword = generator.generate();
        }

        previewField.setText(selectedPassword);
        useButton.setEnabled(true);
    }

}
