package com.james.common.simulation.collisionEngine.math;

import org.lwjgl.util.vector.Vector3f;

/**
 * Triangle class used as the most basic unit for a mesh. Includes its 3 points that define it.
 * These can be in any vector space.
 */
public class Triangle {

    public final Vector3f[] points;

    public Triangle(Vector3f[] points) {
        if (points.length != 3) throw new RuntimeException();

        this.points = points;
    }

    @Override
    public String toString() {
        return points[0].toString() + " | " + points[1].toString() + " | " + points[2].toString();
    }

}
