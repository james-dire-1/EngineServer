package com.james.window;

import templates.commands.CommandExecutor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window {

    private final JTextArea messageHistory;

    public Window(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.messageHistory = new JTextArea();
        frame.add(new JScrollPane(messageHistory), BorderLayout.CENTER);

        JTextField typingArea = new JTextField();
        typingArea.addActionListener((ActionEvent e) -> {
            CommandExecutor.execute(e.getActionCommand());
            typingArea.setText("");
        });
        frame.add(typingArea, BorderLayout.SOUTH);

        frame.setSize(width, height);
        frame.setVisible(true);

        instance = this;
    }

    public void println(String message) {
        messageHistory.append(message + "\n");
    }

    private static volatile Window instance;
    public static Window get() { return instance; }

}
