package com.lokki.view.component;

import com.lokki.view.MainFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainMenuBar extends JMenuBar {

    private MainFrame.MainFrameCallback callback;

    public MainMenuBar() {
        initMenus();
    }

    public void setCallback(MainFrame.MainFrameCallback callback) {
        this.callback = callback;
    }

    private void initMenus() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem lockItem = new JMenuItem("Lock Vault", KeyEvent.VK_L);
        lockItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        lockItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) callback.onLock();
            }
        });
        fileMenu.add(lockItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) callback.onExit();
            }
        });
        fileMenu.add(exitItem);
        add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem addItem = new JMenuItem("Add Credential", KeyEvent.VK_A);
        addItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) callback.onAddCredential();
            }
        });
        editMenu.add(addItem);
        add(editMenu);

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem passwordGenItem = new JMenuItem("Password Generator", KeyEvent.VK_G);
        passwordGenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        passwordGenItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) callback.onShowPasswordGenerator();
            }
        });
        toolsMenu.add(passwordGenItem);
        add(toolsMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutItem = new JMenuItem("About Lokki");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Lokki - Local Password Manager\nVersion 1.0.0\n\nAll data stays on your machine.\nNo network calls are made.",
                        "About Lokki", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);
        add(helpMenu);
    }
}
