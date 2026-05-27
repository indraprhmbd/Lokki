package com.lokki.view.component;

import com.lokki.model.Credential;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CredentialTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Site Name", "Username", "Password", "Category", "Updated"};
    private List<Credential> credentials;

    public CredentialTableModel() {
        this.credentials = new ArrayList<>();
    }

    /**
     * Replaces the entire data set and fires a table update.
     */
    public void setCredentials(List<Credential> credentials) {
        this.credentials = credentials;
        fireTableDataChanged();
    }

    /**
     * Returns the credential at the given row index.
     */
    public Credential getCredentialAt(int row) {
        return credentials.get(row);
    }

    @Override
    public int getRowCount() {
        return credentials.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Credential credential = credentials.get(rowIndex);
        switch (columnIndex) {
            case 0: return credential.getSiteName();
            case 1: return credential.getUsername();
            case 2: return "********";
            case 3: return credential.getCategoryName();
            case 4: return credential.getUpdatedAt() != null ? credential.getUpdatedAt() : credential.getCreatedAt();
            default: return null;
        }
    }
}
