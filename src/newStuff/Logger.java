package newStuff;

import templates.common.GlobalConstants;

public class Logger {

    public static void log(String message) {
        if (GlobalConstants.printingMode == GlobalConstants.PrintingMode.SYSOUT) {
            System.out.println(message);
        } else if (GlobalConstants.printingMode == GlobalConstants.PrintingMode.WINDOW) {
            Window.get().println(message);
        }
    }

}
