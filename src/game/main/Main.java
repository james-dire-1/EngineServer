package game.main;

import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.common.tools.modelLoading.ModelLoader;
import com.james.serverSide.LevelInitializer;
import templates.communication.OnlineServerPacketSendEvents;
import templates.communication.OnlineServerProperties;
import com.james.networking.Server;
import com.james.window.Window;
import templates.serverSide.Scenes;

import java.io.IOException;

public class Main {

    private static final int PORT = 6789;

    public static void main(String[] args) {
        ModelLoader.init("/one-sided-wall.dae", "/test-environment.dae", "/desert-2.dae",
                "/beach-scene.dae");

        ModelMeshBankInR3.init("/one-sided-wall.dae", "/test-environment.dae",
                "/desert-2.dae", "/beach-scene.dae");
        EllipsoidDimensions.init( new float[][]{ { 1, 1, 1 }, { 0.5f, 3, 0.5f } } );

        Window window = new Window("Game Server");
        window.println("Game server version 1");

        try {

            try {
                Server server = new Server(PORT);
                server.setConnectedClientAddedListener(OnlineServerProperties::clientJoined);

                window.println("Server successfully set up on port " + PORT);

                // TODO: 2024-07-11 Problems will happen if a wait is placed here, should a client join during that time
                new LevelInitializer("main", new OnlineServerPacketSendEvents(), Scenes::beachScene);
            } catch (IOException e) {
                window.println("Server was unable to start with the following error:");
                window.println(e.toString());
            }

        } catch (Exception e) {
            window.println(e.toString());
        }
    }

}
