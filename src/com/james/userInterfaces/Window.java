package com.james.userInterfaces;

import com.james.tools.ThreadManager;
import game.main.Main;
import templates.commands.CommandExecutor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window {

    public boolean requestedClose = false;

    private final JFrame frame;
    private final JTextArea messageHistory;
    private JTextField typingArea = null;

    public Window(String title, int width, int height) {
        WindowAdapter onWindowClose = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Main.everythingIsCompleted) {
                    instance.frame.dispose();
                }

                ThreadManager.executeOnMainThread(() -> {
                    if (!Main.shouldClose) {
                        CommandExecutor.execute("stop");
                    }
                    requestedClose = true;
                });
            }
        };

        ActionListener onInputReceived = (ActionEvent e) -> {
            String command = e.getActionCommand();
            typingArea.setText("");

            ThreadManager.executeOnMainThread(() -> {
                CommandExecutor.execute(command);
            });
        };

        this.frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(onWindowClose);

        this.messageHistory = new JTextArea();
        messageHistory.setEditable(false);
        frame.add(new JScrollPane(messageHistory), BorderLayout.CENTER);

        this.typingArea = new JTextField();
        typingArea.setEditable(true);
        typingArea.addActionListener(onInputReceived);
        frame.add(typingArea, BorderLayout.SOUTH);

        frame.setSize(width, height);
        frame.setVisible(true);

        instance = this;
    }

    public void println(String message) {
        SwingUtilities.invokeLater(() -> {
            instance.messageHistory.append(message + "\n");
        });
    }

    public void makeNonEditable() {
        SwingUtilities.invokeLater(() -> {
            typingArea.setEditable(false);
        });
    }

    public void dispose() {
        SwingUtilities.invokeLater(() -> {
            instance.frame.dispose();
        });
    }

    private static volatile Window instance;
    public static Window get() { return instance; }

}
