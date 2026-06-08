package com.james.serverSide;

import com.james.serverSide.simulation.Level;
import org.lwjgl.util.vector.Vector3f;

public interface Scene {

    void onStartup(Level level);

    String name();

    default Vector3f primarySpawnPoint() {
        return new Vector3f();
    }

}
