package com.james.serverSide.simulation.objects;

import com.james.common.simulation.LevelProperties;
import com.james.common.tools.Mth;
import com.james.common.simulation.objects.PhysicalObjectType;
import org.lwjgl.util.vector.Vector3f;

/**
 * An extension of PhysicalObject that allows for velocity and acceleration values, which get applied in the
 * overloaded update() method every game tick.
 */
public class MovableObject extends PhysicalObject {

    private final LevelProperties levelProperties;

    private final Vector3f velocity = new Vector3f(0, 0, 0);
    private final Vector3f acceleration = new Vector3f(0, 0, 0);

    public Vector3f getVelocity() { return velocity; }
    public Vector3f getAcceleration() { return acceleration; }

    public boolean isAffectedByGravity = true;
    public boolean isAffectedByAABBCollisions = false;
    private boolean usesAcceleration = false;

    public MovableObject(LevelProperties levelProperties, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale) {
        super(type, position, rotation, scale);
        this.levelProperties = levelProperties;
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.x = x;
        this.velocity.y = y;
        this.velocity.z = z;
    }

    public void setAcceleration(float x, float y, float z) {
        usesAcceleration = true;

        this.acceleration.x = x;
        this.acceleration.y = y;
        this.acceleration.z = z;
    }

    public void moveUpdate() {
        if (usesAcceleration) {
            Vector3f.add(velocity, Mth.multiply(this.acceleration, levelProperties.secondsPerGameTick), velocity);
        }

        Vector3f netVelocity;
        if (isAffectedByGravity) {
            netVelocity = Vector3f.add(velocity, levelProperties.gravity, null);
        } else {
            netVelocity = velocity;
        }

        Vector3f.add(position, Mth.multiply(netVelocity, levelProperties.secondsPerGameTick), position);
    }

}
