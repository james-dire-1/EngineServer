package com.james.common.tools;

import com.james.userInterfaces.Headless;
import com.james.userInterfaces.Window;
import templates.common.GlobalConstants;

public class Logger {

    public static void println(String message) {
        if (GlobalConstants.headless) {
            Headless.get().println(message);
        } else {
            Window.get().println(message);
        }
    }

}
