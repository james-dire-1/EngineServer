package game.main;

import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.common.tools.modelLoading.ModelLoader;
import com.james.serverSide.LevelInitializer;
import com.james.tools.Logger;
import templates.common.GlobalConstants;
import templates.communication.OnlineServerPacketSendEvents;
import templates.communication.OnlineServerProperties;
import com.james.networking.Server;
import com.james.window.Window;
import templates.serverSide.Scenes;

import java.io.IOException;

public class Main {

    private static final int PORT = 6789;

    public static void main(String[] args) {
        try {
            ModelLoader.init("/one-sided-wall.dae", "/test-environment.dae", "/desert-2.dae",
                    "/beach-scene.dae");

            ModelMeshBankInR3.init("/one-sided-wall.dae", "/test-environment.dae",
                    "/desert-2.dae", "/beach-scene.dae");
            EllipsoidDimensions.init( new float[][]{ { 1, 1, 1 }, { 0.5f, 3, 0.5f } } );

            GlobalConstants.headless = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("headless")) {
                    GlobalConstants.headless = true;
                    break;
                }
            }

            if (!GlobalConstants.headless) {
                new Window("Game Server");
            }

            Logger.log("Game server version 1");

            try {
                Server server = new Server(PORT);
                server.setConnectedClientAddedListener(OnlineServerProperties::clientJoined);

                Logger.log("Server successfully set up on port " + PORT);

                // TODO: 2024-07-11 Problems will happen if a wait is placed here, should a client join during that time
                new LevelInitializer("main", new OnlineServerPacketSendEvents(), Scenes::beachScene);
            } catch (IOException e) {
                Logger.log("Server was unable to start with the following error:");
                Logger.log(e.toString());
            }
        } catch (Exception e) {
            Logger.log(e.toString());
        }
    }

}
