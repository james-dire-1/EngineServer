package com.james.common.simulation.collisionEngine.hitboxes;

import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.serverSide.simulation.collisionEngine.CollisionHandler;
import com.james.serverSide.simulation.objects.MovableObject;

/**
 * An ellipsoid hitbox used by players and moving entities to navigate world geometry that is represented
 * by triangle meshes (and approximated by AABBs). In the CommonCollisionProcedure, these ellipsoid hitboxes
 * are tested against each AbstractAABBHitbox. If an AbstractAABBHitbox is deemed to be colliding with an
 * ellipsoid hitbox, then the broadphase check has passed and the narrow phase test can begin.
 */
public abstract class AbstractEllipsoidHitbox {

    public final MovableObject movableObject;
    public final EllipsoidDimensions dimensions;

    /**
     * Creates a new ellipsoid hitbox of a given dimensions for a specific object. Note that the object
     * being passed in has to be a MovableObject, instead of an AbstractPhysicalObject. This is because
     * ellipsoid hitboxes' objects need to have a velocity vector for the collision calculations. This
     * constructor also enables the hitbox.
     */
    protected AbstractEllipsoidHitbox(MovableObject movableObject, EllipsoidDimensions dimensions) {
        this.movableObject = movableObject;
        this.dimensions = dimensions;

        enable();
    }

    /**
     * Enables the hitbox. This field is checked in the collision handlers to see if the collision routine
     * must be performed.
     * See ClientCollisionHandler
     * @see CollisionHandler
     */
    public void enable() {
        this.movableObject.isAffectedByAABBCollisions = true;
    }

    /**
     * Disables the hitbox. This field is checked in the collision handlers to see if the collision routine
     * must be performed.
     * See ClientCollisionHandler
     * @see CollisionHandler
     */
    public void disable() {
        this.movableObject.isAffectedByAABBCollisions = false;
    }

}
