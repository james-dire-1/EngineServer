package com.james.tools;

import com.james.window.Window;
import templates.common.GlobalConstants;

public class Logger {

    public static void log(String message) {
        if (GlobalConstants.printingMode == GlobalConstants.PrintingMode.STDOUT) {
            System.out.println(message);
        } else if (GlobalConstants.printingMode == GlobalConstants.PrintingMode.WINDOW) {
            Window.get().println(message);
        }
    }

}
