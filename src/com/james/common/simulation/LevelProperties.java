package com.james.common.simulation;

import org.lwjgl.util.vector.Vector3f;

/**
 * Important properties for the simulation of a Level. This class can be used both client-side and
 * server-side.
 */
public class LevelProperties {

    public float secondsPerGameTick = 0.05f;
    public Vector3f gravity = new Vector3f(0, -4f, 0);

    public void setTicksPerSecond(int ticksPerSecond) {
        secondsPerGameTick = 1.0f / ticksPerSecond;
    }

}
