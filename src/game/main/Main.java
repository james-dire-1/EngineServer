package game.main;

import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.common.tools.modelLoading.ModelLoader;
import com.james.serverSide.LevelInitializer;
import com.james.common.tools.Logger;
import com.james.serverSide.Scene;
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

    public static Scene sceneToUse;

    private static final int PORT = 6789;
    private static LevelInitializer levelInitializer;

    public static void main(String[] args) {
        try {
            ModelLoader.init("/objects/abstract-art.dae", "/objects/one-sided-wall.dae", "/objects/stall.obj", "/scenes/beach-scene.dae", "/scenes/desert-scene.dae", "/scenes/plains-scene.dae", "/scenes/test-scene.dae");

            ModelMeshBankInR3.init("/scenes/beach-scene.dae", "/scenes/desert-scene.dae", "/scenes/plains-scene.dae", "/scenes/test-scene.dae");
            EllipsoidDimensions.init( new float[][]{ { 1, 1, 1 }, { 0.5f, 3, 0.5f } } );

            GlobalConstants.headless = false;

            if (GlobalConstants.IS_QUICK_START) {
                sceneToUse = GlobalConstants.QUICK_START_SCENE;
            }

            for (String arg : args) {
                if (!GlobalConstants.headless) {
                    if (arg.equalsIgnoreCase("headless")) {
                        GlobalConstants.headless = true;
                        continue;
                    }
                }

                if (sceneToUse == null) {
                    for (Scene scene : Scenes.allScenes) {
                        if (arg.equals(scene.name())) {
                            sceneToUse = scene;
                            break;
                        }
                    }
                }
            }

            if (GlobalConstants.headless) {
                new Headless();
            } else {
                new Window("Survival Game Server", 500, 300);
            }

            Logger.println("Survival game server");
            Logger.println("Enter `stop` to terminate the server");

            if (sceneToUse == null) {
                promptSceneSelect(null);

                while (sceneToUse == null) {
                    Thread.sleep(100);
                    ThreadManager.updateMain();

                    if (shouldClose) {
                        shutDownServerPrematurely();
                        Logger.println("Server successfully closed");

                        if (GlobalConstants.headless) {
                            Headless.get().close();
                        } else {
                            if (Window.get().requestedClose) {
                                Window.get().dispose();
                            }
                        }

                        return;
                    }
                }
            }

            Logger.println(String.format("Scene `%s` selected", sceneToUse.name()));
            levelInitializer = new LevelInitializer(new OnlineServerPacketSendEvents(), sceneToUse);
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

    private static void shutDownServerPrematurely() {
        everythingIsCompleted = true;

        if (GlobalConstants.headless) {
            Headless.get().makeNonEditable();
        } else {
            Window.get().makeNonEditable();
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

    private static StringBuilder builder;

    public static void promptSceneSelect(String invalidSceneName) {
        if (builder == null) {
            builder = new StringBuilder(200);
            builder.append("Please enter one of ");

            for (int i = 0; i < Scenes.allScenes.length; i++) {
                Scene scene = Scenes.allScenes[i];
                builder.append(String.format("`%s`", scene.name()));

                if (i != Scenes.allScenes.length - 1) {
                    builder.append(", ");
                }
            }
        }

        String firstMessage;
        if (invalidSceneName == null) {
            firstMessage = "A scene has not been selected";
        } else {
            firstMessage = String.format("`%s` is not a valid scene", invalidSceneName);
        }

        Logger.println(firstMessage);
        Logger.println(builder.toString());
    }

}
