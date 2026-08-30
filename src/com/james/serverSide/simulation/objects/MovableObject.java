package com.james.serverSide.simulation.objects;

import com.james.common.simulation.LevelProperties;
import com.james.common.simulation.collisionEngine.math.containers.CollisionDetails;
import com.james.common.tools.Mth;
import com.james.common.simulation.details.FallingAndGravityProperties;
import com.james.common.simulation.details.FallingAndGravityState;
import templates.common.simulation.objects.PhysicalObjectType;
import org.lwjgl.util.vector.Vector3f;

// TODO: 2026-08-16 some more outdated documentation
/**
 * An extension of PhysicalObject that allows for velocity and acceleration values, which get applied in the
 * overloaded update() method every game tick.
 */
public class MovableObject extends PhysicalObject implements MoveUpdatable {

    private final LevelProperties levelProperties;

    private final Vector3f velocity = new Vector3f();
    private final Vector3f secondaryVelocity = new Vector3f();
    private final Vector3f acceleration = new Vector3f();

    private final Vector3f tempCombinedVelocity = new Vector3f();
    private final Vector3f tempNetVelocity = new Vector3f();

    public Vector3f getVelocity() { return velocity; }
    public Vector3f getAcceleration() { return acceleration; }

    public Vector3f getCombinedVelocity() {
        Vector3f.add(velocity, secondaryVelocity, tempCombinedVelocity);
        return tempCombinedVelocity;
    }

    public boolean isAffectedByGravity = true;
    // TODO: 2026-08-14 make more restricted to signify that this should not be modified directly
    public boolean canCollideWithTriangles = false;
    private boolean usesAcceleration = false;

    private CollisionDetails collisionDetails;
    private FallingAndGravityState fgState;

    public CollisionDetails getCollisionDetails() { return collisionDetails; }
    public FallingAndGravityState getFallingAndGravityState() { return fgState; }

    public MovableObject(LevelProperties levelProperties, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale) {
        super(type, position, rotation, scale);
        this.levelProperties = levelProperties;
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.x = x;
        this.velocity.y = y;
        this.velocity.z = z;
    }

    public void setSecondaryVelocity(float x, float y, float z) {
        this.secondaryVelocity.x = x;
        this.secondaryVelocity.y = y;
        this.secondaryVelocity.z = z;
    }

    public void setAcceleration(float x, float y, float z) {
        usesAcceleration = true;

        this.acceleration.x = x;
        this.acceleration.y = y;
        this.acceleration.z = z;
    }

    public void addFallingAndGravityStateWithDefaultProperties() {
        addFallingAndGravityStateWithGivenProperties(defaultFgProperties);
    }

    public void addFallingAndGravityStateWithGivenProperties(FallingAndGravityProperties fgProperties) {
        if (this.collisionDetails == null) {
            this.collisionDetails = new CollisionDetails();
        }

        this.fgState = new FallingAndGravityState(fgProperties, this);
    }

    @Override
    public void moveUpdate() {
        if (usesAcceleration) {
            Vector3f.add(velocity, Mth.multiply(this.acceleration, levelProperties.secondsPerGameTick), velocity);
        }
    }

    public void setPositionBasedOnVelocity() {
        tempNetVelocity.set(velocity);
        Vector3f.add(tempNetVelocity, secondaryVelocity, tempNetVelocity);

        if (isAffectedByGravity) {
            Vector3f gravityToUse;
            if (fgState != null)
                gravityToUse = fgState.getVelocityDueToGravity();
            else
                gravityToUse = levelProperties.constantGravityVelocity;

            Vector3f.add(tempNetVelocity, gravityToUse, tempNetVelocity);
        }

        Vector3f.add(position, Mth.multiply(tempNetVelocity, levelProperties.secondsPerGameTick), position);
    }

    private static final FallingAndGravityProperties defaultFgProperties = new FallingAndGravityProperties();

}
