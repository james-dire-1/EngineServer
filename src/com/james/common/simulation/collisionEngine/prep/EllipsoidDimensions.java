package com.james.common.simulation.collisionEngine.prep;

import com.james.serverSide.simulation.collisionEngine.hitboxes.EllipsoidHitbox;
import com.james.common.simulation.collisionEngine.math.PointOperations;
import com.james.common.simulation.collisionEngine.math.Triangle;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unique dimensions for an EllipsoidHitbox. This also stores all ModelMeshes whose Triangles will be used in
 * the narrow phase collision test, but in ellipsoid space, since this is necessary for the narrow phase
 * algorithm. (This is why each EllipsoidDimensions object stores ModelMeshes, since they will be unique for
 * each kind of ellipsoid.)
 * @see EllipsoidHitbox
 */
public class EllipsoidDimensions {

    public final Vector3f radius;

    public final Map<String, List<ModelMesh>> modelMeshMap = new HashMap<>();

    /**
     * Private constructor that creates a new EllipsoidDimensions of a given radius, and which gets all
     * the ModelMeshes in R3, converts them to ellipsoid space, and stores them in the map.
     *
     * @implNote Notice how modelMeshMap here is of the exact same type as modelMeshMap in ModelMeshBankInR3.
     */
    private EllipsoidDimensions(float radiusX, float radiusY, float radiusZ) {
        this.radius = new Vector3f(radiusX, radiusY, radiusZ);

        for (Map.Entry<String, List<ModelMesh>> outerEntry : ModelMeshBankInR3.getModelMeshMap().entrySet()) {
            String meshPath = outerEntry.getKey();
            List<ModelMesh> innerList = outerEntry.getValue();

            if (!modelMeshMap.containsKey(meshPath)) {
                modelMeshMap.put(meshPath, new ArrayList<>());
            }

            for (ModelMesh modelMeshInR3 : innerList) {
                ModelMesh modelMeshInEllipsoidSpace = convertFromR3ToEllipsoidSpace(modelMeshInR3);
                modelMeshMap.get(meshPath).add(modelMeshInEllipsoidSpace);
            }
        }
    }

    /**
     * Converts a ModelMesh from R3 local space, to ellipsoid local space. To do this, a static method
     * in ModelMesh is called.
     * @see ModelMesh
     */
    private ModelMesh convertFromR3ToEllipsoidSpace(ModelMesh modelMeshInR3) {
        Triangle[] trianglesInR3 = modelMeshInR3.triangles;
        Triangle[] trianglesInEllipsoidSpace = ModelMesh.performOperationOnAllTriangles(trianglesInR3, PointOperations::dividePointByEllipsoidRadius, radius);

        return new ModelMesh(trianglesInEllipsoidSpace);
    }

    /**
     * Sort of an equals() method that checks to see if the current EllipsoidDimensions has the given
     * radius. This is used when retrieving an EllipsoidDimensions (see below).
     */
    private boolean isOfRadius(float radiusX, float radiusY, float radiusZ) {
        return (radiusX == radius.x) && (radiusY == radius.y) && (radiusZ == radius.z);
    }

    private static final List<EllipsoidDimensions> ellipsoidDimensionsBank = new ArrayList<>();

    /**
     * Creates all the EllipsoidDimensions that are necessary for the game, right away (they cannot be
     * created later on, since the constructor is private).
     * @param radii the inner array's elements represent the x, y, and z coordinates for the radius of
     *              the EllipsoidDimensions that will be created, and the outer array is to pass in
     *              multiple sets of x, y, and z coordinates
     */
    public static void init(float[][] radii) {
        for (float[] radius : radii) {
            ellipsoidDimensionsBank.add(new EllipsoidDimensions(radius[0], radius[1], radius[2]));
        }
    }

    public static EllipsoidDimensions get(float radiusX, float radiusY, float radiusZ) {
        for (EllipsoidDimensions dimensions : ellipsoidDimensionsBank) {
            if (dimensions.isOfRadius(radiusX, radiusY, radiusZ)) {
                return dimensions;
            }
        }

        return null;
    }

}
