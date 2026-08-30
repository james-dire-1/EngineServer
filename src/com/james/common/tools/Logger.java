package com.james.common.tools;

import com.james.userInterfaces.Headless;
import com.james.userInterfaces.Window;
import templates.common.GlobalConstants;

// TODO: 2026-08-28 This is not identical to the game engine version of this class
public class Logger {

    public static void println(String message) {
        if (GlobalConstants.headless) {
            Headless.get().println(message);
        } else {
            Window.get().println(message);
        }
    }

}
