package com.lokki.view.component;

import javax.swing.Timer;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

public class ClipboardTimer {

    private static final int CLEAR_INTERVAL_MS = 1000;
    private static final int TOTAL_SECONDS = 30;

    private final Timer timer;
    private int remainingSeconds;
    private Consumer<Integer> onTick;
    private Runnable onComplete;

    /**
     * Starts a 30-second countdown. Every second the onTick callback is invoked
     * with the remaining seconds. When time runs out, the clipboard is cleared
     * and the onComplete callback is invoked.
     */
    public ClipboardTimer(Consumer<Integer> onTick, Runnable onComplete) {
        this.onTick = onTick;
        this.onComplete = onComplete;
        this.remainingSeconds = TOTAL_SECONDS;

        this.timer = new Timer(CLEAR_INTERVAL_MS, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                remainingSeconds--;
                if (remainingSeconds <= 0) {
                    timer.stop();
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new StringSelection(""), null);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                } else {
                    if (onTick != null) {
                        onTick.accept(remainingSeconds);
                    }
                }
            }
        });
    }

    /**
     * Starts or restarts the countdown from 30 seconds.
     */
    public void start() {
        remainingSeconds = TOTAL_SECONDS;
        timer.stop();
        timer.start();
        if (onTick != null) {
            onTick.accept(remainingSeconds);
        }
    }

    /**
     * Stops the timer and clears the callback references.
     */
    public void stop() {
        timer.stop();
    }
}
