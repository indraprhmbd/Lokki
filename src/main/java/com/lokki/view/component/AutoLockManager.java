package com.lokki.view.component;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AutoLockManager {

    private static final int AUTO_LOCK_DELAY_MS = 5 * 60 * 1000;
    private static final int TICK_INTERVAL_MS = 5000;

    private final Timer timer;
    private final JLabel statusLabel;
    private final Runnable onLock;
    private int countdownSeconds;

    public AutoLockManager(JLabel statusLabel, Runnable onLock) {
        this.statusLabel = statusLabel;
        this.onLock = onLock;
        this.countdownSeconds = AUTO_LOCK_DELAY_MS / 1000;

        this.timer = new Timer(TICK_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownSeconds -= TICK_INTERVAL_MS / 1000;
                if (countdownSeconds <= 0) {
                    timer.stop();
                    statusLabel.setText(" ");
                    if (onLock != null) {
                        onLock.run();
                    }
                } else {
                    int minutes = countdownSeconds / 60;
                    int seconds = countdownSeconds % 60;
                    statusLabel.setText(String.format("Auto-lock in %d:%02d", minutes, seconds));
                }
            }
        });

        Toolkit.getDefaultToolkit().addAWTEventListener(new java.awt.event.AWTEventListener() {
            @Override
            public void eventDispatched(java.awt.AWTEvent event) {
                if (event.getSource() instanceof javax.swing.JComponent) {
                    reset();
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);

        reset();
    }

    public void reset() {
        countdownSeconds = AUTO_LOCK_DELAY_MS / 1000;
        if (!timer.isRunning()) {
            timer.start();
        } else {
            timer.restart();
        }
    }
}
