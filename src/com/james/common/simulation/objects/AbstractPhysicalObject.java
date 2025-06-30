package com.james.common.simulation.objects;

import org.lwjgl.util.vector.Vector3f;

/**
 * A representation of an object in 3D space.
 */
public abstract class AbstractPhysicalObject {

    protected final Vector3f position;

    protected AbstractPhysicalObject(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() { return position; }

    public void translate(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }


}
