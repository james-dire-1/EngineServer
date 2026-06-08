package com.james.common.tools;

import com.james.window.Window;
import templates.common.GlobalConstants;

public class Logger {

    public static void println(String message) {
        if (GlobalConstants.headless) {
            System.out.println(message);
        } else {
            Window.get().println(message);
        }
    }

}
