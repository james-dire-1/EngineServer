package templates.commands;

import com.james.common.tools.Logger;
import game.main.Main;

public class CommandExecutor {

    public static void execute(String command) {
        Logger.println(">>> " + command);
        String feedbackMessage;

        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("end")) {
            Main.shouldClose = true;
            feedbackMessage = "Shutting down server...";
        } else {
            feedbackMessage = String.format("Invalid command `%s`", command);
        }

        Logger.println(feedbackMessage);
    }

}
