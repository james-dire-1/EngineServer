package com.james.common.simulation.collisionEngine.math;

import org.lwjgl.util.vector.Vector3f;

public class CollisionMath {

    // Note: this method is implemented from Fauerby's report:
    // http://www.peroxide.dk/papers/collision/collision.pdf
    public static boolean pointInTriangle(Vector3f point, Vector3f pa, Vector3f pb, Vector3f pc) {
        Vector3f e10 = Vector3f.sub(pb, pa, null);
        Vector3f e20 = Vector3f.sub(pc, pa, null);

        float a = Vector3f.dot(e10, e10);
        float b = Vector3f.dot(e10, e20);
        float c = Vector3f.dot(e20, e20);
        float ac_bb = a*c - b*b;
        Vector3f vp = new Vector3f(point.x - pa.x, point.y - pa.y, point.z - pa.z);

        float d = Vector3f.dot(vp, e10);
        float e = Vector3f.dot(vp, e20);
        float x = d*c - e*b;
        float y = e*a - d*b;
        float z = x + y - ac_bb;

        return (( Float.floatToIntBits(z) & (~( Float.floatToIntBits(x) | Float.floatToIntBits(y) )) ) & 0x80000000) != 0;
    }

    // Note: this method is implemented from Fauerby's report:
    // http://www.peroxide.dk/papers/collision/collision.pdf
    public static Float getLowestRootUnderThreshold(float a, float b, float c, float maxR) {
        float determinant = b * b - 4.0f * a * c;
        if (determinant < 0.0f) return null;

        float sqrtOfDeterminant = (float) Math.sqrt(determinant);
        float r1 = (-b - sqrtOfDeterminant) / (2.0f * a);
        float r2 = (-b + sqrtOfDeterminant) / (2.0f * a);

        if (r1 > r2) {
            float temp = r2;
            r2 = r1;
            r1 = temp;
        }

        if (r1 > 0 && r1 < maxR) {
            return r1;
        }

        if (r2 > 0 && r2 < maxR) {
            return r2;
        }

        return null;
    }

}
