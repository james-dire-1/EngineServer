package com.james.common.simulation.collisionEngine.math.containers;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractSphereHitbox;
import org.lwjgl.util.vector.Vector3f;

public class RaySphereInfo {

    public boolean interestedInIntersectionPoint;
    public boolean interestedInCollidedHitbox;
    public Vector3f closestIntersectionPoint;
    public AbstractSphereHitbox closestCollidedHitbox;

    public RaySphereInfo(boolean interestedInIntersectionPoint, boolean interestedInCollidedHitbox) {
        this.interestedInIntersectionPoint = interestedInIntersectionPoint;
        this.interestedInCollidedHitbox = interestedInCollidedHitbox;
    }

    public RaySphereInfo() {
        this.interestedInIntersectionPoint = true;
        this.interestedInCollidedHitbox = true;
    }

}
