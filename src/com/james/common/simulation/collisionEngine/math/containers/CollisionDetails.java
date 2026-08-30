package com.james.common.simulation.collisionEngine.math.containers;

public class CollisionDetails {

    public boolean onGround = false;
    public float inclinationAngle = -1.0f;

    public void reset() {
        onGround = false;
        inclinationAngle = -1.0f;
    }

}
