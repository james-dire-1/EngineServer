package com.james.serverSide.simulation.collisionEngine;

import com.james.common.simulation.collisionEngine.math.CommonCollisionProcedure;
import com.james.common.simulation.collisionEngine.math.containers.CollisionDetails;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.collisionEngine.hitboxes.EllipsoidHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.common.simulation.collisionEngine.math.RayCollisionDefense;
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

    public CollisionHandler(Level level) {
        this.level = level;
    }

    // TODO: 2026-08-14 outdated documentation
    /**
     * Updates collision logic.
     *
     * @implNote if the narrow collision test was not performed (i.e. the EllipsoidHitbox did not collide
     * with any AABBs), then the regular update() method of the MovableObject is called.
     */
    public void update() {
        for (EllipsoidHitbox ellipsoidHitbox : ellipsoidHitboxes) {
            MovableObject movableObject = ellipsoidHitbox.movableObject;

            if (movableObject.canCollideWithTriangles) {
                Vector3f gravityToUse;
                if (movableObject.getFallingAndGravityState() != null)
                    gravityToUse = movableObject.getFallingAndGravityState().getVelocityDueToGravity();
                else
                    gravityToUse = level.constantGravityVelocity;

                CollisionDetails collisionDetails = movableObject.getCollisionDetails();
                if (collisionDetails != null) collisionDetails.reset();
                Vector3f prevPosition = new Vector3f(movableObject.getPosition());

                boolean algorithmPerformed = CommonCollisionProcedure.performEntireCollisionDetectionAlgorithm(ellipsoidHitbox, aabbHitboxes, gravityToUse, level, null, collisionDetails);
                if (!algorithmPerformed) {
                    movableObject.setPositionBasedOnVelocity();
                }

                if (ellipsoidHitbox.doBacktracking) {
                    RayCollisionDefense.backtrackMovableObjectIfNecessary(prevPosition, ellipsoidHitbox, aabbHitboxes);
                }
            }
        }
    }

}
