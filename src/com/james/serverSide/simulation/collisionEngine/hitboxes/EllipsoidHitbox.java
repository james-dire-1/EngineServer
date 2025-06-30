package com.james.serverSide.simulation.collisionEngine.hitboxes;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractEllipsoidHitbox;
import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.serverSide.simulation.objects.MovableObject;

/**
 * Ellipsoid hitbox to be used server-side.
 */
public class EllipsoidHitbox extends AbstractEllipsoidHitbox {

    public EllipsoidHitbox(MovableObject movableObject, EllipsoidDimensions dimensions) {
        super(movableObject, dimensions);
    }

}
