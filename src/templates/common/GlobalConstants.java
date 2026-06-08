package templates.common;

import com.james.serverSide.Scene;
import templates.serverSide.Scenes;

// TODO: 2025-07-01 This is not identical to the game engine version of this class
public class GlobalConstants {

    public static final boolean IS_NETWORK_DEBUG = false;
    public static final boolean IS_DETAILED_NETWORK_DEBUG = false;
    public static final boolean IS_QUICK_START = false;
    public static final Scene QUICK_START_SCENE = Scenes.beachScene;

    public static final String MODELS_BASE_DIRECTORY = "res/models";

    public static boolean headless;

}
