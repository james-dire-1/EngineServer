package com.james.common.simulation.collisionEngine.math;

import com.james.common.simulation.LevelProperties;
import com.james.common.simulation.collisionEngine.math.objects.Plane;
import com.james.common.simulation.collisionEngine.math.objects.Triangle;
import com.james.common.simulation.collisionEngine.prep.ModelMesh;
import com.james.common.simulation.collisionEngine.hitboxes.AbstractAABBHitbox;
import com.james.common.simulation.collisionEngine.hitboxes.AbstractEllipsoidHitbox;
import com.james.common.tools.Mth;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.common.simulation.collisionEngine.math.containers.CollisionDetails;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// Note: most of the contents of this class are implemented from Fauerby's report:
// http://www.peroxide.dk/papers/collision/collision.pdf
public class CommonCollisionProcedure {

    private static final float VERY_CLOSE_DISTANCE = 0.005f;
    private static final float VERY_CLOSE_DISTANCE_SQUARED = VERY_CLOSE_DISTANCE * VERY_CLOSE_DISTANCE;
    private static final int MAX_RECURSION_COUNT = 5;
    private static final Vector3f UNIT_UP_VECTOR = new Vector3f(0.0f, 1.0f, 0.0f);

    /**
     * Does broadphase collision test between AbstractEllipsoidHitbox and all AbstractAABBHitboxes. Determines
     * which AABBs are colliding, and from this info, only relevant Triangles in ellipsoid world space are
     * tested against in the narrow phase collision test. Does the whole collision and response routine for
     * both the MovableObject's velocity vector and gravity vector. Sets the final position of the MovableObject.
     *
     * @return whether the narrow phase test was performed
     */
    public static boolean performEntireCollisionDetectionAlgorithm(AbstractEllipsoidHitbox ellipsoidHitbox, Collection<? extends AbstractAABBHitbox> aabbHitboxes, Vector3f gravityVelocity, LevelProperties levelProperties, DebugAccumulator debugAccumulator, CollisionDetails collisionDetails) {
        List<Triangle> allTrianglesInEllipsoidWorldSpaceList = new ArrayList<>();

        for (AbstractAABBHitbox aabbHitbox : aabbHitboxes) {
            if (!aabbHitbox.activeToEllipsoids)
                continue;

            Vector3f ellipsoidPosition = ellipsoidHitbox.movableObject.getPosition();
            Vector3f ellipsoidRadius = ellipsoidHitbox.dimensions.radius;
            float ellipsoidLowerX = ellipsoidPosition.x - ellipsoidRadius.x;
            float ellipsoidUpperX = ellipsoidPosition.x + ellipsoidRadius.x;
            float ellipsoidLowerY = ellipsoidPosition.y - ellipsoidRadius.y;
            float ellipsoidUpperY = ellipsoidPosition.y + ellipsoidRadius.y;
            float ellipsoidLowerZ = ellipsoidPosition.z - ellipsoidRadius.z;
            float ellipsoidUpperZ = ellipsoidPosition.z + ellipsoidRadius.z;

            if (    (aabbHitbox.upperX >= ellipsoidLowerX && ellipsoidUpperX >= aabbHitbox.lowerX) &&
                    (aabbHitbox.upperY >= ellipsoidLowerY && ellipsoidUpperY >= aabbHitbox.lowerY) &&
                    (aabbHitbox.upperZ >= ellipsoidLowerZ && ellipsoidUpperZ >= aabbHitbox.lowerZ)  ) {
                Vector3f objectPosition = aabbHitbox.object.getPosition();
                ModelMesh meshInEllipsoidLocalSpace = ellipsoidHitbox.dimensions.modelMeshMap.get(aabbHitbox.meshPath).get(aabbHitbox.subMeshIdentifier);
                Triangle[] trianglesInEllipsoidLocalSpace = meshInEllipsoidLocalSpace.triangles;
                Triangle[] trianglesInEllipsoidWorldSpace = ModelMesh.performOperationOnAllTriangles(trianglesInEllipsoidLocalSpace, PointOperations::addObjectPositionToPoint, objectPosition);
                allTrianglesInEllipsoidWorldSpaceList.addAll(Arrays.asList(trianglesInEllipsoidWorldSpace));
                if (debugAccumulator != null) debugAccumulator.accumulator++;
            }
        }

        if (debugAccumulator != null) {
            debugAccumulator.collisions = debugAccumulator.accumulator;
            debugAccumulator.accumulator = 0;
        }

        Triangle[] allTrianglesInEllipsoidWorldSpace = allTrianglesInEllipsoidWorldSpaceList.toArray(new Triangle[0]);
        if (allTrianglesInEllipsoidWorldSpace.length == 0) {
            return false;
        }

        Vector3f ellipsoidHitboxRadius = ellipsoidHitbox.dimensions.radius;
        MovableObject movableObject = ellipsoidHitbox.movableObject;

        // Velocity (AKA displacementThisTick) vector is the MovableObject's velocity
        Vector3f basePoint = movableObject.getPosition();
        PointOperations.dividePointByEllipsoidRadiusDest(basePoint, ellipsoidHitboxRadius);

        Vector3f displacementThisTick = Mth.multiply(movableObject.getCombinedVelocity(), levelProperties.secondsPerGameTick);
        PointOperations.dividePointByEllipsoidRadiusDest(displacementThisTick, ellipsoidHitboxRadius);

        Vector3f finalPosition = collisionDetectionAndResponse(allTrianglesInEllipsoidWorldSpace, basePoint, displacementThisTick, 0, null);

        // Velocity (AKA displacementThisTick) vector is the gravity vector
        if (movableObject.isAffectedByGravity) {
            displacementThisTick = Mth.multiply(gravityVelocity, levelProperties.secondsPerGameTick);
            PointOperations.dividePointByEllipsoidRadiusDest(displacementThisTick, ellipsoidHitboxRadius);

            finalPosition = collisionDetectionAndResponse(allTrianglesInEllipsoidWorldSpace, finalPosition, displacementThisTick, 0, collisionDetails);
        }

        // Convert result back into R3
        PointOperations.multiplyPointByEllipsoidRadiusDest(finalPosition, ellipsoidHitboxRadius);
        movableObject.setPosition(finalPosition.x, finalPosition.y, finalPosition.z);

        return true;
    }

