package com.james.common.simulation.collisionEngine.math;

import com.james.common.simulation.collisionEngine.prep.ModelMesh;
import org.lwjgl.util.vector.Vector3f;

/**
 * Math operations that can be performed on points. These are used mainly for collision calculations in
 * CommonCollisionProcedure. Also, if it is desired to perform an operation on many Triangle points,
 * the static method ModelMesh.performOperationOnAllTriangles() can be used.
 * @implNote If a method in this class has Dest appended to it, then that method does not return a new
 * Vector3f object, but rather sets the fields of an input Vector3f object. This is for efficiency.
 * @see CommonCollisionProcedure
 * @see ModelMesh
 */
public class PointOperations {

    public static Vector3f dividePointByEllipsoidRadius(Vector3f pointInR3, Vector3f ellipsoidRadius) {
        float ellipsoidSpaceX = pointInR3.x / ellipsoidRadius.x;
        float ellipsoidSpaceY = pointInR3.y / ellipsoidRadius.y;
        float ellipsoidSpaceZ = pointInR3.z / ellipsoidRadius.z;

        return new Vector3f(ellipsoidSpaceX, ellipsoidSpaceY, ellipsoidSpaceZ);
    }

    public static void dividePointByEllipsoidRadiusDest(Vector3f pointInR3, Vector3f ellipsoidRadius) {
        float ellipsoidSpaceX = pointInR3.x / ellipsoidRadius.x;
        float ellipsoidSpaceY = pointInR3.y / ellipsoidRadius.y;
        float ellipsoidSpaceZ = pointInR3.z / ellipsoidRadius.z;

        pointInR3.set(ellipsoidSpaceX, ellipsoidSpaceY, ellipsoidSpaceZ);
    }

    public static void multiplyPointByEllipsoidRadiusDest(Vector3f pointInEllipsoidSpace, Vector3f ellipsoidRadius) {
        float r3X = pointInEllipsoidSpace.x * ellipsoidRadius.x;
        float r3Y = pointInEllipsoidSpace.y * ellipsoidRadius.y;
        float r3Z = pointInEllipsoidSpace.z * ellipsoidRadius.z;

        pointInEllipsoidSpace.set(r3X, r3Y, r3Z);
    }

    public static Vector3f addObjectPositionToPoint(Vector3f pointInEllipsoidLocalSpace, Vector3f objectPosition) {
        float ellipsoidWorldSpaceX = pointInEllipsoidLocalSpace.x + objectPosition.x;
        float ellipsoidWorldSpaceY = pointInEllipsoidLocalSpace.y + objectPosition.y;
        float ellipsoidWorldSpaceZ = pointInEllipsoidLocalSpace.z + objectPosition.z;

        return new Vector3f(ellipsoidWorldSpaceX, ellipsoidWorldSpaceY, ellipsoidWorldSpaceZ);
    }

}
