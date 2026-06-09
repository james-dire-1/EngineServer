package com.james.userInterfaces;

import com.james.tools.ThreadManager;
import game.main.Main;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import templates.commands.CommandExecutor;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Headless implements Runnable {

    private final Thread thread;

    private final CountDownLatch initLatch = new CountDownLatch(1);
    private final CountDownLatch nonEditableLatch = new CountDownLatch(1);

    private volatile Terminal terminal;
    private volatile LineReader reader;

    public Headless() throws InterruptedException {
        this.thread = new Thread(this);
        thread.start();
        initLatch.await();
        instance = this;
    }

    @Override
    public void run() {
        try {
            Highlighter highlighter = (LineReader reader, String buffer) -> new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                    .append(buffer)
                    .toAttributedString();

            this.terminal = TerminalBuilder.builder().build();
            this.reader = LineReaderBuilder.builder().terminal(terminal).highlighter(highlighter).build();
            AttributedString prompt = new AttributedString(">>> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA));
            initLatch.countDown();

            try {
                while (!Main.shouldClose) {
                    String command = reader.readLine(prompt.toAnsi());

                    ThreadManager.executeOnMainThread(() -> {
                        CommandExecutor.execute(command);
                    });
                }
            } catch (UserInterruptException e) {
                nonEditableLatch.countDown();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void println(String message) {
        reader.printAbove(message);
    }

    public void makeNonEditable() {
        thread.interrupt();
        try {
            nonEditableLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        terminal.close();
    }

    private static Headless instance;
    public static Headless get() { return instance; }

}
