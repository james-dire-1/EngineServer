package com.james.common.simulation.collisionEngine.math;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractAABBHitbox;
import com.james.common.simulation.collisionEngine.hitboxes.AbstractEllipsoidHitbox;
import com.james.common.simulation.collisionEngine.hitboxes.Ray;
import com.james.common.simulation.collisionEngine.math.containers.RayTriangleInfo;
import com.james.common.tools.Mth;
import org.lwjgl.util.vector.Vector3f;

import java.util.Collection;

public class RayCollisionDefense {

    public static void backtrackMovableObjectIfNecessary(Vector3f prevPosition, AbstractEllipsoidHitbox ellipsoidHitbox, Collection<? extends AbstractAABBHitbox> aabbHitboxes) {
        Vector3f currentPosition = ellipsoidHitbox.movableObject.getPosition();
        Vector3f nonNormalizedDirection = Vector3f.sub(currentPosition, prevPosition, null);
        float pathLength = nonNormalizedDirection.length();
        Ray ray = new Ray(prevPosition, nonNormalizedDirection);
        RayTriangleInfo rayTriangleInfo = new RayTriangleInfo(true, false);

        RSTCommonCollisionProcedure.findClosestRayIntersectionWithTriangle(ray, -1.0f, pathLength, aabbHitboxes, rayTriangleInfo);

        if (rayTriangleInfo.closestIntersectionPoint == null)
            return;

        Vector3f ellipsoidRadius = ellipsoidHitbox.dimensions.radius;
        Vector3f intersectionPointInEllipsoidSpace = rayTriangleInfo.closestIntersectionPoint;
        PointOperations.dividePointByEllipsoidRadiusDest(intersectionPointInEllipsoidSpace, ellipsoidRadius);
        Vector3f normalizedDirectionInEllipsoidSpace = PointOperations.dividePointByEllipsoidRadius(ray.direction, ellipsoidRadius);
        Vector3f prevPositionInEllipsoidSpace = PointOperations.dividePointByEllipsoidRadius(prevPosition, ellipsoidRadius);

        float distance = Mth.distance(prevPositionInEllipsoidSpace, intersectionPointInEllipsoidSpace);
        float parameter = distance - 0.9f;
        Vector3f correctedDisplacementInEllipsoidSpace = Mth.multiply(normalizedDirectionInEllipsoidSpace, parameter);
        Vector3f correctedPositionInEllipsoidSpace = Vector3f.add(prevPosition, correctedDisplacementInEllipsoidSpace, null);
        PointOperations.multiplyPointByEllipsoidRadiusDest(correctedPositionInEllipsoidSpace, ellipsoidRadius);

        currentPosition.set(correctedPositionInEllipsoidSpace);
    }

}
