package game.main;

import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.common.tools.modelLoading.ModelLoader;
import com.james.serverSide.LevelInitializer;
import com.james.common.tools.Logger;
import com.james.serverSide.ServerThreadManager;
import com.james.serverSide.simulation.Level;
import com.james.tools.ThreadManager;
import com.james.userInterfaces.Headless;
import templates.common.GlobalConstants;
import templates.communication.OnlineServerPacketSendEvents;
import templates.communication.OnlineServerProperties;
import com.james.networking.Server;
import com.james.userInterfaces.Window;
import templates.serverSide.Scenes;

import java.io.IOException;

public class Main {

    public static volatile boolean everythingIsCompleted = false;
    public static volatile boolean shouldClose = false;

    private static final int PORT = 6789;
    private static LevelInitializer levelInitializer;

    public static void main(String[] args) {
        try {
            ModelLoader.init("/objects/abstract-art.dae", "/objects/one-sided-wall.dae", "/objects/stall.obj", "/scenes/beach-scene.dae", "/scenes/desert-scene.dae", "/scenes/plains-scene.dae", "/scenes/test-scene.dae");

            ModelMeshBankInR3.init("/scenes/beach-scene.dae", "/scenes/desert-scene.dae", "/scenes/plains-scene.dae", "/scenes/test-scene.dae");
            EllipsoidDimensions.init( new float[][]{ { 1, 1, 1 }, { 0.5f, 3, 0.5f } } );

            GlobalConstants.headless = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("headless")) {
                    GlobalConstants.headless = true;
                    break;
                }
            }

            if (GlobalConstants.headless) {
                new Headless();
            } else {
                new Window("Survival Game Server", 500, 300);
            }

            Logger.println("Survival game server");
            levelInitializer = new LevelInitializer(new OnlineServerPacketSendEvents(), Scenes.beachScene);
            Logger.println("Level initialized");

            try {
                Server server = new Server(PORT);
                server.setConnectedClientAddedListener(OnlineServerProperties::clientJoined);
                Logger.println("Server successfully set up on port " + PORT);
            } catch (IOException e) {
                Logger.println("Server was unable to start with the following error:");
                Logger.println(e.toString());
                shutDownServer();
                if (GlobalConstants.headless) Headless.get().close();
                return;
            }

            while (!shouldClose) {
                Thread.sleep(100);
                ThreadManager.updateMain();

                if (shouldClose) {
                    shutDownServer();
                    Logger.println("Server successfully closed");

                    if (GlobalConstants.headless) {
                        Headless.get().close();
                    } else {
                        if (Window.get().requestedClose) {
                            Window.get().dispose();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.println(e.toString());
        }
    }

    private static void shutDownServer() {
        try {
            Server.get().disconnect();
        } catch (NullPointerException | IOException e) {
            if (!(e instanceof NullPointerException)) {
                e.printStackTrace();
            }
        }

        levelInitializer.shouldRun = false;

        try {
            levelInitializer.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Level.clearNameToLevelMap();
        ServerThreadManager.clearEverything();
        everythingIsCompleted = true;

        if (GlobalConstants.headless) {
            Headless.get().makeNonEditable();
        } else {
            Window.get().makeNonEditable();
        }
    }

}
