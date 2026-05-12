package com.james.serverSide.simulation.objects;

import com.james.common.simulation.objects.AbstractPhysicalObject;
import templates.common.simulation.objects.PhysicalObjectType;
import org.lwjgl.util.vector.Vector3f;

/**
 * A representation of an object in 3D space that exists on the server side.
 */
public class PhysicalObject extends AbstractPhysicalObject {

    public final int id;
    public final PhysicalObjectType type;

    private final Vector3f rotation;
    private float scale;

    public Vector3f getRotation() { return rotation; }
    public float getScale() { return scale; }

    private boolean shouldDelete = false;

    private static int count;

    public PhysicalObject(PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale) {
        super(position);
        this.type = type;
        this.rotation = rotation;
        this.scale = scale;

        count++;
        this.id = count;
    }

    /**
     * Basic update method for PhysicalObjects. As you can see, this method is pretty barebones, but classes
     * that extend PhysicalObject (such as MovableObject) may have more complex overloaded update() methods.
     * @see MovableObject
     * @return whether the current PhysicalObject should be deleted.
     */
    public boolean update() {
        return shouldDelete;
    }

    protected void markForDeletion() {
        shouldDelete = true;
    }

    public void rotate(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

}
