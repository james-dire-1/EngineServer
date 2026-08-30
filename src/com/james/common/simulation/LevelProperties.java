package com.james.common.simulation;

import org.lwjgl.util.vector.Vector3f;

/**
 * Important properties for the simulation of a Level. This class can be used both client-side and
 * server-side.
 */
public class LevelProperties {

    public float secondsPerGameTick = 0.05f;
    public Vector3f constantGravityVelocity = new Vector3f(0.0f, -4.0f, 0.0f);
    public float gravityAcceleration = -150.0f;
    public float terminalVelocity = -200.0f;

    public void setTicksPerSecond(int ticksPerSecond) {
        secondsPerGameTick = 1.0f / ticksPerSecond;
    }

}
