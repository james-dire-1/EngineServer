package com.james.serverSide.simulation.collisionEngine.hitboxes;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractAABBHitbox;
import com.james.serverSide.simulation.objects.PhysicalObject;

/**
 * AABB hitbox to be used server-side.
 */
public class AABBHitbox extends AbstractAABBHitbox {

    public AABBHitbox(PhysicalObject object, String meshPath) {
        super(object, meshPath);
    }

}
