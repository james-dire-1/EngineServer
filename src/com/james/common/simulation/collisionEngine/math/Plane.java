package com.james.common.simulation.collisionEngine.math;

import org.lwjgl.util.vector.Vector3f;

/**
 * Plane class used for collision calculations in CommonCollisionProcedure.
 * Note: this class is implemented from Fauerby's report:
 * http://www.peroxide.dk/papers/collision/collision.pdf
 * @see CommonCollisionProcedure
 */
public class Plane {

    public final Vector3f origin;
    public final Vector3f normal;
    private final float equation;

    /**
     * Constructs a plane from its origin and normal.
     */
    public Plane(Vector3f origin, Vector3f normal) {
        this.origin = origin;
        this.normal = normal;

        this.equation = -(normal.x*origin.x + normal.y*origin.y + normal.z*origin.z);
    }

    /**
     * Constructs a plane from triangle points.
     */
    public Plane(Vector3f p1, Vector3f p2, Vector3f p3) {
        this.origin = p1;
        this.normal = Vector3f.cross(Vector3f.sub(p2, p1, null), Vector3f.sub(p3, p1, null), null);
        this.normal.normalise(this.normal);

        this.equation = -(normal.x*origin.x + normal.y*origin.y + normal.z*origin.z);
    }

    public boolean isFrontFacingTo(Vector3f direction) {
        double dot = Vector3f.dot(normal, direction);
        return dot <= 0;
    }

    public double signedDistanceTo(Vector3f point) {
        return Vector3f.dot(point, normal) + equation;
    }

}
