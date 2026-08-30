package com.james.common.simulation.collisionEngine.hitboxes;

import com.james.common.simulation.objects.AbstractPhysicalObject;
import com.james.common.simulation.collisionEngine.math.CommonCollisionProcedure;
import com.james.common.simulation.collisionEngine.prep.ModelMesh;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import org.lwjgl.util.vector.Vector3f;

/**
 * An AABB hitbox used to approximate objects that are formed by a triangle mesh. One or more instances of
 * this class exist for each AbstractPhysicalObject that we would like to be able to collide with. In the
 * CommonCollisionProcedure, these AABBs are tested against each EllipsoidHitbox. If an EllipsoidHitbox is
 * deemed to be colliding with an AABB, then the broadphase check has passed and the narrow phase test can
 * begin.
 */
public abstract class AbstractAABBHitbox {

    public final AbstractPhysicalObject object;
    public final String meshPath;
    public final int subMeshIdentifier;
    public final ModelMesh mesh;

    public float lowerX, upperX, lowerY, upperY, lowerZ, upperZ;

    public boolean activeToEllipsoids = true;
    public boolean activeToRays = true;

    /**
     * Creates a new AABB hitbox for a specific object (AbstractPhysicalObject), which uses a specific
     * ModelMesh, denoted by its file path and sub mesh identifier. Also, calls updatePosition() to set the
     * bounds of this AABB.
     */
    protected AbstractAABBHitbox(AbstractPhysicalObject object, String meshPath, int subMeshIdentifier) {
        this.object = object;
        this.meshPath = meshPath;
        this.subMeshIdentifier = subMeshIdentifier;
        this.mesh = ModelMeshBankInR3.getModelMeshMap().get(meshPath).get(subMeshIdentifier);

        updatePosition();
    }

    /**
     * Updates the bounds of the AABB, which are in R3 world space. This is done as soon as the AABB is
     * created, but can also be done later on in the AABB's lifetime (e.g. if the object of this AABB moves).
     * To determine these world space bounds, the minimum and maximum coordinate values from the mesh in R3
     * local space are needed. These bounds are used in the CommonCollisionProcedure.
     * @see CommonCollisionProcedure
     */
    public void updatePosition() {
        Vector3f objectPosition = object.getPosition();

        lowerX = objectPosition.x + mesh.minX;
        upperX = objectPosition.x + mesh.maxX;
        lowerY = objectPosition.y + mesh.minY;
        upperY = objectPosition.y + mesh.maxY;
        lowerZ = objectPosition.z + mesh.minZ;
        upperZ = objectPosition.z + mesh.maxZ;
    }

}