    /**
     * Recursive method that performs the entire collision detection and response algorithm for given position
     * and velocity vectors. In the collision step, checks for collision against all the Triangles that were
     * passed in, and finds the closest one. Important data from the collision step are stored in CollisionInfo.
     */
    private static Vector3f collisionDetectionAndResponse(Triangle[] trianglesInEllipsoidWorldSpace, Vector3f position, Vector3f velocity, int recursionCount, CollisionDetails collisionDetails) {
        if (recursionCount > MAX_RECURSION_COUNT)
            return position;

        CollisionInfo collisionInfo = new CollisionInfo();
        for (Triangle triangle : trianglesInEllipsoidWorldSpace) {
            performCollisionDetectionWithTriangle(position, velocity, triangle, collisionInfo);
        }

        Vector3f destinationPoint = Vector3f.add(position, velocity, null);
        if (!collisionInfo.foundCollision)
            return destinationPoint;

        if (collisionDetails != null) {
            Triangle triangle = collisionInfo.collisionTriangle;
            Vector3f triangleEdge1 = Vector3f.sub(triangle.points[1], triangle.points[0], null);
            Vector3f triangleEdge2 = Vector3f.sub(triangle.points[2], triangle.points[0], null);

            Vector3f triangleDirection = Vector3f.cross(triangleEdge1, triangleEdge2, null);
            triangleDirection.normalise(triangleDirection);
            double angle = Math.toDegrees(Math.acos(Vector3f.dot(triangleDirection, UNIT_UP_VECTOR)));

            collisionDetails.onGround = true;
            collisionDetails.inclinationAngle = (float) angle;
        }

        if (collisionInfo.intersectionDistance >= VERY_CLOSE_DISTANCE) {
            velocity.normalise(velocity);
            velocity.scale(collisionInfo.intersectionDistance - VERY_CLOSE_DISTANCE);
            Vector3f.add(position, velocity, position);
            velocity.normalise(velocity);
            Vector3f.sub(collisionInfo.intersectionPoint, Mth.multiply(velocity, VERY_CLOSE_DISTANCE), collisionInfo.intersectionPoint);
        }

        Vector3f slidingPlaneOrigin = collisionInfo.intersectionPoint;
        Vector3f slidingPlaneNormal = Vector3f.sub(position, collisionInfo.intersectionPoint, null);
        slidingPlaneNormal.normalise(slidingPlaneNormal);
        Plane slidingPlane = new Plane(slidingPlaneOrigin, slidingPlaneNormal);

        Vector3f newDestinationPoint = Vector3f.sub(destinationPoint, Mth.multiply(slidingPlaneNormal, (float)slidingPlane.signedDistanceTo(destinationPoint)), null);
        Vector3f newVelocityVector = Vector3f.sub(newDestinationPoint, collisionInfo.intersectionPoint, null);

        if (newVelocityVector.lengthSquared() < VERY_CLOSE_DISTANCE_SQUARED)
            return position;

        recursionCount++;

        return collisionDetectionAndResponse(trianglesInEllipsoidWorldSpace, position, newVelocityVector, recursionCount, collisionDetails);
    }

