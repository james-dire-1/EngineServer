package com.james.serverSide.simulation.collisionEngine;

import com.james.common.simulation.collisionEngine.math.CommonCollisionProcedure;
import com.james.common.simulation.LevelProperties;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.collisionEngine.hitboxes.EllipsoidHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Collision handler to be used server-side.
 */
public class CollisionHandler {

    public final List<EllipsoidHitbox> ellipsoidHitboxes = new ArrayList<>();
    public final List<AABBHitbox> aabbHitboxes = new ArrayList<>();

    private final Level level;

    private final Vector3f prevPosition = new Vector3f();

    public CollisionHandler(Level level) {
        this.level = level;
    }

    /**
     * Updates collision logic.
     *
     * @implNote if the narrow collision test was not performed (i.e. the EllipsoidHitbox did not collide
     * with any AABBs), then the regular update() method of the MovableObject is called.
     */
    public void update() {
        for (EllipsoidHitbox ellipsoidHitbox : ellipsoidHitboxes) {
            MovableObject movableObject = ellipsoidHitbox.movableObject;

            if (movableObject.isAffectedByAABBCollisions) {
                prevPosition.set(movableObject.getPosition());

                boolean algorithmPerformed = CommonCollisionProcedure.performEntireCollisionDetectionAlgorithm(ellipsoidHitbox, aabbHitboxes, level, null);
                if (!algorithmPerformed) {
                    movableObject.moveUpdate();
                }

                Vector3f newPosition = movableObject.getPosition();
                if (!newPosition.equals(prevPosition)) {
                    level.events.sendPhysicalObjectMoved(movableObject.id, newPosition.x, newPosition.y, newPosition.z);
                }
            }
        }
    }

}
