package com.lokki.view;

import com.lokki.util.AppIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RecoveryFrame extends JFrame {

    private JTextField[] recoveryFields;
    private JPasswordField newPasswordField;
    private JPasswordField confirmField;
    private JButton recoverButton;
    private JLabel statusLabel;
    private RecoveryCallback callback;

    public interface RecoveryCallback {
        void onRecover(char[] recoveryKey, char[] newMasterPassword);
        void onCancel();
    }

    public RecoveryFrame() {
        setTitle("Lokki - Account Recovery");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconImage(AppIcon.getIcon().getImage());
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    public void setCallback(RecoveryCallback callback) {
        this.callback = callback;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Recover Your Vault");
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel infoLabel = new JLabel("Enter your recovery key and create a new master password.");
        gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(infoLabel, gbc);

        JLabel recoveryLabel = new JLabel("Recovery Key:");
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1;
        mainPanel.add(recoveryLabel, gbc);

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        recoveryFields = new JTextField[6];
        for (int i = 0; i < 6; i++) {
            recoveryFields[i] = new JTextField(4);
            recoveryFields[i].setHorizontalAlignment(JTextField.CENTER);
            recoveryFields[i].setDocument(new javax.swing.text.PlainDocument() {
                @Override
                public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (str == null) return;
                    if (getLength() + str.length() > 4) return;
                    super.insertString(offs, str.toUpperCase(), a);
                }
            });
            keyPanel.add(recoveryFields[i]);
            if (i < 5) {
                keyPanel.add(new JLabel("-"));
            }
        }

        recoveryFields[0].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                recoveryFields[0].selectAll();
            }
        });

        recoveryFields[0].addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPasteMenu(e);
                }
            }
        });

        recoveryFields[0].addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if ((e.isControlDown() || e.isMetaDown()) && e.getKeyCode() == java.awt.event.KeyEvent.VK_V) {
                    handlePaste();
                }
            }
        });
        gbc.gridx = 1;
        mainPanel.add(keyPanel, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(new JLabel("New Password:"), gbc);
        newPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(newPasswordField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        mainPanel.add(new JLabel("Confirm:"), gbc);
        confirmField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(confirmField, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);

        recoverButton = new JButton("Recover Vault");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(recoverButton);
        buttonPanel.add(cancelButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        getRootPane().setDefaultButton(recoverButton);

        for (int i = 0; i < 6; i++) {
            final int index = i;
            recoveryFields[i].getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    if (recoveryFields[index].getText().length() == 4 && index < 5) {
                        recoveryFields[index + 1].requestFocusInWindow();
                    }
                    updateValidation();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
            });
        }

        javax.swing.event.DocumentListener passwordListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateValidation(); }
        };
        newPasswordField.getDocument().addDocumentListener(passwordListener);
        confirmField.getDocument().addDocumentListener(passwordListener);

        recoverButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                StringBuilder keyBuilder = new StringBuilder();
                for (JTextField field : recoveryFields) {
                    keyBuilder.append(field.getText());
                }
                char[] recoveryKey = keyBuilder.toString().toCharArray();
                char[] newPassword = newPasswordField.getPassword();
                char[] confirm = confirmField.getPassword();

                if (!java.util.Arrays.equals(newPassword, confirm)) {
                    statusLabel.setText("Passwords do not match.");
                    return;
                }

                if (callback != null) {
                    callback.onRecover(recoveryKey, newPassword);
                }
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });
    }

    private void updateValidation() {
        boolean allFieldsFilled = true;
        for (JTextField field : recoveryFields) {
            if (field.getText().length() != 4) {
                allFieldsFilled = false;
                break;
            }
        }
        boolean passwordsMatch = java.util.Arrays.equals(newPasswordField.getPassword(), confirmField.getPassword());
        boolean passwordsFilled = newPasswordField.getPassword().length > 0 && confirmField.getPassword().length > 0;
        recoverButton.setEnabled(allFieldsFilled && passwordsMatch && passwordsFilled);
    }

    private void handlePaste() {
        String clipboard = getClipboardText();
        if (clipboard == null || clipboard.isEmpty()) return;

        String clean = clipboard.replace("-", "").replace(" ", "").toUpperCase();
        if (clean.length() > 24) {
            clean = clean.substring(0, 24);
        }

        for (int i = 0; i < recoveryFields.length; i++) {
            int start = i * 4;
            if (start < clean.length()) {
                int end = Math.min(start + 4, clean.length());
                String group = clean.substring(start, end);
                recoveryFields[i].setText(group);
            } else {
                recoveryFields[i].setText("");
            }
        }

        updateValidation();
        int nextEmpty = 0;
        for (int i = 0; i < recoveryFields.length; i++) {
            if (recoveryFields[i].getText().length() < 4) {
                nextEmpty = i;
                break;
            }
        }
        recoveryFields[nextEmpty].requestFocusInWindow();
    }

    private String getClipboardText() {
        try {
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            java.awt.datatransfer.Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            // clipboard not accessible
        }
        return null;
    }

    private void showPasteMenu(java.awt.event.MouseEvent e) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem pasteItem = new javax.swing.JMenuItem("Paste Recovery Key");
        pasteItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handlePaste();
            }
        });
        menu.add(pasteItem);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public void clearAllFields() {
        for (JTextField field : recoveryFields) {
            field.setText("");
        }
        recoveryFields[0].requestFocusInWindow();
        updateValidation();
    }
}
