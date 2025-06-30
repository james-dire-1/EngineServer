package game;

import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.common.tools.ModelLoader;
import com.james.serverSide.LevelInitializer;
import newStuff.OnlineServerPacketSendEvents;
import newStuff.OnlineServerProperties;
import newStuff.Server;
import newStuff.Window;

import java.io.IOException;

/*
This is for testing!
 */

public class Main {

    private static final int PORT = 6789;

    public static void main(String[] args) {
        ModelLoader.init("res/stall.obj", "res/abstract_art.dae", "res/one-sided-wall5.dae", "res/test_environment_7.dae");

        ModelMeshBankInR3.init("res/one-sided-wall5.dae", "res/test_environment_7.dae");
        EllipsoidDimensions.init( new float[][]{ { 1, 1, 1 }, { 0.5f, 3, 0.5f } } );

        Window window = new Window("Game Server");
        window.println("Game server version 1");

        try {

            try {
                Server server = new Server(PORT);
                server.setConnectedClientAddedListener(OnlineServerProperties::onConnectedClientAdded);

                window.println("Server successfully set up on port " + PORT);

                // TODO: 2024-07-11 Problems will happen if a wait is placed here, should a client join during that time
                new LevelInitializer("main", new OnlineServerPacketSendEvents());
            } catch (IOException e) {
                window.println("Server was unable to start with the following error:");
                window.println(e.toString());
            }

        } catch (Exception e) {
            window.println(e.toString());
        }
    }

}
