package com.lokki.view;

import com.lokki.model.Category;
import com.lokki.model.Credential;
import com.lokki.util.AppIcon;
import com.lokki.view.component.AutoLockManager;
import com.lokki.view.component.ClipboardTimer;
import com.lokki.view.component.CredentialTableModel;
import com.lokki.view.component.MainMenuBar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {

    private JTable credentialTable;
    private CredentialTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Category> categoryFilter;
    private JLabel statusLabel;
    private JLabel clipboardLabel;
    private JLabel autoLockLabel;
    private ClipboardTimer clipboardTimer;
    private AutoLockManager autoLockManager;
    private MainMenuBar menuBar;

    private MainFrameCallback callback;
    private List<Credential> currentCredentials;

    public interface MainFrameCallback {
        void onAddCredential();
        void onEditCredential(Credential credential);
        void onDeleteCredential(Credential credential);
        void onCopyUsername(Credential credential);
        void onCopyPassword(Credential credential);
        void onSearch(String searchTerm);
        void onCategoryFilter(int categoryId);
        void onShowPasswordGenerator();
        void onLock();
        void onExit();
        void onRefresh();
    }

    public MainFrame() {
        setTitle("Lokki - Vault");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setIconImage(AppIcon.getIcon().getImage());
        initComponents();
        initClipboardTimer();
    }

    public void setCallback(MainFrameCallback callback) {
        this.callback = callback;
        if (menuBar != null) {
            menuBar.setCallback(callback);
        }
    }

    public void initComponents() {
        menuBar = new MainMenuBar();
        setJMenuBar(menuBar);

        add(createStatusBar(), BorderLayout.SOUTH);

        autoLockManager = new AutoLockManager(autoLockLabel, new Runnable() {
            @Override
            public void run() {
                if (callback != null) callback.onLock();
            }
        });

        add(createToolBar(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (callback != null) {
                    callback.onExit();
                }
            }
        });
    }

    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        toolbar.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        toolbar.add(searchField);

        toolbar.addSeparator();

        toolbar.add(new JLabel("Category:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem(new Category(0, "All Categories"));
        toolbar.add(categoryFilter);

        toolbar.addSeparator();

        JButton addButton = new JButton("Add Credential");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) callback.onAddCredential();
            }
        });
        toolbar.add(addButton);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });

        categoryFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (categoryFilter.getSelectedIndex() > 0 && callback != null) {
                    Category selected = (Category) categoryFilter.getSelectedItem();
                    callback.onCategoryFilter(selected.getId());
                } else if (categoryFilter.getSelectedIndex() == 0 && callback != null) {
                    callback.onRefresh();
                }
            }
        });

        autoLockManager.reset();
        return toolbar;
    }

    private void search() {
        String term = searchField.getText().trim();
        if (callback != null) {
            if (term.isEmpty()) {
                callback.onRefresh();
            } else {
                callback.onSearch(term);
            }
        }
        autoLockManager.reset();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new CredentialTableModel();
        credentialTable = new JTable(tableModel);
        credentialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        credentialTable.setRowHeight(28);
        credentialTable.getTableHeader().setReorderingAllowed(false);
        credentialTable.setAutoCreateRowSorter(true);

        credentialTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = credentialTable.getSelectedRow();
                    if (row >= 0 && callback != null) {
                        int modelRow = credentialTable.convertRowIndexToModel(row);
                        callback.onEditCredential(tableModel.getCredentialAt(modelRow));
                    }
                }
                autoLockManager.reset();
            }
        });

        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem copyUserItem = new JMenuItem("Copy Username");
        copyUserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = credentialTable.getSelectedRow();
                if (row >= 0 && callback != null) {
                    int modelRow = credentialTable.convertRowIndexToModel(row);
                    callback.onCopyUsername(tableModel.getCredentialAt(modelRow));
                }
                autoLockManager.reset();
            }
        });
        contextMenu.add(copyUserItem);

        JMenuItem copyPassItem = new JMenuItem("Copy Password");
        copyPassItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = credentialTable.getSelectedRow();
                if (row >= 0 && callback != null) {
                    int modelRow = credentialTable.convertRowIndexToModel(row);
                    callback.onCopyPassword(tableModel.getCredentialAt(modelRow));
                }
                autoLockManager.reset();
            }
        });
        contextMenu.add(copyPassItem);

        contextMenu.addSeparator();

        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = credentialTable.getSelectedRow();
                if (row >= 0 && callback != null) {
                    int modelRow = credentialTable.convertRowIndexToModel(row);
                    callback.onEditCredential(tableModel.getCredentialAt(modelRow));
                }
                autoLockManager.reset();
            }
        });
        contextMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = credentialTable.getSelectedRow();
                if (row >= 0 && callback != null) {
                    int modelRow = credentialTable.convertRowIndexToModel(row);
                    callback.onDeleteCredential(tableModel.getCredentialAt(modelRow));
                }
                autoLockManager.reset();
            }
        });
        contextMenu.add(deleteItem);

        credentialTable.setComponentPopupMenu(contextMenu);

        panel.add(new JScrollPane(credentialTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        statusLabel = new JLabel("Vault unlocked");
        statusBar.add(statusLabel);

        clipboardLabel = new JLabel(" ");
        clipboardLabel.setForeground(new java.awt.Color(0, 102, 204));
        statusBar.add(clipboardLabel);

        autoLockLabel = new JLabel(" ");
        autoLockLabel.setForeground(java.awt.Color.GRAY);
        statusBar.add(autoLockLabel);

        return statusBar;
    }

    private void initClipboardTimer() {
        clipboardTimer = new ClipboardTimer(
            new java.util.function.Consumer<Integer>() {
                @Override
                public void accept(Integer seconds) {
                    clipboardLabel.setText("Password copied - clears in " + seconds + "s");
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    clipboardLabel.setText("Clipboard cleared");
                    Timer clearLabelTimer = new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            clipboardLabel.setText(" ");
                        }
                    });
                    clearLabelTimer.setRepeats(false);
                    clearLabelTimer.start();
                }
            }
        );
    }

    public void refreshTable(List<Credential> credentials) {
        this.currentCredentials = credentials;
        tableModel.setCredentials(credentials);
        statusLabel.setText("Vault unlocked - " + credentials.size() + " credential(s)");
        autoLockManager.reset();
    }

    public void setCategories(List<Category> categories) {
        categoryFilter.removeAllItems();
        categoryFilter.addItem(new Category(0, "All Categories"));
        for (Category category : categories) {
            categoryFilter.addItem(category);
        }
    }

    public void startClipboardCountdown() {
        clipboardTimer.start();
        autoLockManager.reset();
    }

    public void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        startClipboardCountdown();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmDelete(Credential credential) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the credential for \"" + credential.getSiteName() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    public void cleanup() {
        autoLockManager.stop();
        clipboardTimer.stop();
    }
}