    private static final ThreadLocal<TempCollisionInfo> temp = ThreadLocal.withInitial(TempCollisionInfo::new);

    /**
     * Checks for collision against a single Triangle. First, we see if a collision with the triangle plane is
     * possible. Then, tests if a collision with the triangle face has occurred. If this has not happened, then
     * tests if a collision with the triangle's vertices or edges has occurred. If this is the closest triangle
     * thus far, then its collision data overwrite old data in CollisionInfo.
     */
    private static void performCollisionDetectionWithTriangle(Vector3f basePoint, Vector3f velocity, Triangle triangle, CollisionInfo collisionInfo) {
        // Update: Apparently I don't have to change the triangle order, for some reason..
        Vector3f p1 = triangle.points[0];
        Vector3f p2 = triangle.points[1];
        Vector3f p3 = triangle.points[2];

        Vector3f normalizedVelocity = velocity.normalise(null);
        Plane trianglePlane = new Plane(p1, p2, p3);

        if (!trianglePlane.isFrontFacingTo(normalizedVelocity)) return;

        double t0, t1;
        boolean embeddedInPlane = false;

        double signedDistanceToTrianglePlane = trianglePlane.signedDistanceTo(basePoint);

        float normalDotVelocity = Vector3f.dot(trianglePlane.normal, velocity);
        if (normalDotVelocity == 0.0f) {
            if (Math.abs(signedDistanceToTrianglePlane) >= 1.0f) return;

            embeddedInPlane = true;
            t0 = 0.0;
        } else {
            t0 = (-1.0 - signedDistanceToTrianglePlane) / normalDotVelocity;
            t1 = ( 1.0 - signedDistanceToTrianglePlane) / normalDotVelocity;

            if (t0 > t1) {
                double temp = t1;
                t1 = t0;
                t0 = temp;
            }

            if (t0 > 1.0f || t1 < 0.0f) return;

            if (t0 < 0.0) t0 = 0.0;
        }

        temp.get().foundCollision = false;
        temp.get().t = 1.0;
        temp.get().intersectionPoint = null;

        if (!embeddedInPlane) {
            Vector3f planeIntersectionPoint = Vector3f.add(Vector3f.sub(basePoint, trianglePlane.normal, null), Mth.multiply(velocity, (float)t0), null);

            if (CollisionMath.pointInTriangle(planeIntersectionPoint, p1, p2, p3)) {
                temp.get().foundCollision = true;
                temp.get().t = t0;
                temp.get().intersectionPoint = planeIntersectionPoint;
            }
        }

        if (!temp.get().foundCollision) {
            float velocityLengthSquared = velocity.lengthSquared();

            testAgainstVertex(velocityLengthSquared, basePoint, velocity, p1);
            testAgainstVertex(velocityLengthSquared, basePoint, velocity, p2);
            testAgainstVertex(velocityLengthSquared, basePoint, velocity, p3);

            testAgainstEdge(velocityLengthSquared, basePoint, velocity, p2, p1);
            testAgainstEdge(velocityLengthSquared, basePoint, velocity, p3, p2);
            testAgainstEdge(velocityLengthSquared, basePoint, velocity, p1, p3);
        }

        if (temp.get().foundCollision) {
            float intersectionDistance = (float)temp.get().t * velocity.length();

            if (!collisionInfo.foundCollision || intersectionDistance < collisionInfo.intersectionDistance) {
                collisionInfo.foundCollision = true;
                collisionInfo.intersectionDistance = intersectionDistance;
                collisionInfo.intersectionPoint = temp.get().intersectionPoint;
                collisionInfo.collisionTriangle = triangle;
            }
        }
    }

