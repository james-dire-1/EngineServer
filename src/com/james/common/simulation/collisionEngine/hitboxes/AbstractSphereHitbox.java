package com.james.common.simulation.collisionEngine.hitboxes;

import com.james.common.simulation.objects.AbstractPhysicalObject;

public abstract class AbstractSphereHitbox {

    public static final float MAX_SPEED = 10.0f;

    public final AbstractPhysicalObject object;
    public final float radius;

    public boolean activeToRays = true;
    public boolean affectOtherSpheres = true;
    public boolean affectedByOtherSpheres = true;

    public AbstractSphereHitbox(AbstractPhysicalObject object, float radius) {
        this.object = object;
        this.radius = radius;
    }

}
