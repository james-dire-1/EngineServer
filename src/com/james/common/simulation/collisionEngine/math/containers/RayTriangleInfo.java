package com.james.common.simulation.collisionEngine.math.containers;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractAABBHitbox;
import org.lwjgl.util.vector.Vector3f;

public class RayTriangleInfo {

    public boolean interestedInIntersectionPoint;
    public boolean interestedInCollidedHitbox;
    public Vector3f closestIntersectionPoint;
    public AbstractAABBHitbox closestCollidedHitbox;

    public RayTriangleInfo(boolean interestedInIntersectionPoint, boolean interestedInCollidedHitbox) {
        this.interestedInIntersectionPoint = interestedInIntersectionPoint;
        this.interestedInCollidedHitbox = interestedInCollidedHitbox;
    }

    public RayTriangleInfo() {
        this.interestedInIntersectionPoint = true;
        this.interestedInCollidedHitbox = true;
    }

}
