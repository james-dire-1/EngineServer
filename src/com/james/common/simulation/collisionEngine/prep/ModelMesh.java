package com.james.common.simulation.collisionEngine.prep;

import com.james.common.simulation.collisionEngine.hitboxes.AbstractAABBHitbox;
import com.james.common.simulation.collisionEngine.math.Triangle;
import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import org.lwjgl.util.vector.Vector3f;

/**
 * Represents a triangle collision mesh. Is used for models in R3 local space, and in ellipsoid local
 * space. Contains minimum and maximum coordinate values (in R3 local space) to be used in the
 * AbstractAABBHitbox class for calculating the AABB bounds in R3 world space.
 * @see AbstractAABBHitbox
 */
public class ModelMesh {

    public Triangle[] triangles;
    public float minX, maxX, minY, maxY, minZ, maxZ;

    /**
     * Constructor for models in R3 local space, which populates the triangles array and sets up the
     * minimum and maximum coordinate values. These ModelMeshes are used by AbstractAABBHitboxes.
     */
    public ModelMesh(float[] vertexPositions, int[] indices) {
        // not divisible by three
        if (indices.length % 3 != 0) throw new RuntimeException();

        int triangleCount = indices.length / 3;
        triangles = new Triangle[triangleCount];

        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;

        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;

        int count = 0;
        for (int i = 0; i < indices.length; i += 3) {
            int indexPointA = indices[i];
            int indexPointB = indices[i + 1];
            int indexPointC = indices[i + 2];

            Vector3f pointA = processPoint(indexPointA, vertexPositions);
            Vector3f pointB = processPoint(indexPointB, vertexPositions);
            Vector3f pointC = processPoint(indexPointC, vertexPositions);

            Triangle triangle = new Triangle(new Vector3f[]{pointA, pointB, pointC});
            triangles[count] = triangle;

            count++;
        }
    }

    /**
     * Constructor that accepts a triangle array, which is in ellipsoid local space. This is used when
     * an EllipsoidDimensions needs to convert models from R3 to ellipsoid space, to add to its map.
     * @implNote Note that this constructor does not set the minimum and maximum coordinate values,
     * since they are not needed for the ModelMeshes that are in the EllipsoidDimensions class.
     * @see EllipsoidDimensions
     */
    public ModelMesh(Triangle[] triangles) {
        this.triangles = triangles;
    }

    /**
     * Extracts x, y, and z coordinates from the vertexPositions array, to create a new Vector3f object.
     * Also, assigns the minimum and maximum coordinate values.
     */
    private Vector3f processPoint(int indexForPoint, float[] vertexPositions) {
        float xCoord = vertexPositions[indexForPoint*3];
        float yCoord = vertexPositions[indexForPoint*3 + 1];
        float zCoord = vertexPositions[indexForPoint*3 + 2];

        if (xCoord < minX) minX = xCoord;
        if (xCoord > maxX) maxX = xCoord;
        if (yCoord < minY) minY = yCoord;
        if (yCoord > maxY) maxY = yCoord;
        if (zCoord < minZ) minZ = zCoord;
        if (zCoord > maxZ) maxZ = zCoord;

        return new Vector3f(xCoord, yCoord, zCoord);
    }

    /**
     * A static method that is used for performing a math operation on all points of all the triangles
     * from the input array.
     * @return a new array whose triangles have the newly calculated points
     */
    public static Triangle[] performOperationOnAllTriangles(Triangle[] inputTriangles, Operation operation, Vector3f other) {
        Triangle[] outputTriangles = new Triangle[inputTriangles.length];

        for (int i = 0; i < inputTriangles.length; i++) {
            Vector3f[] inputPoints = inputTriangles[i].points;
            Vector3f[] outputPoints = new Vector3f[3];

            for (int j = 0; j <= 2; j++) {
                Vector3f inputPoint = inputPoints[j];
                Vector3f outputPoint = operation.perform(inputPoint, other);

                outputPoints[j] = outputPoint;
            }

            outputTriangles[i] = new Triangle(outputPoints);
        }

        return outputTriangles;
    }

    public interface Operation {
        Vector3f perform(Vector3f inputPoint, Vector3f other);
    }

}
