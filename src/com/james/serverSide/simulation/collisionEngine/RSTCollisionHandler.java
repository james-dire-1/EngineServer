package com.james.serverSide.simulation.collisionEngine;

import com.james.serverSide.simulation.collisionEngine.hitboxes.SphereHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.common.simulation.collisionEngine.math.RSTCommonCollisionProcedure;

import java.util.ArrayList;
import java.util.List;

public class RSTCollisionHandler {

    public final List<SphereHitbox> sphereHitboxes = new ArrayList<>();

    public void updateSphereHitboxes() {
        for (SphereHitbox currentSphereHitbox : sphereHitboxes) {
            if (!(currentSphereHitbox.object instanceof MovableObject))
                continue;

            RSTCommonCollisionProcedure.beRepelledByOtherSpheresForThisSphere(currentSphereHitbox, sphereHitboxes);
        }
    }

}
