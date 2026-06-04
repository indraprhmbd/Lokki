package com.lokki.view;

import com.lokki.model.Category;
import com.lokki.model.Credential;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class AddEditCredentialDialog extends JDialog {

    private JTextField siteNameField;
    private JTextField siteUrlField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckbox;
    private JComboBox<Category> categoryCombo;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton generateButton;
    private CredentialCallback callback;
    private Credential editingCredential;

    public interface CredentialCallback {
        void onSave(Credential credential, String plaintextPassword);
        void onCancel();
    }

    public AddEditCredentialDialog(JFrame parent, List<Category> categories) {
        this(parent, categories, null);
    }

    public AddEditCredentialDialog(JFrame parent, List<Category> categories, Credential credential) {
        super(parent, credential == null ? "Add Credential" : "Edit Credential", true);
        this.editingCredential = credential;
        initComponents(categories);
        pack();
        setLocationRelativeTo(parent);
        if (credential != null) {
            populateFields(credential);
        }
    }

    public void setCallback(CredentialCallback callback) {
        this.callback = callback;
    }

    private void initComponents(List<Category> categories) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Site Name:*"), gbc);
        siteNameField = new JTextField(24);
        gbc.gridx = 1;
        mainPanel.add(siteNameField, gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Site URL:"), gbc);
        siteUrlField = new JTextField(24);
        gbc.gridx = 1;
        mainPanel.add(siteUrlField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(24);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:*"), gbc);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        passwordField = new JPasswordField(18);
        passwordPanel.add(passwordField);

        generateButton = new JButton("Generate");
        passwordPanel.add(generateButton);

        showPasswordCheckbox = new JCheckBox("Show");
        passwordPanel.add(showPasswordCheckbox);
        gbc.gridx = 1;
        mainPanel.add(passwordPanel, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        mainPanel.add(new JLabel("Category:"), gbc);
        categoryCombo = new JComboBox<>();
        for (Category category : categories) {
            categoryCombo.addItem(category);
            if (editingCredential != null && category.getId() == editingCredential.getCategoryId()) {
                categoryCombo.setSelectedItem(category);
            }
        }
        gbc.gridx = 1;
        mainPanel.add(categoryCombo, gbc);

        gbc.gridy = 5; gbc.gridx = 0;
        mainPanel.add(new JLabel("Notes:"), gbc);
        notesArea = new JTextArea(3, 24);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(new javax.swing.JScrollPane(notesArea), BorderLayout.CENTER);
        JLabel notesInfo = new JLabel("Notes are stored as plaintext");
        notesInfo.setFont(notesInfo.getFont().deriveFont(java.awt.Font.ITALIC, 10f));
        notesInfo.setForeground(java.awt.Color.GRAY);
        notesPanel.add(notesInfo, BorderLayout.SOUTH);
        gbc.gridx = 1;
        mainPanel.add(notesPanel, gbc);

        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        getRootPane().setDefaultButton(saveButton);

        javax.swing.event.DocumentListener fieldListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
        };
        siteNameField.getDocument().addDocumentListener(fieldListener);
        passwordField.getDocument().addDocumentListener(fieldListener);

        showPasswordCheckbox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                char echoChar = showPasswordCheckbox.isSelected() ? (char) 0 : '\u2022';
                passwordField.setEchoChar(echoChar);
            }
        });

        generateButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PasswordGeneratorDialog dialog = new PasswordGeneratorDialog((JFrame) getParent());
                dialog.setCallback(new PasswordGeneratorDialog.PasswordSelectionCallback() {
                    @Override
                    public void onPasswordSelected(String password) {
                        passwordField.setText(password);
                    }
                });
                dialog.setVisible(true);
            }
        });

        saveButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String siteName = siteNameField.getText().trim();
                String siteUrl = siteUrlField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String notes = notesArea.getText().trim();
                Category selectedCategory = (Category) categoryCombo.getSelectedItem();

                if (siteName.isEmpty()) {
                    JOptionPane.showMessageDialog(AddEditCredentialDialog.this,
                            "Site name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Credential credential;
                if (editingCredential != null) {
                    credential = editingCredential;
                    credential.setSiteName(siteName);
                    credential.setSiteUrl(siteUrl);
                    credential.setUsername(username);
                    credential.setCategoryId(selectedCategory.getId());
                    credential.setNotes(notes);
                } else {
                    credential = new Credential(siteName, siteUrl, username, "", selectedCategory.getId(), notes);
                }

                if (callback != null) {
                    callback.onSave(credential, password);
                }
                dispose();
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (callback != null) {
                    callback.onCancel();
                }
                dispose();
            }
        });

        getRootPane().registerKeyboardAction(
            new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            },
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void populateFields(Credential credential) {
        siteNameField.setText(credential.getSiteName());
        siteUrlField.setText(credential.getSiteUrl());
        usernameField.setText(credential.getUsername());
        passwordField.setText("");
        notesArea.setText(credential.getNotes());
    }

    private void updateValidation() {
        boolean valid = siteNameField.getText().trim().length() > 0
                && (editingCredential != null || passwordField.getPassword().length > 0);
        saveButton.setEnabled(valid);
    }
}