    /**
     * Tests against one vertex by solving a quadratic.
     */
    private static void testAgainstVertex(float a, Vector3f basePoint, Vector3f velocity, Vector3f vertex) {
        float b = 2.0f * Vector3f.dot(velocity, Vector3f.sub(basePoint, vertex, null));
        float c = Vector3f.sub(vertex, basePoint, null).lengthSquared() - 1.0f;
        Float x1 = CollisionMath.getLowestRootUnderThreshold(a, b, c, (float)temp.get().t);
        if (x1 != null) {
            temp.get().foundCollision = true;
            temp.get().t = x1;
            temp.get().intersectionPoint = vertex;
        }
    }

    /**
     * Tests against one edge by solving a quadratic. If a solution was found, then that means that collision
     * with the line has occurred, but that does not necessarily mean that collision with the line segment
     * making up the edge has occurred. Thus, a value f is calculated, and if it is between 0 and 1, then we
     * know that collision with the edge has occurred.
     */
    private static void testAgainstEdge(float velocityLengthSquared, Vector3f basePoint, Vector3f velocity, Vector3f left, Vector3f right) {
        Vector3f edge = Vector3f.sub(left, right, null);
        Vector3f basePointToVertex = Vector3f.sub(right, basePoint, null);
        float edgeLengthSquared = edge.lengthSquared();
        float edgeDotVelocity = Vector3f.dot(edge, velocity);
        float edgeDotBaseToVertex = Vector3f.dot(edge, basePointToVertex);

        float a = edgeLengthSquared*-velocityLengthSquared + edgeDotVelocity*edgeDotVelocity;
        float b = edgeLengthSquared*(2.0f*Vector3f.dot(velocity, basePointToVertex)) - 2.0f*edgeDotVelocity*edgeDotBaseToVertex;
        float c = edgeLengthSquared*(1.0f - basePointToVertex.lengthSquared()) + edgeDotBaseToVertex*edgeDotBaseToVertex;
        Float x1 = CollisionMath.getLowestRootUnderThreshold(a, b, c, (float)temp.get().t);
        if (x1 != null) {
            float f = (edgeDotVelocity*x1 - edgeDotBaseToVertex) / edgeLengthSquared;
            if (f >= 0.0f && f <= 1.0f) {
                temp.get().foundCollision = true;
                temp.get().t = x1;
                temp.get().intersectionPoint = Vector3f.add(right, Mth.multiply(edge, f), null);
            }
        }
    }

    /**
     * Stores important data that are necessary for the response step. Stores only data for the closest
     * triangle, which is all that is needed for the response step.
     */
    private static class CollisionInfo {
        private boolean foundCollision;
        private float intersectionDistance;
        private Vector3f intersectionPoint;
        private Triangle collisionTriangle;
    }

    public static class DebugAccumulator {
        public int collisions;
        private int accumulator;
    }

    private static class TempCollisionInfo {
        private boolean foundCollision;
        private Vector3f intersectionPoint;
        private double t;
    }

}
