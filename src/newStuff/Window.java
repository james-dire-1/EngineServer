package newStuff;

import javax.swing.*;
import java.awt.*;

public class Window {

    private final JTextArea messageHistory;

    public Window(String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.messageHistory = new JTextArea();
        frame.add(new JScrollPane(messageHistory), BorderLayout.CENTER);

        JTextField typingArea = new JTextField();
        frame.add(typingArea, BorderLayout.SOUTH);

        frame.setSize(700, 400);
//        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        instance = this;
    }

    public void println(String message) {
        messageHistory.append(message + "\n");
    }

    private static volatile Window instance;
    public static Window get() { return instance; }

}
