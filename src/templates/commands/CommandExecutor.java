package templates.commands;

import com.james.common.tools.Logger;
import com.james.serverSide.Scene;
import game.main.Main;
import templates.serverSide.Scenes;

public class CommandExecutor {

    public static void execute(String command) {
        Logger.println(">>> " + command);

        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("end")) {
            Main.shouldClose = true;
            Logger.println("Shutting down server...");
        } else if (Main.sceneToUse == null) {
            for (Scene scene : Scenes.allScenes) {
                if (command.equals(scene.name())) {
                    Main.sceneToUse = scene;
                    break;
                }
            }

            if (Main.sceneToUse == null) {
                Main.promptSceneSelect(command);
            }
        } else {
            Logger.println(String.format("Invalid command `%s`", command));
        }
    }

}
