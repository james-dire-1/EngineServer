package com.james.common.simulation.collisionEngine.math;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractAABBHitbox;
import com.james.common.simulation.collisionEngine.hitboxes.AbstractSphereHitbox;
import com.james.common.simulation.collisionEngine.hitboxes.Ray;
import com.james.common.simulation.collisionEngine.math.objects.Plane;
import com.james.common.tools.Mth;
import org.lwjgl.util.vector.Vector3f;

public class RSTCollisionMath {

    // The algorithm was obtained from this video by The Art of Code:
    // https://www.youtube.com/watch?v=HFPlKQGChpE
    public static boolean rayAndSphereIntersection(Ray ray, AbstractSphereHitbox sphere, Vector3f intersectionPoint) {
        Vector3f rayOrigin = ray.origin;
        Vector3f rayDirection = ray.direction;
        Vector3f sphereCenter = sphere.object.getPosition();
        float radiusSquared = sphere.radius * sphere.radius;

        Vector3f rayOriginToSphereCenter = Vector3f.sub(sphereCenter, rayOrigin, null);
        float parameterForClosestPoint = Vector3f.dot(rayOriginToSphereCenter, rayDirection);

        if (parameterForClosestPoint < 0.0f) {
            return false;
        }

        Vector3f closestPoint = Vector3f.add(rayOrigin, Mth.multiply(rayDirection, parameterForClosestPoint), null);
        float distanceSquared = Mth.squaredDistance(sphereCenter, closestPoint);

        if (distanceSquared > radiusSquared) {
            return false;
        }

        if (intersectionPoint != null) {
            float parameterDelta = (float) Math.sqrt(radiusSquared - distanceSquared);
            float parameterForIntersectionPoint = parameterForClosestPoint - parameterDelta;

            if (parameterForIntersectionPoint < 0.0f) {
                return false;
            }

            Vector3f.add(rayOrigin, Mth.multiply(rayDirection, parameterForIntersectionPoint), intersectionPoint);
        }

        return true;
    }

    // The algorithm and code were obtained from these videos by Jorge Rodriguez:
    // https://www.youtube.com/watch?v=USjbg5QXk3g
    // https://www.youtube.com/watch?v=3vONlLYtHUE
    public static boolean rayAndAABBIntersection(Vector3f rayStart, Vector3f rayEnd, AbstractAABBHitbox aabb) {
        ClipLineInfo clipLineInfo = new ClipLineInfo();

        boolean possiblyInAABB = clipLine(rayStart.x, rayEnd.x, aabb.lowerX, aabb.upperX, clipLineInfo);
        if (!possiblyInAABB) return false;

        possiblyInAABB = clipLine(rayStart.y, rayEnd.y, aabb.lowerY, aabb.upperY, clipLineInfo);
        if (!possiblyInAABB) return false;

        return clipLine(rayStart.z, rayEnd.z, aabb.lowerZ, aabb.upperZ, clipLineInfo);
    }

    // The algorithm and code were obtained from these videos by Jorge Rodriguez:
    // https://www.youtube.com/watch?v=USjbg5QXk3g
    // https://www.youtube.com/watch?v=3vONlLYtHUE
    private static boolean clipLine(float rayStartDim, float rayEndDim, float aabbLowerDim, float aabbUpperDim, ClipLineInfo clipLineInfo) {
        float fLowerDim = (aabbLowerDim - rayStartDim) / (rayEndDim - rayStartDim);
        float fUpperDim = (aabbUpperDim - rayStartDim) / (rayEndDim - rayStartDim);

        if (fLowerDim > fUpperDim) {
            float temporary = fLowerDim;
            fLowerDim = fUpperDim;
            fUpperDim = temporary;
        }

        if (fUpperDim < clipLineInfo.fLower || fLowerDim > clipLineInfo.fUpper) {
            return false;
        }

        clipLineInfo.fLower = Math.max(clipLineInfo.fLower, fLowerDim);
        clipLineInfo.fUpper = Math.min(clipLineInfo.fUpper, fUpperDim);

        return clipLineInfo.fLower <= clipLineInfo.fUpper;
    }

    // The algorithm was obtained from this video by Jorge Rodriguez:
    // https://www.youtube.com/watch?v=fIu_8b2n8ZM
    public static Vector3f rayAndPlaneIntersection(Ray ray, Plane plane) {
        Vector3f x0 = ray.origin;
        Vector3f v = ray.direction;
        Vector3f n = plane.normal;
        Vector3f w = Vector3f.sub(plane.origin, ray.origin, null);

        float numerator = Vector3f.dot(w, n);
        float denominator = Vector3f.dot(v, n);

        if (Math.abs(denominator) < 0.000001f) {
            return null;
        }

        float parameter = numerator / denominator;

        if (parameter < 0) {
            return null;
        }

        Vector3f term = Mth.multiply(v, parameter);
        return Vector3f.add(x0, term, null);
    }

    private static class ClipLineInfo {
        private float fLower = 0;
        private float fUpper = 1;
    }

}
