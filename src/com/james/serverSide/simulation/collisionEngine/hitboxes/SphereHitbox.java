package com.james.serverSide.simulation.collisionEngine.hitboxes;

import com.james.serverSide.simulation.objects.PhysicalObject;
import com.james.common.simulation.collisionEngine.hitboxes.AbstractSphereHitbox;

public class SphereHitbox extends AbstractSphereHitbox {

    public SphereHitbox(PhysicalObject object, float radius) {
        super(object, radius);
    }

}
